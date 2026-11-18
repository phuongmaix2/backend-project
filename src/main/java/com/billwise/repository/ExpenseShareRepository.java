package com.billwise.repository;

import com.billwise.domain.ExpenseShare;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Database access for participant share snapshots. */

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
