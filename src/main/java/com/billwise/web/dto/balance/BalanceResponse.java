package com.billwise.web.dto.balance;

import java.math.BigDecimal;

public record BalanceResponse(
        Long userId,
        String username,
        BigDecimal balance) {
}