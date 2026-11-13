package com.billwise.repository;

import com.billwise.domain.ExpenseGroup;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExpenseGroupRepository
        extends JpaRepository<ExpenseGroup, Long> {

    @Query("""
            SELECT membership.group
            FROM GroupMember membership
            WHERE membership.user.id = :userId
            ORDER BY membership.group.createdAt DESC
            """)
    List<ExpenseGroup> findAllForUser(
            @Param("userId") Long userId);
}