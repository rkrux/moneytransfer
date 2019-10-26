package com.bank.moneytransfer.exception.types;


public class AccountNotFoundException extends Exception {
    public AccountNotFoundException(String message) {
        super(message);
    }
}
