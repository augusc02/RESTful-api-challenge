package com.challenge.api.domain.transaction.controller;

import com.challenge.api.domain.transaction.dto.TransactionRequest;
import com.challenge.api.domain.transaction.dto.TransactionResponse;
import com.challenge.api.domain.transaction.service.TransactionService;
import com.challenge.api.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "CRUD operations for financial transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    @Operation(
            summary = "List transactions",
            description = "Returns all transactions"
    )
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> findAll() {

        List<TransactionResponse> results = transactionService.findAll();

        return ResponseEntity.ok(ApiResponse.success(results));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get transaction by id")
    public ResponseEntity<ApiResponse<TransactionResponse>> findById(
            @Parameter(description = "Transaction id", required = true)
            @PathVariable Long id) {

        return ResponseEntity.ok(ApiResponse.success(transactionService.findById(id)));
    }

    @GetMapping("/types/{type}")
    @Operation(
            summary = "Get transaction ids by type",
            description = "Returns a list of ids of all transactions matching the given type"
    )
    public ResponseEntity<ApiResponse<List<Long>>> findIdsByType(
            @Parameter(description = "Transaction type (e.g. 'car', 'shopping')", required = true)
            @PathVariable String type) {

        return ResponseEntity.ok(ApiResponse.success(transactionService.findIdsByType(type)));
    }

    @GetMapping("/sum/{id}")
    @Operation(
            summary = "Get sum of a transaction subtree",
            description = "Returns {\"sum\": <double>} with the total amount of the transaction and all its transitive children"
    )
    public ResponseEntity<ApiResponse<java.util.Map<String, Double>>> sum(@PathVariable Long id) {
        Double total = transactionService.sumByTransactionId(id);
        return ResponseEntity.ok(ApiResponse.success(java.util.Map.of("sum", total)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Create or update a transaction")
    public ResponseEntity<java.util.Map<String, String>> putTransaction(
            @PathVariable Long id,
            @Valid @RequestBody TransactionRequest request) {

        return transactionService.putTransaction(id, request);
    }
}
