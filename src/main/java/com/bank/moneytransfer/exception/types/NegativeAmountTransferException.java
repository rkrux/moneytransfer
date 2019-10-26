package com.bank.moneytransfer.exception.types;

public class NegativeAmountTransferException extends Exception {
    public NegativeAmountTransferException(String message) {
        super(message);
    }
}
