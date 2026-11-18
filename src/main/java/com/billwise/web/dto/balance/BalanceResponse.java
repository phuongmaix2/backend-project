package com.billwise.web.dto.balance;

import java.math.BigDecimal;

/** Read-only net balance returned for one group member. */
public record BalanceResponse(
        Long userId,
        String username,
        BigDecimal balance) {
}
