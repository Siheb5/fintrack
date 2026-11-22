package com.fintrack;

import com.fintrack.transaction.Transaction;
import com.fintrack.transaction.TransactionRepository;
import com.fintrack.transaction.TransactionType;
import com.fintrack.user.User;
import com.fintrack.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Proves the entities and repositories actually talk to the database correctly.
 *
 * @DataJpaTest spins up ONLY the JPA layer against an in-memory database,
 * runs Flyway to build the real schema, and rolls everything back after each
 * test so tests never interfere with each other.
 */
@DataJpaTest
// Use our real H2 settings + Flyway schema (don't let Spring swap in its own).
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1",
        "spring.flyway.enabled=true",
        "spring.jpa.hibernate.ddl-auto=validate"
})
class RepositoryTest {

    @Autowired UserRepository users;
    @Autowired TransactionRepository transactions;

    @Test
    void savesAndFindsAUser() {
        User saved = users.save(new User("iheb@example.com", "hashed-pw"));

        assertThat(saved.getId()).isNotNull();          // DB generated an id
        assertThat(saved.getCreatedAt()).isNotNull();   // DB default filled the timestamp

        Optional<User> found = users.findByEmail("iheb@example.com");
        assertThat(found).isPresent();
        assertThat(users.existsByEmail("iheb@example.com")).isTrue();
        assertThat(users.existsByEmail("nobody@example.com")).isFalse();
    }

    @Test
    void savesTransactionsAndFindsOnlyTheOwnersRows() {
        User a = users.save(new User("a@example.com", "pw"));
        User b = users.save(new User("b@example.com", "pw"));

        transactions.save(new Transaction(a.getId(), new BigDecimal("12.50"),
                TransactionType.EXPENSE, "Food", "Lunch", LocalDate.now()));
        transactions.save(new Transaction(a.getId(), new BigDecimal("2000.00"),
                TransactionType.INCOME, "Salary", null, LocalDate.now()));
        transactions.save(new Transaction(b.getId(), new BigDecimal("99.00"),
                TransactionType.EXPENSE, "Rent", null, LocalDate.now()));

        // User A sees only their 2 rows, never user B's.
        List<Transaction> aRows = transactions.findByUserIdOrderByOccurredOnDesc(a.getId());
        assertThat(aRows).hasSize(2);
        assertThat(aRows).allMatch(t -> t.getUserId().equals(a.getId()));

        // Fetching B's row as user A must fail (ownership scoping).
        Transaction bRow = transactions.findByUserIdOrderByOccurredOnDesc(b.getId()).get(0);
        assertThat(transactions.findByIdAndUserId(bRow.getId(), a.getId())).isEmpty();
        assertThat(transactions.findByIdAndUserId(bRow.getId(), b.getId())).isPresent();
    }
}
