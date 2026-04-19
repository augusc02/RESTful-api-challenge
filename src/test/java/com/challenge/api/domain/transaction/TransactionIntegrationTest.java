package com.challenge.api.domain.transaction;

import com.challenge.api.domain.transaction.dto.TransactionRequest;
import com.challenge.api.domain.transaction.model.Transaction;
import com.challenge.api.domain.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TransactionIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TransactionRepository transactionRepository;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/transactions";
    }

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
    }

    @Test
    void shouldCreateATransactionAndPersistIt() {
        TransactionRequest request = new TransactionRequest();
        request.setAmount(5000.0);
        request.setType("cars");
        request.setParentId(0L);

        ResponseEntity<Map> response = restTemplate.exchange(
                getBaseUrl() + "/10",
                HttpMethod.PUT,
                new HttpEntity<>(request),
                Map.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("ok", response.getBody().get("status"));

        assertTrue(transactionRepository.existsById(10L));
    }

    @Test
    void shouldFetchTransactionsByType() {
        createTransaction(20L, 100.0, "food", 0L);
        createTransaction(21L, 50.0, "food", 0L);
        createTransaction(22L, 200.0, "travel", 0L);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                getBaseUrl() + "/types/food",
                HttpMethod.GET,
                null,
                (Class<Map<String, Object>>) (Class<?>) Map.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        List<Number> ids = (List<Number>) response.getBody().get("data");
        assertNotNull(ids);
        assertEquals(2, ids.size());
        assertTrue(ids.stream().anyMatch(n -> n.longValue() == 20L));
        assertTrue(ids.stream().anyMatch(n -> n.longValue() == 21L));
        assertFalse(ids.stream().anyMatch(n -> n.longValue() == 22L), "Travel transaction should not be returned for type 'food'");
    }

    @Test
    void shouldCalculateSumOfSubtree() {
        createTransaction(30L, 1000.0, "main", 0L);
        createTransaction(31L, 250.0, "child1", 30L);
        createTransaction(32L, 50.0, "child2", 30L);
        createTransaction(33L, 10.0, "grandchild", 31L);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                getBaseUrl() + "/sum/30",
                HttpMethod.GET,
                null,
                (Class<Map<String, Object>>) (Class<?>) Map.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
        assertNotNull(data);
        assertEquals(1310.0, ((Number) data.get("sum")).doubleValue(), 0.001);
    }

    @Test
    void shouldRejectSelfReferencingTransaction() {
        TransactionRequest request = new TransactionRequest();
        request.setAmount(100.0);
        request.setType("food");
        request.setParentId(40L);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                getBaseUrl() + "/40",
                HttpMethod.PUT,
                new HttpEntity<>(request),
                (Class<Map<String, Object>>) (Class<?>) Map.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(false, response.getBody().get("success"));
        assertEquals("Transaction 40 cannot reference itself as its parent", response.getBody().get("message"));
    }

    @Test
    void shouldHandleEmptyParentId() {
        TransactionRequest requestCreate = new TransactionRequest();
        requestCreate.setAmount(100.0);
        requestCreate.setType("test");

        ResponseEntity<Map<String, Object>> createResponse = restTemplate.exchange(
                getBaseUrl() + "/50",
                HttpMethod.PUT,
                new HttpEntity<>(requestCreate),
                (Class<Map<String, Object>>) (Class<?>) Map.class
        );

        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        Transaction t1 = transactionRepository.findById(50L).orElseThrow();
        assertEquals(0L, t1.getParentId());
        assertEquals(100.0, t1.getAmount());

        createTransaction(10L, 500.0, "parent", 0L);
        TransactionRequest setParentRequest = new TransactionRequest();
        setParentRequest.setAmount(100.0);
        setParentRequest.setType("test");
        setParentRequest.setParentId(10L);
        restTemplate.exchange(
                getBaseUrl() + "/50",
                HttpMethod.PUT,
                new HttpEntity<>(setParentRequest),
                (Class<Map<String, Object>>) (Class<?>) Map.class
        );

        TransactionRequest requestUpdate = new TransactionRequest();
        requestUpdate.setAmount(200.0);
        requestUpdate.setType("test2");

        ResponseEntity<Map<String, Object>> updateResponse = restTemplate.exchange(
                getBaseUrl() + "/50",
                HttpMethod.PUT,
                new HttpEntity<>(requestUpdate),
                (Class<Map<String, Object>>) (Class<?>) Map.class
        );

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        Transaction t2 = transactionRepository.findById(50L).orElseThrow();
        assertEquals(10L, t2.getParentId());
        assertEquals(200.0, t2.getAmount());
        assertEquals("test2", t2.getType());
    }

    @Test
    void shouldReturnTransactionById() {
        createTransaction(60L, 750.0, "electronics", 0L);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                getBaseUrl() + "/60",
                HttpMethod.GET,
                null,
                (Class<Map<String, Object>>) (Class<?>) Map.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(true, response.getBody().get("success"));
        Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
        assertNotNull(data);
        assertEquals(750.0, ((Number) data.get("amount")).doubleValue(), 0.001);
        assertEquals("electronics", data.get("type"));
    }

    @Test
    void shouldReturn404WhenTransactionNotFound() {
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                getBaseUrl() + "/9999",
                HttpMethod.GET,
                null,
                (Class<Map<String, Object>>) (Class<?>) Map.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(false, response.getBody().get("success"));
        assertNotNull(response.getBody().get("message"));
    }

    private void createTransaction(long id, double amount, String type, long parentId) {
        TransactionRequest request = new TransactionRequest();
        request.setAmount(amount);
        request.setType(type);
        request.setParentId(parentId);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                getBaseUrl() + "/" + id,
                HttpMethod.PUT,
                new HttpEntity<>(request),
                (Class<Map<String, Object>>) (Class<?>) Map.class
        );
        assertTrue(response.getStatusCode().is2xxSuccessful(),
                "Setup PUT failed for transaction id=" + id + ", status=" + response.getStatusCode());
    }
}
