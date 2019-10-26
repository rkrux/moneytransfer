package com.bank.moneytransfer.exception.types;

public class FundsInsufficientTransferException extends Exception {
    public FundsInsufficientTransferException(String message) {
        super(message);
    }
}
