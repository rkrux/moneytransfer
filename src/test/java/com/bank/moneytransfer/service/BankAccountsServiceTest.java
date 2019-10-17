package com.bank.moneytransfer.service;

import com.bank.moneytransfer.exception.ErrorMessages;
import com.bank.moneytransfer.exception.types.AccountPresentException;
import com.bank.moneytransfer.exception.types.IllegalAccountParamsException;
import com.bank.moneytransfer.model.AddBankAccountRequest;
import com.bank.moneytransfer.model.AllBankAccountsResponse;
import com.bank.moneytransfer.model.BankAccount;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

//Unit Tests
public class BankAccountsServiceTest {
    BankAccountsService bankAccountsService;

    //create this account
    private static Integer fromId = 9;
    private static BigDecimal fromBalance = new BigDecimal(30).setScale(2, BigDecimal.ROUND_HALF_EVEN);
    private static BigDecimal illegalBalance = new BigDecimal(-1).setScale(2, BigDecimal.ROUND_HALF_EVEN);

    private AddBankAccountRequest buildRequest(Integer id, BigDecimal balance) {
        return new AddBankAccountRequest(id, balance);
    }

    @Before
    public void setup() {
        bankAccountsService = BankAccountsService.getInstance();
    }

    @Test
    public void testBankAccountsService() throws Exception {
        //create bank account 9 with wrong parameters
        try {
            bankAccountsService.addBankAccount(buildRequest(fromId, illegalBalance));
        } catch (IllegalAccountParamsException ex) {
            assertEquals(ErrorMessages.ILLEGAL_ADD_ACCOUNT_PARAMETERS.getValue(), ex.getMessage());
        }


        //create bank account 9
        bankAccountsService.addBankAccount(buildRequest(fromId, fromBalance));

        //get all accounts
        AllBankAccountsResponse allBankAccountsResponse = bankAccountsService.getAllBankAccounts();
        assertNotNull(allBankAccountsResponse);
        List<BankAccount> bankAccountList = allBankAccountsResponse.getAllBankAccounts();
        assertNotNull(bankAccountList);
        assertTrue(bankAccountList.size() > 0);
        BankAccount fromBankAccount = bankAccountList
                .stream()
                .filter(bankAccount -> bankAccount.getId().equals(fromId))
                .findAny()
                .get();
        assertEquals(fromId, fromBankAccount.getId());
        assertEquals(fromBalance, fromBankAccount.getBalance());

        //create bank account 9 again
        try {
            bankAccountsService.addBankAccount(buildRequest(fromId, fromBalance));
        } catch (AccountPresentException ex) {
            assertEquals(ErrorMessages.ACCOUNT_PRESENT.getValue(), ex.getMessage());
        }
    }
}