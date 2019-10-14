package com.bank.moneytransfer.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ErrorMessages {
    INCOMPLETE_REQUEST_PARAMS("Incomplete transfer parameters"),
    ACCOUNT_NOT_FOUND("Account not found"),
    ACCOUNT_PRESENT("Account already present"),
    NOT_POSITIVE_AMOUNT_TRANSFER("Only positive amount transfer allowed"),
    FUNDS_INSUFFICIENT_TRANSFER("Account balance insufficient for this transfer"),
    SELF_ACCOUNT_TRANSFER("Can't transfer funds to self"),
    ILLEGAL_ACCOUNT_PARAMETERS("Illegal add account parameters");

    @Getter
    private String value;
}
