package com.bank.moneytransfer.exception.types;

public class IllegalAccountParamsException extends RuntimeException {
    public IllegalAccountParamsException(String message) {
        super(message);
    }
}
