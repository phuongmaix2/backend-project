package com.billwise.service;

import com.billwise.domain.Expense;
import com.billwise.domain.ExpenseGroup;
import com.billwise.domain.User;
import com.billwise.exception.ForbiddenOperationException;
import com.billwise.exception.ResourceNotFoundException;
import com.billwise.repository.ExpenseGroupRepository;
import com.billwise.repository.ExpenseRepository;
import com.billwise.repository.GroupMemberRepository;
import com.billwise.repository.UserRepository;
import com.billwise.web.dto.expense.ExpenseRequest;
import com.billwise.web.dto.expense.ExpenseResponse;
import com.billwise.repository.ExpenseShareRepository;
import com.billwise.domain.ExpenseShare;
import com.billwise.exception.ConflictException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

/** Applies expense validation and creates 
 * the immutable share records used by reports and balances. */
@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final ExpenseGroupRepository groupRepository;
    private final GroupMemberRepository memberRepository;
    private final ExpenseShareRepository expenseShareRepository;

    public ExpenseService(
            ExpenseRepository expenseRepository,
            UserRepository userRepository,
            ExpenseGroupRepository groupRepository,
            GroupMemberRepository memberRepository,
            ExpenseShareRepository expenseShareRepository) {
        {

            this.expenseRepository = expenseRepository;
            this.userRepository = userRepository;
            this.groupRepository = groupRepository;
            this.memberRepository = memberRepository;
            this.expenseShareRepository = expenseShareRepository;
        }
    }

    /** Creates a personal or group expense and 
     * its participant shares atomically. */
    @Transactional
    public ExpenseResponse createExpense(
            String currentUsername,
            ExpenseRequest request) {

        User creator = findUser(currentUsername);

        ExpenseGroup group = null;

        if (request.getGroupId() != null) {
            group = findGroup(request.getGroupId());

            requireMember(group.getId(), creator.getId());
        }

        User payer = resolvePayer(
                currentUsername,
                request.getPayerUsernameOrEmail());

        if (group == null) {
            if (!Objects.equals(
                    payer.getId(),
                    creator.getId())) {
                throw new ForbiddenOperationException(
                        "A personal expense can only belong to you");
            }
        } else {
            requireMember(
                    group.getId(),
                    payer.getId());
        }

        Expense expense = new Expense(
                group,
                creator,
                payer,
                request.getDescription(),
                request.getAmount(),
                request.getCurrency(),
                request.getExpenseDate(),
                request.getCategory());

        Expense savedExpense = expenseRepository.save(expense);

        List<User> participants = resolveParticipants(
                group,
                creator,
                request.getParticipantUsernamesOrEmails());

        expenseShareRepository.saveAll(
                buildEqualShares(
                        savedExpense,
                        participants));

        return ExpenseResponse.from(savedExpense);
    }

    @Transactional(readOnly = true)
    public List<ExpenseResponse> findPersonalExpenses(
            String currentUsername) {

        User user = findUser(currentUsername);

        return expenseRepository
                .findByCreatorIdAndGroupIsNullOrderByExpenseDateDesc(
                        user.getId())
                .stream()
                .map(ExpenseResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ExpenseResponse> findGroupExpenses(
            Long groupId,
            String currentUsername) {

        User user = findUser(currentUsername);

        findGroup(groupId);

        requireMember(groupId, user.getId());

        return expenseRepository
                .findByGroupIdOrderByExpenseDateDesc(groupId)
                .stream()
                .map(ExpenseResponse::from)
                .toList();
    }

    private List<User> resolveParticipants(
            ExpenseGroup group,
            User creator,
            List<String> participantValues) {

        if (group == null) {
            return new ArrayList<>(
                    List.of(creator));
        }

        if (participantValues == null
                || participantValues.isEmpty()) {

            throw new ConflictException(
                    "At least one participant is required");
        }

        List<User> participants = new ArrayList<>();
        Set<Long> participantIds = new HashSet<>();

        for (String participantValue : participantValues) {
            User participant = findUserByUsernameOrEmail(
                    participantValue);

            requireMember(
                    group.getId(),
                    participant.getId());

            if (!participantIds.add(participant.getId())) {
                throw new ConflictException(
                        "Duplicate participant");
            }

            participants.add(participant);
        }

        return participants;
    }

    /**
     * Splits cents exactly. Any remainder is assigned by ascending user id,
     * making the result deterministic and ensuring shares equal the total.
     */
    private List<ExpenseShare> buildEqualShares(
            Expense expense,
            List<User> participants) {

        participants.sort(
                Comparator.comparing(
                        User::getId,
                        Comparator.nullsLast(
                                Comparator.naturalOrder())));

        BigDecimal total = expense.getAmount()
                .setScale(2, RoundingMode.HALF_UP);

        long totalCents = total.movePointRight(2)
                .longValueExact();

        long baseCents = totalCents / participants.size();

        long remainingCents = totalCents % participants.size();

        List<ExpenseShare> shares = new ArrayList<>();

        for (int index = 0; index < participants.size(); index++) {

            long participantCents = baseCents
                    + (index < remainingCents ? 1 : 0);

            BigDecimal shareAmount = BigDecimal.valueOf(participantCents)
                    .movePointLeft(2);

            shares.add(
                    new ExpenseShare(
                            expense,
                            participants.get(index),
                            shareAmount));
        }

        return shares;
    }

    private User resolvePayer(
            String currentUsername,
            String payerUsernameOrEmail) {

        if (payerUsernameOrEmail == null
                || payerUsernameOrEmail.isBlank()) {

            return findUser(currentUsername);
        }

        return findUserByUsernameOrEmail(
                payerUsernameOrEmail);
    }

    private void requireMember(
            Long groupId,
            Long userId) {

        boolean isMember = memberRepository.existsByGroupIdAndUserId(
                groupId,
                userId);

        if (!isMember) {
            throw new ForbiddenOperationException(
                    "You are not a member of this group");
        }
    }

    private User findUser(String username) {
        String normalizedUsername = username.trim().toLowerCase(Locale.ROOT);

        return userRepository
                .findByUsername(normalizedUsername)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found"));
    }

    private User findUserByUsernameOrEmail(
            String usernameOrEmail) {

        String normalizedValue = usernameOrEmail
                .trim()
                .toLowerCase(Locale.ROOT);

        if (normalizedValue.contains("@")) {
            return userRepository
                    .findByEmail(normalizedValue)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "User not found"));
        }

        return findUser(normalizedValue);
    }

    private ExpenseGroup findGroup(Long groupId) {
        return groupRepository
                .findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Group not found"));
    }
}
