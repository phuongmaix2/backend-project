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

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final ExpenseGroupRepository groupRepository;
    private final GroupMemberRepository memberRepository;

    public ExpenseService(
            ExpenseRepository expenseRepository,
            UserRepository userRepository,
            ExpenseGroupRepository groupRepository,
            GroupMemberRepository memberRepository) {

        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.memberRepository = memberRepository;
    }

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
                request.getCategory(),
                request.getSplitMethod());

        return ExpenseResponse.from(
                expenseRepository.save(expense));
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