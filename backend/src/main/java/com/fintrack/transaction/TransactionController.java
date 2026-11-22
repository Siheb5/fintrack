package com.fintrack.transaction;

import com.fintrack.transaction.dto.SummaryResponse;
import com.fintrack.transaction.dto.TransactionRequest;
import com.fintrack.transaction.dto.TransactionResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * The HTTP layer for transactions. Maps each endpoint to a service call,
 * reads/writes JSON, and sets the correct status code. No business logic here.
 *
 * @RestController = returns data (JSON), not web pages.
 * @RequestMapping = all paths in this class start with /api/transactions.
 */
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    // The current user comes from the verified JWT (set by JwtAuthFilter).
    private Long currentUserId() {
        return com.fintrack.security.CurrentUser.id();
    }

    /** POST /api/transactions  -> 201 Created */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse create(@Valid @RequestBody TransactionRequest req) {
        return service.create(currentUserId(), req);
    }

    /** GET /api/transactions  -> 200 OK, list of the user's transactions */
    @GetMapping
    public List<TransactionResponse> list() {
        return service.list(currentUserId());
    }

    /** GET /api/transactions/summary  -> 200 OK, dashboard totals */
    @GetMapping("/summary")
    public SummaryResponse summary() {
        return service.summary(currentUserId());
    }

    /** GET /api/transactions/{id}  -> 200 OK or 404 */
    @GetMapping("/{id}")
    public TransactionResponse getOne(@PathVariable Long id) {
        return service.getOne(currentUserId(), id);
    }

    /** PUT /api/transactions/{id}  -> 200 OK or 404 */
    @PutMapping("/{id}")
    public TransactionResponse update(@PathVariable Long id,
                                      @Valid @RequestBody TransactionRequest req) {
        return service.update(currentUserId(), id, req);
    }

    /** DELETE /api/transactions/{id}  -> 204 No Content or 404 */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(currentUserId(), id);
    }
}
