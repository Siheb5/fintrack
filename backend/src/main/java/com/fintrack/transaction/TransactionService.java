package com.fintrack.transaction;

import com.fintrack.transaction.dto.SummaryResponse;
import com.fintrack.transaction.dto.TransactionRequest;
import com.fintrack.transaction.dto.TransactionResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Business logic for transactions. Knows nothing about HTTP or JSON — it just
 * takes plain values and returns plain results. Every method is scoped by
 * `userId`, so a user can only ever touch their own data.
 *
 * @Service marks this as a Spring-managed component (injected into the controller).
 */
@Service
public class TransactionService {

    private final TransactionRepository repository;

    // Spring injects the repository automatically (constructor injection).
    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
    }

    /** Create a new transaction owned by the given user. */
    @Transactional
    public TransactionResponse create(Long userId, TransactionRequest req) {
        Transaction t = new Transaction(
                userId, req.amount(), req.type(), req.category(), req.note(), req.occurredOn());
        return TransactionResponse.from(repository.save(t));
    }

    /** List all of the user's transactions, newest first. */
    @Transactional(readOnly = true)
    public List<TransactionResponse> list(Long userId) {
        return repository.findByUserIdOrderByOccurredOnDesc(userId)
                .stream()
                .map(TransactionResponse::from)
                .toList();
    }

    /** Get one transaction — but only if it belongs to the user. */
    @Transactional(readOnly = true)
    public TransactionResponse getOne(Long userId, Long id) {
        Transaction t = repository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> notFound(id));
        return TransactionResponse.from(t);
    }

    /** Update one transaction the user owns. 404 if it isn't theirs. */
    @Transactional
    public TransactionResponse update(Long userId, Long id, TransactionRequest req) {
        Transaction t = repository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> notFound(id));
        t.setAmount(req.amount());
        t.setType(req.type());
        t.setCategory(req.category());
        t.setNote(req.note());
        t.setOccurredOn(req.occurredOn());
        return TransactionResponse.from(repository.save(t));
    }

    /** Delete one transaction the user owns. 404 if it isn't theirs. */
    @Transactional
    public void delete(Long userId, Long id) {
        long removed = repository.deleteByIdAndUserId(id, userId);
        if (removed == 0) {
            throw notFound(id);
        }
    }

    /** Compute totals and the per-category expense breakdown for the dashboard. */
    @Transactional(readOnly = true)
    public SummaryResponse summary(Long userId) {
        List<Transaction> all = repository.findByUserIdOrderByOccurredOnDesc(userId);

        BigDecimal income = BigDecimal.ZERO;
        BigDecimal expense = BigDecimal.ZERO;
        // LinkedHashMap keeps categories in insertion order for a stable chart.
        Map<String, BigDecimal> perCategory = new LinkedHashMap<>();

        for (Transaction t : all) {
            if (t.getType() == TransactionType.INCOME) {
                income = income.add(t.getAmount());
            } else {
                expense = expense.add(t.getAmount());
                perCategory.merge(t.getCategory(), t.getAmount(), BigDecimal::add);
            }
        }

        List<SummaryResponse.CategoryTotal> byCategory = new ArrayList<>();
        perCategory.forEach((cat, total) ->
                byCategory.add(new SummaryResponse.CategoryTotal(cat, total)));

        return new SummaryResponse(income, expense, income.subtract(expense), byCategory);
    }

    /** Helper that produces a 404 response. */
    private ResponseStatusException notFound(Long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction " + id + " not found");
    }
}
