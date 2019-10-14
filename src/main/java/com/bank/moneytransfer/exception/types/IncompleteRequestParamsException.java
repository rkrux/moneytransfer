package com.bank.moneytransfer.exception.types;

public class IncompleteRequestParamsException extends RuntimeException {
    public IncompleteRequestParamsException(String message) {
        super(message);
    }
}
