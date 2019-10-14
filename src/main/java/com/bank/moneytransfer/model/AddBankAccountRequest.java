package com.bank.moneytransfer.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AddBankAccountRequest {
    Integer id;
    BigDecimal balance;
}
