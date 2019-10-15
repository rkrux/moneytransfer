package com.bank.moneytransfer.exception.types;

public class SelfAccountTransferException extends Exception {
    public SelfAccountTransferException(String message) {
        super(message);
    }
}
