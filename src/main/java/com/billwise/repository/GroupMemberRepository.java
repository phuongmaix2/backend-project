package com.billwise.repository;

import com.billwise.domain.GroupMember;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/** Database queries used to enforce group membership and owner rules. */

public interface GroupMemberRepository
    extends JpaRepository<GroupMember, Long> {

    List<GroupMember> findByGroupIdOrderByJoinedAtAsc(
        Long groupId
    );

    Optional<GroupMember> findByGroupIdAndUserId(
        Long groupId,
        Long userId
    );

    boolean existsByGroupIdAndUserId(
        Long groupId,
        Long userId
    );

    void deleteByGroupIdAndUserId(
        Long groupId,
        Long userId
    );
}
