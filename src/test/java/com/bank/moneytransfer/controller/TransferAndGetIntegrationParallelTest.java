package com.bank.moneytransfer.controller;

import com.anarsoft.vmlens.concurrent.junit.ConcurrentTestRunner;
import com.bank.moneytransfer.datastore.BankAccountStorage;
import com.bank.moneytransfer.exception.ExceptionHandler;
import com.bank.moneytransfer.model.AllBankAccountsResponse;
import com.bank.moneytransfer.model.BankAccount;
import com.bank.moneytransfer.model.TransferMoneyRequest;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

//Integration Test that runs 4 threads in parallel and later checks the final states of the bank accounts
//Will fail if business logic doesn't handle multi-threading scenarios
@RunWith(ConcurrentTestRunner.class)
public class TransferAndGetIntegrationParallelTest extends JerseyTest {

    //create and transfer money between these accounts
    private static Integer fromId = 5;
    private static BigDecimal fromBalance = new BigDecimal(30).setScale(2, BigDecimal.ROUND_HALF_EVEN);
    private static Integer toId = 6;
    private static BigDecimal toBalance = new BigDecimal(30).setScale(2, BigDecimal.ROUND_HALF_EVEN);
    private String[] transferAmounts = new String[]{"1.5"};

    //Runs in Main Thread
    @Before
    public void createBankAccounts() {
        BankAccountStorage bankAccountStorage = BankAccountStorage.getInstance();
        bankAccountStorage.addBankAccount(new BankAccount(fromId, fromBalance));
        bankAccountStorage.addBankAccount(new BankAccount(toId, toBalance));
    }

    @Override
    protected Application configure() {
        return new ResourceConfig(BankAccountsController.class, TransferMoneyController.class, ExceptionHandler.class);
    }

    private TransferMoneyRequest buildRequest(Integer from, Integer to, String amount) {
        return new
                TransferMoneyRequest(from, to,
                new BigDecimal(amount).setScale(2, BigDecimal.ROUND_HALF_EVEN));
    }

    //transfer money from 5 to 6 in 4 parallel threads
    @Test
    public void testTransferMoneySuccess5to6() {
        Response response = target("/transferMoney")
                .request()
                .post(Entity.json(buildRequest(fromId, toId, transferAmounts[0])));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
    }

    //Runs in Main Thread that finally verifies the test case by calling the GET API
    //final balance of first account = (30 - (4 * 1.5)) = 24 | final balance of first account = (30 + (4 * 1.5)) = 36
    @After
    public void verifyTransferMoneySuccess5to6() {
        Response response = target("/bankAccounts/all")
                .request()
                .get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());

        AllBankAccountsResponse result = response.readEntity(AllBankAccountsResponse.class);
        List<BankAccount> allBankAccounts = result.getAllBankAccounts();

        assertTrue(allBankAccounts.size() > 0);

        BankAccount fromBankAccount = allBankAccounts
                .stream()
                .filter(bankAccount -> bankAccount.getId().equals(fromId))
                .findAny()
                .get();

        BankAccount toBankAccount = allBankAccounts
                .stream()
                .filter(bankAccount -> bankAccount.getId().equals(toId))
                .findAny()
                .get();

        assertEquals(fromId, fromBankAccount.getId());
        assertEquals(new BigDecimal(24).setScale(2, BigDecimal.ROUND_HALF_EVEN),
               fromBankAccount.getBalance());
        assertEquals(toId, toBankAccount.getId());
        assertEquals(new BigDecimal(36).setScale(2, BigDecimal.ROUND_HALF_EVEN),
                toBankAccount.getBalance());
    }
}
