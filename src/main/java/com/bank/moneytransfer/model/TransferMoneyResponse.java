package com.bank.moneytransfer.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TransferMoneyResponse {

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public class UpdatedAccounts {
        BankAccount from;
        BankAccount to;
    }

    UpdatedAccounts updatedAccounts;

    public TransferMoneyResponse(BankAccount from, BankAccount to) {
        updatedAccounts = new UpdatedAccounts(from, to);
    }
}