package com.billwise.service;

import com.billwise.domain.Expense;
import com.billwise.domain.ExpenseGroup;
import com.billwise.domain.ExpenseShare;
import com.billwise.domain.GroupMember;
import com.billwise.domain.User;
import com.billwise.repository.ExpenseGroupRepository;
import com.billwise.repository.ExpenseRepository;
import com.billwise.repository.ExpenseShareRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BalanceServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ExpenseGroupRepository groupRepository;

    @Mock
    private GroupMemberRepository memberRepository;

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private ExpenseShareRepository shareRepository;

    @InjectMocks
    private BalanceService balanceService;

    @Test
    void calculatesBalancesFromPaidAmountAndShares() {
        User alice = mock(User.class);
        User bob = mock(User.class);
        User currentUser = alice;

        ExpenseGroup group = mock(ExpenseGroup.class);
        GroupMember aliceMembership = mock(GroupMember.class);
        GroupMember bobMembership = mock(GroupMember.class);

        Expense expense = mock(Expense.class);
        ExpenseShare aliceShare = mock(ExpenseShare.class);
        ExpenseShare bobShare = mock(ExpenseShare.class);

        when(alice.getId()).thenReturn(1L);
        when(alice.getUsername()).thenReturn("alice");

        when(bob.getId()).thenReturn(2L);
        when(bob.getUsername()).thenReturn("bob");

        when(aliceMembership.getUser())
                .thenReturn(alice);

        when(bobMembership.getUser())
                .thenReturn(bob);

        when(userRepository.findByUsername("alice"))
                .thenReturn(Optional.of(currentUser));

        when(groupRepository.findById(10L))
                .thenReturn(Optional.of(group));

        when(memberRepository.existsByGroupIdAndUserId(
                10L,
                1L)).thenReturn(true);

        when(memberRepository.findByGroupIdOrderByJoinedAtAsc(
                10L)).thenReturn(List.of(
                        aliceMembership,
                        bobMembership));

        when(expenseRepository.findByGroupIdOrderByExpenseDateDesc(
                10L)).thenReturn(List.of(expense));

        when(expense.getId()).thenReturn(100L);
        when(expense.getPayer()).thenReturn(alice);
        when(expense.getAmount())
                .thenReturn(new BigDecimal("20.00"));

        when(aliceShare.getParticipant())
                .thenReturn(alice);
        when(aliceShare.getShareAmount())
                .thenReturn(new BigDecimal("10.00"));

        when(bobShare.getParticipant())
                .thenReturn(bob);
        when(bobShare.getShareAmount())
                .thenReturn(new BigDecimal("10.00"));

        when(shareRepository.findByExpenseId(100L))
                .thenReturn(List.of(
                        aliceShare,
                        bobShare));

        var balances = balanceService.calculateBalances(
                10L,
                "alice");

        assertThat(balances).hasSize(2);

        assertThat(balances.get(0).username())
                .isEqualTo("alice");

        assertThat(balances.get(0).balance())
                .isEqualByComparingTo("10.00");

        assertThat(balances.get(1).username())
                .isEqualTo("bob");

        assertThat(balances.get(1).balance())
                .isEqualByComparingTo("-10.00");
    }
}