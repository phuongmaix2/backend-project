package com.billwise.web.dto.group;

import com.billwise.domain.ExpenseGroup;

import java.time.LocalDateTime;

public record GroupResponse(
    Long id,
    String name,
    String description,
    String baseCurrency,
    Long ownerId,
    String ownerUsername,
    LocalDateTime createdAt
) {

    public static GroupResponse from(
        ExpenseGroup group
    ) {
        return new GroupResponse(
            group.getId(),
            group.getName(),
            group.getDescription(),
            group.getBaseCurrency(),
            group.getOwner().getId(),
            group.getOwner().getUsername(),
            group.getCreatedAt()
        );
    }
}