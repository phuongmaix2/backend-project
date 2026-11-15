CREATE TABLE expenses (
    id BIGSERIAL PRIMARY KEY,

    group_id BIGINT,

    creator_id BIGINT NOT NULL,

    payer_id BIGINT NOT NULL,

    description VARCHAR(200) NOT NULL,

    amount NUMERIC(19, 2) NOT NULL,

    currency VARCHAR(3) NOT NULL,

    expense_date DATE NOT NULL,

    category VARCHAR(30) NOT NULL,

    split_method VARCHAR(20) NOT NULL,

    created_at TIMESTAMP(6) NOT NULL,

    CONSTRAINT ck_expense_amount_positive
        CHECK (amount > 0),

    CONSTRAINT fk_expense_group
        FOREIGN KEY (group_id)
        REFERENCES expense_groups(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_expense_creator
        FOREIGN KEY (creator_id)
        REFERENCES app_users(id),

    CONSTRAINT fk_expense_payer
        FOREIGN KEY (payer_id)
        REFERENCES app_users(id)
);

CREATE INDEX idx_expenses_group_id
    ON expenses(group_id);

CREATE INDEX idx_expenses_creator_id
    ON expenses(creator_id);

CREATE INDEX idx_expenses_payer_id
    ON expenses(payer_id);

CREATE INDEX idx_expenses_date
    ON expenses(expense_date);