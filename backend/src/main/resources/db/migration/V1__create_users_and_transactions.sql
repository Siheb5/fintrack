-- V1__create_users_and_transactions.sql
-- Flyway migration #1: creates the initial database schema for FinTrack.
-- Flyway runs files in order (V1, V2, ...) exactly once and records which have run.

-- ========================= USERS =========================
-- One row per person who signs up.
CREATE TABLE users (
    id            BIGSERIAL     PRIMARY KEY,          -- auto-incrementing unique id
    email         VARCHAR(255)  NOT NULL UNIQUE,      -- login name; no two users share one
    password_hash VARCHAR(255)  NOT NULL,             -- the SCRAMBLED password, never the real one
    created_at    TIMESTAMPTZ   NOT NULL DEFAULT now()-- when the account was created
);

-- ===================== TRANSACTIONS ======================
-- One row per income/expense entry. Each belongs to exactly one user.
CREATE TABLE transactions (
    id           BIGSERIAL     PRIMARY KEY,
    user_id      BIGINT        NOT NULL,              -- which user owns this row
    amount       NUMERIC(12,2) NOT NULL CHECK (amount > 0),  -- money, always positive
    type         VARCHAR(10)   NOT NULL CHECK (type IN ('INCOME', 'EXPENSE')),
    category     VARCHAR(100)  NOT NULL,              -- e.g. Food, Rent, Salary
    note         VARCHAR(255),                        -- optional free text
    occurred_on  DATE          NOT NULL,              -- the date the money moved
    created_at   TIMESTAMPTZ   NOT NULL DEFAULT now(),

    -- FOREIGN KEY: ties each transaction to a real user. This is what enforces
    -- "every transaction has an owner". ON DELETE CASCADE = if a user is deleted,
    -- their transactions are deleted too (no orphan rows left behind).
    CONSTRAINT fk_transactions_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- An INDEX makes "find all transactions for user X" fast, the query we run most.
-- Without it, the database would scan every row every time.
CREATE INDEX idx_transactions_user_id ON transactions (user_id);
