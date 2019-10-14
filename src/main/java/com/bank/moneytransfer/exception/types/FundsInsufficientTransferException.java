package com.bank.moneytransfer.exception.types;

public class FundsInsufficientTransferException extends RuntimeException {
    public FundsInsufficientTransferException(String message) {
        super(message);
    }
}
