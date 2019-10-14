package com.bank.moneytransfer.exception.types;

public class NegativeAmountTransferException extends RuntimeException {
    public NegativeAmountTransferException(String message) {
        super(message);
    }
}
