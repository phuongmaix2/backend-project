package com.billwise.web.dto.group;

import com.billwise.domain.GroupMember;
import com.billwise.domain.GroupRole;

import java.time.LocalDateTime;

/** Safe member view returned to group participants. */
public record GroupMemberResponse(
        Long userId,
        String username,
        String email,
        GroupRole role,
        LocalDateTime joinedAt) {

    public static GroupMemberResponse from(
            GroupMember membership) {
        return new GroupMemberResponse(
                membership.getUser().getId(),
                membership.getUser().getUsername(),
                membership.getUser().getEmail(),
                membership.getRole(),
                membership.getJoinedAt());
    }
}
