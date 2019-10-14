package com.bank.moneytransfer.exception.types;

public class SelfAccountTransferException extends RuntimeException {
    public SelfAccountTransferException(String message) {
        super(message);
    }
}
