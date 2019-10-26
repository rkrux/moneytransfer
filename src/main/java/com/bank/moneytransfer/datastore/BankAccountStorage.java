package com.bank.moneytransfer.datastore;

import com.bank.moneytransfer.exception.ErrorMessages;
import com.bank.moneytransfer.exception.types.FundsInsufficientTransferException;
import com.bank.moneytransfer.model.BankAccount;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//In-Memory DataStore that provides basic utility functions
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

    //must be called in a synchronized block to ensure data integrity
    public void updateAccounts(Integer fromId, Integer toId, BigDecimal amount) {
        //here it's guaranteed that both the accounts exist

        BankAccount from = getBankAccount(fromId);
        //existing balance must be greater than the transfer amount
        if (from.getBalance().compareTo(amount) < 0) {
            throw new FundsInsufficientTransferException(ErrorMessages.FUNDS_INSUFFICIENT_TRANSFER.getValue());
        }
        from.setBalance(from.getBalance().subtract(amount));

        BankAccount to = getBankAccount(toId);
        to.setBalance(to.getBalance().add(amount));
    }
}
