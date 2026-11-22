package com.fintrack;

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
 * End-to-end test of the transaction API. Every endpoint now requires a valid
 * JWT, so each test first registers a user, grabs the token, and sends it in
 * the Authorization header.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TransactionApiTest {

    @Autowired MockMvc mvc;

    private String token;

    @BeforeEach
    void registerAndGetToken() throws Exception {
        String body = mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"iheb@example.com\",\"password\":\"supersecret\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        token = com.jayway.jsonpath.JsonPath.parse(body).read("$.token");
    }

    private String bearer() {
        return "Bearer " + token;
    }

    @Test
    void requestsWithoutTokenAreRejected() throws Exception {
        mvc.perform(get("/api/transactions"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void fullCrudFlowWorks() throws Exception {
        String created = mvc.perform(post("/api/transactions")
                        .header("Authorization", bearer())
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

        mvc.perform(get("/api/transactions").header("Authorization", bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].note").value("Lunch"));

        mvc.perform(get("/api/transactions/" + id).header("Authorization", bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(12.50));

        mvc.perform(put("/api/transactions/" + id)
                        .header("Authorization", bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"amount": 20.00, "type": "EXPENSE", "category": "Food",
                             "note": "Dinner", "occurredOn": "2026-11-20"}
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.note").value("Dinner"));

        mvc.perform(delete("/api/transactions/" + id).header("Authorization", bearer()))
                .andExpect(status().isNoContent());

        mvc.perform(get("/api/transactions/" + id).header("Authorization", bearer()))
                .andExpect(status().isNotFound());
    }

    @Test
    void invalidInputIsRejectedWith400() throws Exception {
        mvc.perform(post("/api/transactions")
                        .header("Authorization", bearer())
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
        createTxn("Salary", "INCOME", 2000, "2026-11-01");
        createTxn("Rent", "EXPENSE", 300, "2026-11-02");
        createTxn("Food", "EXPENSE", 50, "2026-11-03");

        mvc.perform(get("/api/transactions/summary").header("Authorization", bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalIncome").value(2000))
                .andExpect(jsonPath("$.totalExpense").value(350))
                .andExpect(jsonPath("$.balance").value(1650))
                .andExpect(jsonPath("$.byCategory.length()").value(2));
    }

    private void createTxn(String category, String type, int amount, String date) throws Exception {
        mvc.perform(post("/api/transactions")
                        .header("Authorization", bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format(
                                "{\"amount\": %d, \"type\": \"%s\", \"category\": \"%s\", \"occurredOn\": \"%s\"}",
                                amount, type, category, date)))
                .andExpect(status().isCreated());
    }
}
