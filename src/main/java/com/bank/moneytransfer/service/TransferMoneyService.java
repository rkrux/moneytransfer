package com.bank.moneytransfer.service;

import com.bank.moneytransfer.datastore.BankAccountStorage;
import com.bank.moneytransfer.exception.ErrorMessages;
import com.bank.moneytransfer.exception.types.AccountNotFoundException;
import com.bank.moneytransfer.exception.types.NegativeAmountTransferException;
import com.bank.moneytransfer.exception.types.SelfAccountTransferException;
import com.bank.moneytransfer.model.BankAccount;
import com.bank.moneytransfer.model.TransferMoneyRequest;
import com.bank.moneytransfer.model.TransferMoneyResponse;

import java.math.BigDecimal;

public class TransferMoneyService {

    private static TransferMoneyService INSTANCE = new TransferMoneyService();
    private BankAccountStorage bankAccountStorage = BankAccountStorage.getInstance();

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

    private TransferMoneyResponse doAtomicTransfer(TransferMoneyRequest request) {
        //transfer amount from one to another atomically
        bankAccountStorage.updateAccounts(request.getFrom(), request.getTo(), request.getAmount());

        //read the updated account state and return
        return new TransferMoneyResponse(bankAccountStorage.getBankAccount(request.getFrom()),
                bankAccountStorage.getBankAccount(request.getTo()));
    }

    //transfer money between bank accounts
    public TransferMoneyResponse transfer(TransferMoneyRequest request) {
        //couple of validations on the transfer request
        validateRequest(request);

        //these bankAccount references will be used to synchronize the atomic operations
        BankAccount from = bankAccountStorage.getBankAccount(request.getFrom());
        BankAccount to = bankAccountStorage.getBankAccount(request.getTo());

        //validate accounts before synchronized block because either of them could be null
        if ((from == null) || (to == null)) {
            throw new AccountNotFoundException(ErrorMessages.ACCOUNT_NOT_FOUND.getValue());
        }

        //Removing these synchronized blocks will cause the multi-threaded parallel IntegrationTests to fail.
        //These blocks ensures the data integrity and consistency in a multi-threaded scenario.
        if (from.getId().compareTo(to.getId()) < 0) {
            synchronized (from) {
                synchronized (to) {
                    return doAtomicTransfer(request);
                }
            }
        } else {
            synchronized (to) {
                synchronized (from) {
                    return doAtomicTransfer(request);
                }
            }
        }

    }
}