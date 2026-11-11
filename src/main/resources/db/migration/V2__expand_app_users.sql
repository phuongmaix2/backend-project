-- Rename the password column to show that it stores an encoded password.
ALTER TABLE app_users
RENAME COLUMN password TO password_hash;

-- Normalize existing email addresses.
UPDATE app_users
SET email = LOWER(TRIM(email));

-- Prevent emails that differ only by uppercase/lowercase.
CREATE UNIQUE INDEX uk_app_users_email_lower
ON app_users (LOWER(email));

-- Add profile and account-status fields.
ALTER TABLE app_users
ADD COLUMN preferred_currency VARCHAR(3) NOT NULL DEFAULT 'EUR';

ALTER TABLE app_users
ADD COLUMN enabled BOOLEAN NOT NULL DEFAULT TRUE;

-- Ensure all existing users have a creation time.
UPDATE app_users
SET created_at = CURRENT_TIMESTAMP
WHERE created_at IS NULL;

ALTER TABLE app_users
ALTER COLUMN created_at SET NOT NULL;