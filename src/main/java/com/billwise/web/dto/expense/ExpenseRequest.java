package com.billwise.web.dto.expense;

import com.billwise.domain.ExpenseCategory;
import com.billwise.domain.SplitMethod;
import com.billwise.validation.ValidCurrency;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExpenseRequest {

    // Null means this is a personal expense.
    private Long groupId;

    // Optional for personal expenses.
    private String payerUsernameOrEmail;

    @NotBlank(message = "Description is required")
    @Size(max = 200, message = "Description must not exceed 200 characters")
    private String description;

    @NotNull(message = "Amount is required")
    @DecimalMin(
            value = "0.01",
            message = "Amount must be greater than zero"
    )
    private BigDecimal amount;

    @NotBlank(message = "Currency is required")
    @ValidCurrency
    private String currency;

    @NotNull(message = "Expense date is required")
    private LocalDate expenseDate;

    @NotNull(message = "Category is required")
    private ExpenseCategory category;

    @NotNull(message = "Split method is required")
    private SplitMethod splitMethod;

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getPayerUsernameOrEmail() {
        return payerUsernameOrEmail;
    }

    public void setPayerUsernameOrEmail(
            String payerUsernameOrEmail) {
        this.payerUsernameOrEmail = payerUsernameOrEmail;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public LocalDate getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(LocalDate expenseDate) {
        this.expenseDate = expenseDate;
    }

    public ExpenseCategory getCategory() {
        return category;
    }

    public void setCategory(ExpenseCategory category) {
        this.category = category;
    }

    public SplitMethod getSplitMethod() {
        return splitMethod;
    }

    public void setSplitMethod(SplitMethod splitMethod) {
        this.splitMethod = splitMethod;
    }
}