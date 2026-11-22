package com.fintrack.transaction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * Mirrors the "transactions" table. One Transaction object == one row.
 *
 * Design choice: we store the owner's id in a plain `userId` field rather than
 * a full @ManyToOne User object. For a REST API this is simpler and avoids
 * JPA lazy-loading pitfalls; the database still enforces the real foreign key.
 */
@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The id of the user who owns this transaction (foreign key in the DB). */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** Money amount. BigDecimal (not double) because money must be exact. */
    @Column(nullable = false)
    private BigDecimal amount;

    /** INCOME or EXPENSE. Stored as text in a VARCHAR column. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    /** e.g. Food, Rent, Salary. */
    @Column(nullable = false)
    private String category;

    /** Optional free-text note. */
    @Column
    private String note;

    /** The date the money actually moved. */
    @Column(name = "occurred_on", nullable = false)
    private LocalDate occurredOn;

    /** When this row was created; filled by the DB default and read back after INSERT. */
    @Generated(event = EventType.INSERT)
    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    /** Convenience constructor for creating a new transaction. */
    public Transaction(Long userId, BigDecimal amount, TransactionType type,
                       String category, String note, LocalDate occurredOn) {
        this.userId = userId;
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.note = note;
        this.occurredOn = occurredOn;
    }
}
