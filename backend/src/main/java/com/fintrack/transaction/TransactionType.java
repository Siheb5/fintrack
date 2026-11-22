package com.fintrack.transaction;

/**
 * A transaction is either money coming IN or money going OUT.
 * Using an enum (instead of a free-text string) means the compiler and the
 * database both reject any value that isn't one of these two.
 */
public enum TransactionType {
    INCOME,
    EXPENSE
}
