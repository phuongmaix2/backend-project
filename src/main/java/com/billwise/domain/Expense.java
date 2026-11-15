package com.billwise.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

@Entity
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Null means this is a personal expense.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private ExpenseGroup group;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payer_id", nullable = false)
    private User payer;

    @Column(nullable = false, length = 200)
    private String description;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(name = "expense_date", nullable = false)
    private LocalDate expenseDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ExpenseCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "split_method", nullable = false, length = 20)
    private SplitMethod splitMethod;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    protected Expense() {
    }

    public Expense(
            ExpenseGroup group,
            User creator,
            User payer,
            String description,
            BigDecimal amount,
            String currency,
            LocalDate expenseDate,
            ExpenseCategory category,
            SplitMethod splitMethod) {

        this.group = group;
        this.creator = creator;
        this.payer = payer;
        this.description = description.trim();
        this.amount = amount;
        this.currency = normalizeCurrency(currency);
        this.expenseDate = expenseDate;
        this.category = category;
        this.splitMethod = splitMethod;
    }

    private static String normalizeCurrency(String currency) {
        return currency.trim().toUpperCase(Locale.ROOT);
    }

    public Long getId() {
        return id;
    }

    public ExpenseGroup getGroup() {
        return group;
    }

    public User getCreator() {
        return creator;
    }

    public User getPayer() {
        return payer;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public LocalDate getExpenseDate() {
        return expenseDate;
    }

    public ExpenseCategory getCategory() {
        return category;
    }

    public SplitMethod getSplitMethod() {
        return splitMethod;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setDescription(String description) {
        this.description = description.trim();
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setCurrency(String currency) {
        this.currency = normalizeCurrency(currency);
    }

    public void setExpenseDate(LocalDate expenseDate) {
        this.expenseDate = expenseDate;
    }

    public void setCategory(ExpenseCategory category) {
        this.category = category;
    }

    public void setSplitMethod(SplitMethod splitMethod) {
        this.splitMethod = splitMethod;
    }
}