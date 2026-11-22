package com.fintrack.transaction.dto;

import com.fintrack.transaction.Transaction;
import com.fintrack.transaction.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * The JSON we SEND BACK for a transaction. Includes the server-set fields
 * (id, createdAt) that the request does not carry.
 */
public record TransactionResponse(
        Long id,
        BigDecimal amount,
        TransactionType type,
        String category,
        String note,
        LocalDate occurredOn,
        OffsetDateTime createdAt
) {
    /** Convert a database entity into the response shape. */
    public static TransactionResponse from(Transaction t) {
        return new TransactionResponse(
                t.getId(), t.getAmount(), t.getType(), t.getCategory(),
                t.getNote(), t.getOccurredOn(), t.getCreatedAt()
        );
    }
}
