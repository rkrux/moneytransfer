package com.bank.moneytransfer.controller;

import com.anarsoft.vmlens.concurrent.junit.ConcurrentTestRunner;
import com.bank.moneytransfer.TestUtil;
import com.bank.moneytransfer.datastore.BankAccountStorage;
import com.bank.moneytransfer.exception.ExceptionHandler;
import com.bank.moneytransfer.dto.AllBankAccountsResponse;
import com.bank.moneytransfer.model.BankAccount;
import com.bank.moneytransfer.dto.TransferMoneyRequest;
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
    private static Integer[] fromId = new Integer[]{5, 15};
    private static Integer[] toId = new Integer[]{6, 16};
    private static BigDecimal[] fromBalance = new BigDecimal[]{
            new BigDecimal(30).setScale(2, BigDecimal.ROUND_HALF_EVEN),
            new BigDecimal(50).setScale(2, BigDecimal.ROUND_HALF_EVEN)};
    private static BigDecimal[] toBalance = new BigDecimal[]{
            new BigDecimal(30).setScale(2, BigDecimal.ROUND_HALF_EVEN),
            new BigDecimal(50).setScale(2, BigDecimal.ROUND_HALF_EVEN)};

    private String[] transferAmounts = new String[]{"1.5", "2"};

    //Runs in Main Thread
    @Before
    public void createBankAccounts() {
        //creates 4 accounts
        BankAccountStorage bankAccountStorage = BankAccountStorage.getInstance();
        for (int i = 0; i < fromId.length; i++) {
            bankAccountStorage.addBankAccount(new BankAccount(fromId[i], fromBalance[i]));
        }
        for (int i = 0; i < toId.length; i++) {
            bankAccountStorage.addBankAccount(new BankAccount(toId[i], toBalance[i]));
        }
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

    //transfer money from (5 to 6) and (15 to 16) in 4 parallel threads
    @Test
    public void testTransferMoneySuccess5to6() {
        for (int i = 0; i < fromId.length; i++) {
            Response response = target(TestUtil.TRANSFER_MONEY_PATH)
                    .request()
                    .post(Entity.json(buildRequest(fromId[i], toId[i], transferAmounts[i])));
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            assertNotNull(response.getEntity());
        }
    }

    private BankAccount findAndGetBankAccount(List<BankAccount> allBankAccounts, Integer id) {
        return allBankAccounts
                .stream()
                .filter(bankAccount -> bankAccount.getId().equals(id))
                .findAny()
                .get();
    }

    //Runs in Main Thread that finally verifies the test cases by calling the GET API
    @After
    public void verifyTransferMoneySuccess5to6() {
        Response response = target(TestUtil.BANK_ACCOUNT_GET_ALL_PATH)
                .request()
                .get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());

        AllBankAccountsResponse result = response.readEntity(AllBankAccountsResponse.class);
        List<BankAccount> allBankAccounts = result.getAllBankAccounts();

        assertTrue(allBankAccounts.size() > 0);

        BankAccount fromBankAccount, toBankAccount;

        //Assert transfer 5 to 6
        //final balance of 5 = (30 - (4 * 1.5)) = 24 | final balance of 6 = (30 + (4 * 1.5)) = 36
        fromBankAccount = findAndGetBankAccount(allBankAccounts, fromId[0]);
        toBankAccount = findAndGetBankAccount(allBankAccounts, toId[0]);
        assertEquals(fromId[0], fromBankAccount.getId());
        assertEquals(new BigDecimal(24).setScale(2, BigDecimal.ROUND_HALF_EVEN),
               fromBankAccount.getBalance());
        assertEquals(toId[0], toBankAccount.getId());
        assertEquals(new BigDecimal(36).setScale(2, BigDecimal.ROUND_HALF_EVEN),
                toBankAccount.getBalance());

        //Assert transfer 15 to 16
        //final balance of 15 = (50 - (4 * 2)) = 42 | final balance of 16 = (50 + (4 * 2)) = 58
        fromBankAccount = findAndGetBankAccount(allBankAccounts, fromId[1]);
        toBankAccount = findAndGetBankAccount(allBankAccounts, toId[1]);
        assertEquals(fromId[1], fromBankAccount.getId());
        assertEquals(new BigDecimal(42).setScale(2, BigDecimal.ROUND_HALF_EVEN),
                fromBankAccount.getBalance());
        assertEquals(toId[1], toBankAccount.getId());
        assertEquals(new BigDecimal(58).setScale(2, BigDecimal.ROUND_HALF_EVEN),
                toBankAccount.getBalance());
    }
}
