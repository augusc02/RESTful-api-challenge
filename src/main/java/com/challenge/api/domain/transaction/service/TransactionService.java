package com.challenge.api.domain.transaction.service;

import com.challenge.api.domain.transaction.dto.TransactionRequest;
import com.challenge.api.domain.transaction.dto.TransactionResponse;

import java.util.List;


public interface TransactionService {

    // Returns all stored transactions.
    List<TransactionResponse> findAll();

    // Returns all transactions matching the given type (case-insensitive).
    List<TransactionResponse> findByType(String type);

    // Returns the ids of all transactions matching the given type (case-insensitive).
    List<Long> findIdsByType(String type);

    // Returns the transaction identified by {@code id}, or throws if not found.
    TransactionResponse findById(Long id);

    // Calculates the sum of all transaction amounts transitively
    Double sumByTransactionId(Long id);

    // Creates or updates a transaction manually by id. Returns true if it was newly created.
    boolean putTransaction(Long id, TransactionRequest request);
}
