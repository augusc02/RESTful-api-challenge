package com.challenge.api.domain.transaction.exception;

public class SelfReferencingTransactionException extends RuntimeException {

    public SelfReferencingTransactionException(Long id) {
        super("Transaction %d cannot reference itself as its parent".formatted(id));
    }
}
