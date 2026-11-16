package com.billwise.repository;

import com.billwise.domain.ExpenseShare;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseShareRepository
        extends JpaRepository<ExpenseShare, Long> {

    List<ExpenseShare> findByExpenseId(
            Long expenseId
    );

    List<ExpenseShare> findByParticipantId(
            Long participantId
    );

    void deleteByExpenseId(Long expenseId);
}