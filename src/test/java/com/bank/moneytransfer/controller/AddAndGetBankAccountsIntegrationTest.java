package com.bank.moneytransfer.controller;

import com.bank.moneytransfer.exception.ErrorMessages;
import com.bank.moneytransfer.exception.ExceptionHandler;
import com.bank.moneytransfer.model.AddBankAccountRequest;
import com.bank.moneytransfer.model.AllBankAccountsResponse;
import com.bank.moneytransfer.model.BankAccount;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

public class AddAndGetBankAccountsIntegrationTest extends JerseyTest {

    //create bankAccount 1
    private Integer bankAccountId = 1;

    @Override
    protected Application configure() {
        return new ResourceConfig(BankAccountsController.class, ExceptionHandler.class);
    }

    private AddBankAccountRequest buildPostRequest(Integer id, String amount) {
        AddBankAccountRequest request;
        if (amount == null) {
            request = new AddBankAccountRequest(id, null);
        } else {
            request = new AddBankAccountRequest(id,
                    new BigDecimal(amount).setScale(2, BigDecimal.ROUND_HALF_EVEN));
        }
        return request;
    }

    @Test
    public void testBankAccountController() {
        //add bank account 1 with invalid request
        Response response = target("/bankAccounts/add")
                .request()
                .post(Entity.json(buildPostRequest(bankAccountId, null)));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(ErrorMessages.INCOMPLETE_REQUEST_PARAMS.getValue(), response.readEntity(String.class));

        //add bank account 1
        response = target("/bankAccounts/add")
                .request()
                .post(Entity.json(buildPostRequest(bankAccountId, "30")));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        //get all bank accounts
        response = target("/bankAccounts/all")
                .request()
                .get();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        AllBankAccountsResponse result = response.readEntity(AllBankAccountsResponse.class);
        List<BankAccount> allBankAccounts = result.getAllBankAccounts();
        assertTrue(allBankAccounts.size() > 0);

        //add bank account 1 again
        response = target("/bankAccounts/add")
                .request()
                .post(Entity.json(buildPostRequest(bankAccountId, "30")));
        assertEquals(Response.Status.PRECONDITION_FAILED.getStatusCode(), response.getStatus());
        assertEquals(ErrorMessages.ACCOUNT_PRESENT.getValue(), response.readEntity(String.class));
    }

}
