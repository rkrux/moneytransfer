package com.bank.moneytransfer.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AllBankAccountsResponse {
    List<BankAccount> allBankAccounts;
}
