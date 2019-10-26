package com.bank.moneytransfer.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class BankAccount {
    Integer id;
    BigDecimal balance;

    public BankAccount(Integer id, BigDecimal balance) {
        this.id = id;
        this.balance = balance;
    }
}
