package com.challenge.api.domain.transaction.exception;

/**
 * Thrown when a requested transaction is not found.
 */
public class TransactionNotFoundException extends RuntimeException {

    public TransactionNotFoundException(Long id) {
        super(String.format("Transaction not found with id: %d", id));
    }

    public TransactionNotFoundException(String field, String value) {
        super(String.format("Transaction not found with %s: '%s'", field, value));
    }
}
