package com.fintrack.transaction;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Database access for Transaction.
 *
 * The method names drive the generated SQL. Every query here is scoped by
 * userId — that is how we guarantee a user only ever touches their OWN rows.
 */
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /** All transactions for one user, newest first. */
    List<Transaction> findByUserIdOrderByOccurredOnDesc(Long userId);

    /** One transaction, but only if it belongs to the given user. */
    Optional<Transaction> findByIdAndUserId(Long id, Long userId);

    /** Delete one transaction only if it belongs to the given user; returns rows removed. */
    long deleteByIdAndUserId(Long id, Long userId);
}
