package com.fintrack.transaction.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * The JSON for the dashboard summary: overall totals plus a per-category
 * breakdown (which the mobile app turns into a pie/bar chart).
 */
public record SummaryResponse(
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal balance,                 // income minus expense
        List<CategoryTotal> byCategory      // expense totals grouped by category
) {
    /** One slice of the spending-by-category chart. */
    public record CategoryTotal(String category, BigDecimal total) {
    }
}
