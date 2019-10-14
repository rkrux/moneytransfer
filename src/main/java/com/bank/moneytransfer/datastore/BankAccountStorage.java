package com.bank.moneytransfer.datastore;

import com.bank.moneytransfer.model.BankAccount;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//In-Memory DataStore that provides only basic get/add/update functions without business logic
public class BankAccountStorage {

    private static final BankAccountStorage INSTANCE = new BankAccountStorage();
    private Map<Integer, BankAccount> bankAccounts;

    public static BankAccountStorage getInstance() {
        return INSTANCE;
    }

    private BankAccountStorage() {
        bankAccounts = new ConcurrentHashMap<>();
    }

    //if already present, return false, else return true
    public boolean addBankAccount(BankAccount bankAccount) {
        BankAccount existingAccount = bankAccounts.putIfAbsent(bankAccount.getId(), bankAccount);
        return (existingAccount == null);
    }

    public BankAccount getBankAccount(Integer id) {
        return bankAccounts.get(id);
    }

    public Collection<BankAccount> getAllBankAccounts() {
        return bankAccounts.values();
    }

    public void updateBankAccount(Integer id, BigDecimal balance) {
        bankAccounts.put(id, new BankAccount(id, balance));
    }
}
