package com.bank.moneytransfer.service;

import com.bank.moneytransfer.datastore.BankAccountStorage;
import com.bank.moneytransfer.exception.ErrorMessages;
import com.bank.moneytransfer.exception.types.AccountNotFoundException;
import com.bank.moneytransfer.exception.types.AccountPresentException;
import com.bank.moneytransfer.exception.types.IllegalAccountParamsException;
import com.bank.moneytransfer.dto.AddBankAccountRequest;
import com.bank.moneytransfer.dto.AllBankAccountsResponse;
import com.bank.moneytransfer.model.BankAccount;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class BankAccountsService {

    private static final BankAccountsService INSTANCE = new BankAccountsService();
    BankAccountStorage bankAccountStorage = BankAccountStorage.getInstance();

    Function<AddBankAccountRequest, BankAccount> converter =
            (request ->
                    new BankAccount(request.getId(),
                            request.getBalance().setScale(2, BigDecimal.ROUND_HALF_EVEN)));

    public static BankAccountsService getInstance() {
        return INSTANCE;
    }

    private void validateAddRequest(AddBankAccountRequest request) throws IllegalAccountParamsException {
        if ((request.getId().compareTo(0) <= 0) || (request.getBalance().compareTo(BigDecimal.ZERO) <= 0)) {
            throw new IllegalAccountParamsException(ErrorMessages.ILLEGAL_ADD_ACCOUNT_PARAMETERS.getValue());
        }
    }

    public void addBankAccount(AddBankAccountRequest addBankAccountRequest)
            throws AccountPresentException, IllegalAccountParamsException {

        validateAddRequest(addBankAccountRequest);
        boolean added = bankAccountStorage.addBankAccount(converter.apply(addBankAccountRequest));
        //if already present, then added is false
        if (!added) {
            throw new AccountPresentException(ErrorMessages.ACCOUNT_PRESENT.getValue());
        }
    }

    public AllBankAccountsResponse getAllBankAccounts() {
        Collection<BankAccount> collection  = bankAccountStorage.getAllBankAccounts();
        List<BankAccount> bankAccounts = new ArrayList<>(collection);
        return new AllBankAccountsResponse(bankAccounts);
    }

    public BankAccount getAccountById(Integer id) throws AccountNotFoundException {
        BankAccount bankAccount = bankAccountStorage.getBankAccount(id);
        if (bankAccount == null) {
            throw new AccountNotFoundException(ErrorMessages.ACCOUNT_NOT_FOUND.getValue());
        }
        return bankAccount;
    }
}
