package com.fintrack.transaction.dto;

import com.fintrack.transaction.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * The JSON the phone SENDS when creating or updating a transaction.
 *
 * A Java "record" is a compact, immutable data holder — perfect for DTOs.
 * The validation annotations reject bad input automatically (before our
 * service logic runs), returning HTTP 400 with a helpful message.
 */
public record TransactionRequest(

        @NotNull(message = "amount is required")
        @DecimalMin(value = "0.01", message = "amount must be greater than 0")
        BigDecimal amount,

        @NotNull(message = "type is required (INCOME or EXPENSE)")
        TransactionType type,

        @NotBlank(message = "category is required")
        @Size(max = 100, message = "category must be at most 100 characters")
        String category,

        @Size(max = 255, message = "note must be at most 255 characters")
        String note,

        @NotNull(message = "occurredOn date is required")
        LocalDate occurredOn
) {
}
