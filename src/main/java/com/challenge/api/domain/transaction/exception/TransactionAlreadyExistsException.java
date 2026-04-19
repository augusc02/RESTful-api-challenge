package com.challenge.api.domain.transaction.exception;

/**
 * Thrown when trying to create a transaction that already exists.
 */
public class TransactionAlreadyExistsException extends RuntimeException {

    public TransactionAlreadyExistsException(String field, String value) {
        super(String.format("Transaction already exists with %s: '%s'", field, value));
    }
}
