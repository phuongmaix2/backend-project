package com.billwise.web.dto.expense;

import com.billwise.domain.Expense;
import com.billwise.domain.ExpenseCategory;
import com.billwise.domain.SplitMethod;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ExpenseResponse(
        Long id,
        Long groupId,
        String creatorUsername,
        String payerUsername,
        String description,
        BigDecimal amount,
        String currency,
        LocalDate expenseDate,
        ExpenseCategory category,
        SplitMethod splitMethod,
        LocalDateTime createdAt) {

    public static ExpenseResponse from(
            Expense expense) {

        return new ExpenseResponse(
                expense.getId(),
                expense.getGroup() == null
                        ? null
                        : expense.getGroup().getId(),
                expense.getCreator().getUsername(),
                expense.getPayer().getUsername(),
                expense.getDescription(),
                expense.getAmount(),
                expense.getCurrency(),
                expense.getExpenseDate(),
                expense.getCategory(),
                expense.getSplitMethod(),
                expense.getCreatedAt());
    }
}