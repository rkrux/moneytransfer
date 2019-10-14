package com.bank.moneytransfer.exception.types;

public class AccountPresentException extends RuntimeException {
    public AccountPresentException(String message) {
        super(message);
    }
}
