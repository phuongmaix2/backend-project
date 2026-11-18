package com.billwise.repository;

import com.billwise.domain.Expense;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Database access for personal and group expenses. */

public interface ExpenseRepository
        extends JpaRepository<Expense, Long> {

    List<Expense> findByCreatorIdAndGroupIsNullOrderByExpenseDateDesc(
            Long creatorId);

    List<Expense> findByGroupIdOrderByExpenseDateDesc(
            Long groupId);

    List<Expense> findByPayerIdOrderByExpenseDateDesc(
            Long payerId);
}
