package com.challenge.api.domain.transaction.controller;

import com.challenge.api.domain.transaction.dto.TransactionRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TransactionControllerTest {

    private static final String BASE_URL = "/transactions";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateATransaction() throws Exception {
        TransactionRequest request = new TransactionRequest();
        request.setAmount(100.0);
        request.setType("food");
        request.setParentId(0L);

                mockMvc.perform(put(BASE_URL + "/" + 10)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ok"));
    }

    @Test
    void shouldUpdateATransaction() throws Exception {
        TransactionRequest createRequest = new TransactionRequest();
        createRequest.setAmount(100.0);
        createRequest.setType("food");
        createRequest.setParentId(0L);

        mockMvc.perform(put(BASE_URL + "/" + 20)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ok"));

        TransactionRequest updateRequest = new TransactionRequest();
        updateRequest.setAmount(250.0);
        updateRequest.setType("shopping");
        updateRequest.setParentId(0L);

        mockMvc.perform(put(BASE_URL + "/" + 20)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"));
    }

    @Test
    void shouldReturnTransactionIdsByType() throws Exception {
        createTransaction(30, 500.0, "food", 0L);
        createTransaction(31, 200.0, "food", 0L);
        createTransaction(32, 99.0, "shopping", 0L);

        mockMvc.perform(get(BASE_URL + "/types/food"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void shouldReturnSumOfTransactionSubtree() throws Exception {
        createTransaction(40, 100.0, "food", 0L);

        createTransaction(41, 30.0, "food", 40);
        createTransaction(42, 20.0, "food", 40);

        mockMvc.perform(get(BASE_URL + "/sum/" + 40))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.sum").value(150.0));
    }


    private String createTransaction(long id, double amount, String type, long parentId) throws Exception {
        TransactionRequest request = new TransactionRequest();
        request.setAmount(amount);
        request.setType(type);
        request.setParentId(parentId);

        return mockMvc.perform(put(BASE_URL + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ok"))
                .andReturn().getResponse().getContentAsString();
    }
}
