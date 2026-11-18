package com.billwise.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;

/** Stores one participant's share, 
 * preserving the amount used at creation time. */

@Entity
@Table(name = "expense_shares", uniqueConstraints = {
        @UniqueConstraint(name = "uk_expense_share_participant", columnNames = {
                "expense_id",
                "participant_id"
        })
})
public class ExpenseShare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "expense_id", nullable = false)
    private Expense expense;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "participant_id", nullable = false)
    private User participant;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal shareAmount;

    protected ExpenseShare() {
    }

    public ExpenseShare(
            Expense expense,
            User participant,
            BigDecimal shareAmount) {

        this.expense = expense;
        this.participant = participant;
        this.shareAmount = shareAmount;
    }

    public Long getId() {
        return id;
    }

    public Expense getExpense() {
        return expense;
    }

    public User getParticipant() {
        return participant;
    }

    public BigDecimal getShareAmount() {
        return shareAmount;
    }
}
