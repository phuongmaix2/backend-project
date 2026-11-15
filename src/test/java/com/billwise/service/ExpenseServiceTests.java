package com.billwise.service;

import com.billwise.domain.Expense;
import com.billwise.domain.ExpenseCategory;
import com.billwise.domain.ExpenseGroup;
import com.billwise.domain.User;
import com.billwise.domain.SplitMethod;
import com.billwise.repository.ExpenseGroupRepository;
import com.billwise.repository.ExpenseRepository;
import com.billwise.repository.GroupMemberRepository;
import com.billwise.repository.UserRepository;
import com.billwise.web.dto.expense.ExpenseRequest;
import com.billwise.exception.ForbiddenOperationException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTests {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ExpenseGroupRepository groupRepository;

    @Mock
    private GroupMemberRepository memberRepository;

    @InjectMocks
    private ExpenseService expenseService;

    @Test
    void createsPersonalExpense() {
        User user = new User(
                "alice",
                "alice@example.com",
                "encoded-password");

        when(userRepository.findByUsername("alice"))
                .thenReturn(Optional.of(user));

        when(expenseRepository.save(any(Expense.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ExpenseRequest request = new ExpenseRequest();

        request.setDescription("Lunch");
        request.setAmount(new BigDecimal("25.50"));
        request.setCurrency("EUR");
        request.setExpenseDate(LocalDate.of(2026, 11, 15));
        request.setCategory(ExpenseCategory.FOOD);
        request.setSplitMethod(SplitMethod.EQUAL);

        var response = expenseService.createExpense(
                "alice",
                request);

        assertThat(response.description())
                .isEqualTo("Lunch");

        assertThat(response.amount())
                .isEqualByComparingTo("25.50");

        assertThat(response.currency())
                .isEqualTo("EUR");

        assertThat(response.groupId())
                .isNull();

        assertThat(response.creatorUsername())
                .isEqualTo("alice");

        assertThat(response.payerUsername())
                .isEqualTo("alice");
    }

    @Test
    void createsGroupExpenseForGroupMember() {
        User user = mock(User.class);
        ExpenseGroup group = mock(ExpenseGroup.class);

        when(user.getId()).thenReturn(1L);
        when(user.getUsername()).thenReturn("alice");

        when(group.getId()).thenReturn(10L);

        when(userRepository.findByUsername("alice"))
                .thenReturn(Optional.of(user));

        when(groupRepository.findById(10L))
                .thenReturn(Optional.of(group));

        when(memberRepository.existsByGroupIdAndUserId(
                10L,
                1L)).thenReturn(true);

        when(expenseRepository.save(any(Expense.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ExpenseRequest request = new ExpenseRequest();

        request.setGroupId(10L);
        request.setDescription("Dinner");
        request.setAmount(new BigDecimal("60.00"));
        request.setCurrency("EUR");
        request.setExpenseDate(
                LocalDate.of(2026, 11, 15));
        request.setCategory(ExpenseCategory.FOOD);
        request.setSplitMethod(SplitMethod.EQUAL);

        var response = expenseService.createExpense(
                "alice",
                request);

        assertThat(response.groupId())
                .isEqualTo(10L);

        assertThat(response.description())
                .isEqualTo("Dinner");

        assertThat(response.amount())
                .isEqualByComparingTo("60.00");

        assertThat(response.payerUsername())
                .isEqualTo("alice");
    }

    @Test
    void rejectsGroupExpenseForNonMember() {
        User user = mock(User.class);
        ExpenseGroup group = mock(ExpenseGroup.class);

        when(user.getId()).thenReturn(1L);
        when(group.getId()).thenReturn(10L);

        when(userRepository.findByUsername("alice"))
                .thenReturn(Optional.of(user));

        when(groupRepository.findById(10L))
                .thenReturn(Optional.of(group));

        when(memberRepository.existsByGroupIdAndUserId(
                10L,
                1L)).thenReturn(false);

        ExpenseRequest request = new ExpenseRequest();
        request.setGroupId(10L);

        assertThatThrownBy(() -> expenseService.createExpense(
                "alice",
                request))
                .isInstanceOf(
                        ForbiddenOperationException.class)
                .hasMessage(
                        "You are not a member of this group");
    }
}