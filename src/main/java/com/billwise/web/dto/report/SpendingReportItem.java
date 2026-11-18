package com.billwise.web.dto.report;

import java.math.BigDecimal;

/** One category total in a spending report. */
public record SpendingReportItem(
        String label,
        BigDecimal total) {
}
