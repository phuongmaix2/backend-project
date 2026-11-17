package com.billwise.web.dto.report;

import java.math.BigDecimal;

public record SpendingReportItem(
        String label,
        BigDecimal total) {
}