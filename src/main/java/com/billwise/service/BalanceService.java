package com.billwise.service;

import com.billwise.domain.Expense;
import com.billwise.domain.ExpenseGroup;
import com.billwise.domain.ExpenseShare;
import com.billwise.domain.GroupMember;
import com.billwise.domain.User;
import com.billwise.exception.ForbiddenOperationException;
import com.billwise.exception.ResourceNotFoundException;
import com.billwise.repository.ExpenseGroupRepository;
import com.billwise.repository.ExpenseRepository;
import com.billwise.repository.ExpenseShareRepository;
import com.billwise.repository.GroupMemberRepository;
import com.billwise.repository.UserRepository;
import com.billwise.web.dto.balance.BalanceResponse;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class BalanceService {

    private final UserRepository userRepository;
    private final ExpenseGroupRepository groupRepository;
    private final GroupMemberRepository memberRepository;
    private final ExpenseRepository expenseRepository;
    private final ExpenseShareRepository shareRepository;

    public BalanceService(
            UserRepository userRepository,
            ExpenseGroupRepository groupRepository,
            GroupMemberRepository memberRepository,
            ExpenseRepository expenseRepository,
            ExpenseShareRepository shareRepository) {

        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.memberRepository = memberRepository;
        this.expenseRepository = expenseRepository;
        this.shareRepository = shareRepository;
    }

    @Transactional(readOnly = true)
    public List<BalanceResponse> calculateBalances(
            Long groupId,
            String currentUsername) {

        User currentUser = findUser(currentUsername);
        findGroup(groupId);

        boolean isMember = memberRepository.existsByGroupIdAndUserId(
                groupId,
                currentUser.getId());

        if (!isMember) {
            throw new ForbiddenOperationException(
                    "You are not a member of this group");
        }

        List<GroupMember> members = memberRepository
                .findByGroupIdOrderByJoinedAtAsc(groupId);

        Map<Long, BigDecimal> balances = new HashMap<>();

        for (GroupMember member : members) {
            balances.put(
                    member.getUser().getId(),
                    BigDecimal.ZERO);
        }

        List<Expense> expenses = expenseRepository
                .findByGroupIdOrderByExpenseDateDesc(
                        groupId);

        for (Expense expense : expenses) {
            Long payerId = expense.getPayer().getId();

            balances.put(
                    payerId,
                    balances.get(payerId)
                            .add(expense.getAmount()));

            List<ExpenseShare> shares = shareRepository.findByExpenseId(
                    expense.getId());

            for (ExpenseShare share : shares) {
                Long participantId = share.getParticipant().getId();

                balances.put(
                        participantId,
                        balances.get(participantId)
                                .subtract(
                                        share.getShareAmount()));
            }
        }

        return members.stream()
                .map(member -> {
                    User user = member.getUser();

                    return new BalanceResponse(
                            user.getId(),
                            user.getUsername(),
                            balances.get(user.getId()));
                })
                .toList();
    }

    private User findUser(String username) {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found"));
    }

    private ExpenseGroup findGroup(Long groupId) {
        return groupRepository
                .findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Group not found"));
    }
}