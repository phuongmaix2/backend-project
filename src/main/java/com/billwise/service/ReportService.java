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
import com.billwise.web.dto.report.SpendingReportItem;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Produces read-only spending summaries from persisted group expenses. */
@Service
public class ReportService {

    private final UserRepository userRepository;
    private final ExpenseGroupRepository groupRepository;
    private final GroupMemberRepository memberRepository;
    private final ExpenseRepository expenseRepository;

    public ReportService(
            UserRepository userRepository,
            ExpenseGroupRepository groupRepository,
            GroupMemberRepository memberRepository,
            ExpenseRepository expenseRepository) {

        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.memberRepository = memberRepository;
        this.expenseRepository = expenseRepository;
    }

    /** Groups expense totals by category for the authenticated group member. */
    @Transactional(readOnly = true)
    public List<SpendingReportItem> spendingByCategory(
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

        List<Expense> expenses = expenseRepository
                .findByGroupIdOrderByExpenseDateDesc(
                        groupId);

        Map<String, BigDecimal> totals = new LinkedHashMap<>();

        for (Expense expense : expenses) {
            String category = expense.getCategory().name();

            totals.merge(
                    category,
                    expense.getAmount(),
                    BigDecimal::add);
        }

        return totals.entrySet()
                .stream()
                .map(entry -> new SpendingReportItem(
                        entry.getKey(),
                        entry.getValue()))
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
