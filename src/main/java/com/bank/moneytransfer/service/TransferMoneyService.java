package com.bank.moneytransfer.service;

import com.bank.moneytransfer.datastore.BankAccountStorage;
import com.bank.moneytransfer.exception.ErrorMessages;
import com.bank.moneytransfer.exception.types.AccountNotFoundException;
import com.bank.moneytransfer.exception.types.FundsInsufficientTransferException;
import com.bank.moneytransfer.exception.types.NegativeAmountTransferException;
import com.bank.moneytransfer.exception.types.SelfAccountTransferException;
import com.bank.moneytransfer.model.BankAccount;
import com.bank.moneytransfer.model.TransferMoneyRequest;
import com.bank.moneytransfer.model.TransferMoneyResponse;

import java.math.BigDecimal;

public class TransferMoneyService {

    private static TransferMoneyService INSTANCE = new TransferMoneyService();
    private BankAccountStorage bankAccountStorage = BankAccountStorage.getInstance();

    //shared lock used to synchronize the atomic operations
    private final Object sharedLock = new Object();

    public static TransferMoneyService getInstance() {
        return INSTANCE;
    }

    //business logic related validations on the request data
    private void validateRequest(TransferMoneyRequest request) {
        if (request.getFrom().equals(request.getTo())) {
            throw new SelfAccountTransferException(ErrorMessages.SELF_ACCOUNT_TRANSFER.getValue());
        }
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new NegativeAmountTransferException(ErrorMessages.NOT_POSITIVE_AMOUNT_TRANSFER.getValue());
        }
    }

    //business logic related validations on the in-memory bank accounts
    private void validateAccounts(TransferMoneyRequest request, BankAccount from, BankAccount to) {
        if ((from == null) || (to == null)) {
            throw new AccountNotFoundException(ErrorMessages.ACCOUNT_NOT_FOUND.getValue());
        }
        if (from.getBalance().compareTo(request.getAmount()) < 0) {
            throw new FundsInsufficientTransferException(ErrorMessages.FUNDS_INSUFFICIENT_TRANSFER.getValue());
        }
    }

    //transfer money between bank accounts
    public TransferMoneyResponse transfer(TransferMoneyRequest request) {
        //couple of validations on the transfer request
        this.validateRequest(request);

        BankAccount from, to;

        //Removing this synchronized block will cause the multi-threaded parallel IntegrationTests to fail.
        //This block ensures the data integrity and consistency in a multi-threaded scenario.
        synchronized (sharedLock) {
            from = bankAccountStorage.getBankAccount(request.getFrom());
            to = bankAccountStorage.getBankAccount(request.getTo());

            validateAccounts(request, from, to);

            //debit of the bank account
            bankAccountStorage.updateBankAccount(from.getId(), (from.getBalance().subtract(request.getAmount())));
            //credit to the bank account
            bankAccountStorage.updateBankAccount(to.getId(), (to.getBalance().add(request.getAmount())));

            //read the updated state of the bank accounts
            from = bankAccountStorage.getBankAccount(request.getFrom());
            to = bankAccountStorage.getBankAccount(request.getTo());
        }

        return new TransferMoneyResponse(from, to);
    }
}
