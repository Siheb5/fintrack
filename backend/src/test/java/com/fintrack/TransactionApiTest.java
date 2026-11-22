package com.fintrack;

import com.fintrack.user.User;
import com.fintrack.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * End-to-end test of the transaction API over (mock) HTTP.
 *
 * @SpringBootTest + @AutoConfigureMockMvc boots the whole app and lets us fire
 * real HTTP requests at it without a running server. We use the "test" profile
 * so the DevDataLoader is off and we seed our own user with id 1.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional   // each test method rolls back, so tests don't see each other's data
class TransactionApiTest {

    @Autowired MockMvc mvc;
    @Autowired UserRepository users;

    @BeforeEach
    void seedUser() {
        // The controller acts as the first user in the DB; ensure one exists.
        if (users.count() == 0) {
            users.save(new User("dev@fintrack.local", "pw"));
        }
    }

    @Test
    void fullCrudFlowWorks() throws Exception {
        // CREATE -> 201 Created, returns an id
        String created = mvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"amount": 12.50, "type": "EXPENSE", "category": "Food",
                             "note": "Lunch", "occurredOn": "2026-11-20"}
                            """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.category").value("Food"))
                .andReturn().getResponse().getContentAsString();

        long id = com.jayway.jsonpath.JsonPath.parse(created).read("$.id", Integer.class);

        // LIST -> 200, contains our one transaction
        mvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].note").value("Lunch"));

        // GET one -> 200
        mvc.perform(get("/api/transactions/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(12.50));

        // UPDATE -> 200, value changed
        mvc.perform(put("/api/transactions/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"amount": 20.00, "type": "EXPENSE", "category": "Food",
                             "note": "Dinner", "occurredOn": "2026-11-20"}
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.note").value("Dinner"))
                .andExpect(jsonPath("$.amount").value(20.00));

        // DELETE -> 204
        mvc.perform(delete("/api/transactions/" + id))
                .andExpect(status().isNoContent());

        // GET after delete -> 404
        mvc.perform(get("/api/transactions/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void invalidInputIsRejectedWith400() throws Exception {
        // amount 0 and blank category violate validation rules
        mvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"amount": 0, "type": "EXPENSE", "category": "",
                             "occurredOn": "2026-11-20"}
                            """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void summaryComputesTotals() throws Exception {
        mvc.perform(post("/api/transactions").contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"amount": 2000, "type": "INCOME", "category": "Salary", "occurredOn": "2026-11-01"}
                    """)).andExpect(status().isCreated());
        mvc.perform(post("/api/transactions").contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"amount": 300, "type": "EXPENSE", "category": "Rent", "occurredOn": "2026-11-02"}
                    """)).andExpect(status().isCreated());
        mvc.perform(post("/api/transactions").contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"amount": 50, "type": "EXPENSE", "category": "Food", "occurredOn": "2026-11-03"}
                    """)).andExpect(status().isCreated());

        mvc.perform(get("/api/transactions/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalIncome").value(2000))
                .andExpect(jsonPath("$.totalExpense").value(350))
                .andExpect(jsonPath("$.balance").value(1650))
                .andExpect(jsonPath("$.byCategory.length()").value(2));
    }
}
