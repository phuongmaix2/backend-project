CREATE TABLE expense_groups (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    base_currency VARCHAR(3) NOT NULL,
    owner_id BIGINT NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,

    CONSTRAINT fk_expense_group_owner
        FOREIGN KEY (owner_id)
        REFERENCES app_users(id)
);

CREATE TABLE group_members (
    id BIGSERIAL PRIMARY KEY,
    group_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    role VARCHAR(20) NOT NULL,
    joined_at TIMESTAMP(6) NOT NULL,

    CONSTRAINT fk_group_member_group
        FOREIGN KEY (group_id)
        REFERENCES expense_groups(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_group_member_user
        FOREIGN KEY (user_id)
        REFERENCES app_users(id),

    CONSTRAINT uk_group_member
        UNIQUE (group_id, user_id)
);

CREATE INDEX idx_group_members_user_id
    ON group_members(user_id);