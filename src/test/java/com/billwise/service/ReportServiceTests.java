package com.billwise.service;

import com.billwise.domain.Expense;
import com.billwise.domain.ExpenseCategory;
import com.billwise.domain.ExpenseGroup;
import com.billwise.domain.User;
import com.billwise.repository.ExpenseGroupRepository;
import com.billwise.repository.ExpenseRepository;
import com.billwise.repository.GroupMemberRepository;
import com.billwise.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ExpenseGroupRepository groupRepository;

    @Mock
    private GroupMemberRepository memberRepository;

    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private ReportService reportService;

    @Test
    void calculatesSpendingTotalsByCategory() {
        User user = mock(User.class);
        ExpenseGroup group = mock(ExpenseGroup.class);
        Expense foodExpense = mock(Expense.class);
        Expense transportExpense = mock(Expense.class);

        when(user.getId()).thenReturn(1L);

        when(userRepository.findByUsername("alice"))
                .thenReturn(Optional.of(user));

        when(groupRepository.findById(10L))
                .thenReturn(Optional.of(group));

        when(memberRepository.existsByGroupIdAndUserId(
                10L,
                1L)).thenReturn(true);

        when(foodExpense.getCategory())
                .thenReturn(ExpenseCategory.FOOD);

        when(foodExpense.getAmount())
                .thenReturn(new BigDecimal("30.00"));

        when(transportExpense.getCategory())
                .thenReturn(ExpenseCategory.TRANSPORT);

        when(transportExpense.getAmount())
                .thenReturn(new BigDecimal("20.00"));

        when(expenseRepository.findByGroupIdOrderByExpenseDateDesc(
                10L)).thenReturn(List.of(
                        foodExpense,
                        transportExpense));

        var report = reportService.spendingByCategory(
                10L,
                "alice");

        assertThat(report).hasSize(2);

        assertThat(report.get(0).label())
                .isEqualTo("FOOD");

        assertThat(report.get(0).total())
                .isEqualByComparingTo("30.00");

        assertThat(report.get(1).label())
                .isEqualTo("TRANSPORT");

        assertThat(report.get(1).total())
                .isEqualByComparingTo("20.00");
    }
}