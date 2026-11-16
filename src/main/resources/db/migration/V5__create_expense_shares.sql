CREATE TABLE expense_shares (
    id BIGSERIAL PRIMARY KEY,

    expense_id BIGINT NOT NULL,

    participant_id BIGINT NOT NULL,

    share_amount NUMERIC(19, 2) NOT NULL,

    CONSTRAINT ck_expense_share_amount_positive
        CHECK (share_amount > 0),

    CONSTRAINT fk_expense_share_expense
        FOREIGN KEY (expense_id)
        REFERENCES expenses(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_expense_share_participant
        FOREIGN KEY (participant_id)
        REFERENCES app_users(id),

    CONSTRAINT uk_expense_share_participant
        UNIQUE (expense_id, participant_id)
);

CREATE INDEX idx_expense_shares_expense_id
    ON expense_shares(expense_id);

CREATE INDEX idx_expense_shares_participant_id
    ON expense_shares(participant_id);