package com.bank.moneytransfer.service;

import com.bank.moneytransfer.datastore.BankAccountStorage;
import com.bank.moneytransfer.exception.types.AccountNotFoundException;
import com.bank.moneytransfer.exception.types.FundsInsufficientTransferException;
import com.bank.moneytransfer.exception.types.NegativeAmountTransferException;
import com.bank.moneytransfer.exception.types.SelfAccountTransferException;
import com.bank.moneytransfer.model.BankAccount;
import com.bank.moneytransfer.model.TransferMoneyRequest;
import com.bank.moneytransfer.model.TransferMoneyResponse;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

//Unit Tests
public class TransferMoneyServiceTest {

    BankAccountStorage bankAccountStorage = BankAccountStorage.getInstance();
    TransferMoneyService transferMoneyService;

    //create and transfer money between these accounts
    private static Integer fromId = 7;
    private static BigDecimal fromBalance = new BigDecimal(30).setScale(2, BigDecimal.ROUND_HALF_EVEN);
    private static Integer toId = 8;
    private static BigDecimal toBalance = new BigDecimal(30).setScale(2, BigDecimal.ROUND_HALF_EVEN);
    private String[] transferAmounts = new String[]{"4.5", "1", "10", "100", "-1"};

    private void buildBankAccountStorage() {
        bankAccountStorage.addBankAccount(new BankAccount(fromId, fromBalance));
        bankAccountStorage.addBankAccount(new BankAccount(toId, toBalance));
    }

    @Before
    public void setup() {
        this.buildBankAccountStorage();
        transferMoneyService = TransferMoneyService.getInstance();
    }

    private TransferMoneyRequest buildRequest(Integer from, Integer to, String amount) {
        TransferMoneyRequest request = new TransferMoneyRequest(from, to,
                new BigDecimal(amount).setScale(2, BigDecimal.ROUND_HALF_EVEN));
        return request;
    }

    @Test(expected = SelfAccountTransferException.class)
    public void testSelfAccountTransfer() throws Exception {
        transferMoneyService.transfer(this.buildRequest(fromId, fromId, transferAmounts[0]));
    }

    @Test(expected = NegativeAmountTransferException.class)
    public void testNegativeAmountTransfer() throws Exception {
        transferMoneyService.transfer(this.buildRequest(fromId, toId, transferAmounts[4]));
    }

    @Test(expected = AccountNotFoundException.class)
    public void testAccountNotFound() throws Exception {
        transferMoneyService.transfer(this.buildRequest(20, toId, transferAmounts[0]));
    }

    @Test(expected = FundsInsufficientTransferException.class)
    public void testFundsInsufficientTransfer() throws Exception {
        transferMoneyService.transfer(this.buildRequest(fromId, toId, transferAmounts[3]));
    }

    @Test
    public void testMoneyTransferSuccess() throws Exception {
        TransferMoneyResponse response = transferMoneyService.transfer(this.buildRequest(fromId, toId, transferAmounts[0]));

        assertNotNull(response);
        assertEquals(new Integer(fromId), response.getUpdatedAccounts().getFrom().getId());
        assertEquals(new Integer(toId), response.getUpdatedAccounts().getTo().getId());
        assertEquals(new BigDecimal(25.5).setScale(2, BigDecimal.ROUND_HALF_EVEN),
                response.getUpdatedAccounts().getFrom().getBalance());
        assertEquals(new BigDecimal(34.5).setScale(2, BigDecimal.ROUND_HALF_EVEN),
                response.getUpdatedAccounts().getTo().getBalance());
    }
}