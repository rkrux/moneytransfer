package com.bank.moneytransfer.controller;

import com.bank.moneytransfer.datastore.BankAccountStorage;
import com.bank.moneytransfer.exception.ErrorMessages;
import com.bank.moneytransfer.exception.ExceptionHandler;
import com.bank.moneytransfer.model.BankAccount;
import com.bank.moneytransfer.model.TransferMoneyRequest;
import com.bank.moneytransfer.model.TransferMoneyResponse;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

//Functional Tests
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TransferMoneyFunctionalTest extends JerseyTest {

    //create and transfer money between these accounts
    private static Integer fromId = 3;
    private static BigDecimal fromBalance = new BigDecimal(30).setScale(2, BigDecimal.ROUND_HALF_EVEN);
    private static Integer toId = 4;
    private static BigDecimal toBalance = new BigDecimal(40).setScale(2, BigDecimal.ROUND_HALF_EVEN);
    private String[] transferAmounts = new String[]{"2.5", "1", "10", "100", "-1"};

    private TransferMoneyRequest buildTransferRequest(Integer from, Integer to, String amount) {
        return new
                TransferMoneyRequest(from, to,
                new BigDecimal(amount).setScale(2, BigDecimal.ROUND_HALF_EVEN));
    }

    @BeforeClass
    public static void createBankAccounts() {
        BankAccountStorage bankAccountStorage = BankAccountStorage.getInstance();
        bankAccountStorage.addBankAccount(new BankAccount(fromId, fromBalance));
        bankAccountStorage.addBankAccount(new BankAccount(toId, toBalance));
    }

    @Override
    protected Application configure() {
        return new ResourceConfig(BankAccountsController.class, TransferMoneyController.class, ExceptionHandler.class);
    }

    @Test
    public void testTransferMoneySuccess3to4() {
        Response response = target("/transferMoney")
                .request()
                .post(Entity.json(buildTransferRequest(fromId, toId, transferAmounts[0])));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());

        TransferMoneyResponse transferMoneyResponse = response.readEntity(TransferMoneyResponse.class);

        assertEquals(fromId, transferMoneyResponse.getUpdatedAccounts().getFrom().getId());
        assertEquals(new BigDecimal(27.5).setScale(2, BigDecimal.ROUND_HALF_EVEN),
                transferMoneyResponse.getUpdatedAccounts().getFrom().getBalance());
        assertEquals(toId, transferMoneyResponse.getUpdatedAccounts().getTo().getId());
        assertEquals(new BigDecimal(42.5).setScale(2, BigDecimal.ROUND_HALF_EVEN),
                transferMoneyResponse.getUpdatedAccounts().getTo().getBalance());
    }

    @Test
    public void testTransferMoneySuccess4to3() {
        Response response = target("/transferMoney")
                .request()
                .post(Entity.json(buildTransferRequest(toId, fromId, transferAmounts[1])));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());

        TransferMoneyResponse transferMoneyResponse = response.readEntity(TransferMoneyResponse.class);

        assertEquals(fromId, transferMoneyResponse.getUpdatedAccounts().getTo().getId());
        assertEquals(new BigDecimal(28.5).setScale(2, BigDecimal.ROUND_HALF_EVEN),
                transferMoneyResponse.getUpdatedAccounts().getTo().getBalance());
        assertEquals(toId, transferMoneyResponse.getUpdatedAccounts().getFrom().getId());
        assertEquals(new BigDecimal(41.5).setScale(2, BigDecimal.ROUND_HALF_EVEN),
                transferMoneyResponse.getUpdatedAccounts().getFrom().getBalance());
    }

    @Test
    public void testIncompleteTransferParams() {
        Response response = target("/transferMoney")
                .request()
                .post(Entity.json(buildTransferRequest(null, toId, transferAmounts[2])));

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        assertEquals(ErrorMessages.INCOMPLETE_REQUEST_PARAMS.getValue(), response.readEntity(String.class));
    }

    @Test
    public void testAccountNotFound() {
        Response response = target("/transferMoney")
                .request()
                .post(Entity.json(buildTransferRequest(10, toId, transferAmounts[2])));

        assertEquals(Response.Status.PRECONDITION_FAILED.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        assertEquals(ErrorMessages.ACCOUNT_NOT_FOUND.getValue(), response.readEntity(String.class));
    }

    @Test
    public void testFundsInsufficientTransfer() {
        Response response = target("/transferMoney")
                .request()
                .post(Entity.json(buildTransferRequest(fromId, toId, transferAmounts[3])));

        assertEquals(Response.Status.PRECONDITION_FAILED.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        assertEquals(ErrorMessages.FUNDS_INSUFFICIENT_TRANSFER.getValue(), response.readEntity(String.class));
    }

    @Test
    public void testNegativeAmountTransfer() {
        Response response = target("/transferMoney")
                .request()
                .post(Entity.json(buildTransferRequest(fromId, toId, transferAmounts[4])));

        assertEquals(Response.Status.PRECONDITION_FAILED.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        assertEquals(ErrorMessages.NOT_POSITIVE_AMOUNT_TRANSFER.getValue(), response.readEntity(String.class));
    }

    @Test
    public void testSelfAccountTransfer() {
        Response response = target("/transferMoney")
                .request()
                .post(Entity.json(buildTransferRequest(fromId, fromId, transferAmounts[4])));

        assertEquals(Response.Status.PRECONDITION_FAILED.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        assertEquals(ErrorMessages.SELF_ACCOUNT_TRANSFER.getValue(), response.readEntity(String.class));
    }
}
