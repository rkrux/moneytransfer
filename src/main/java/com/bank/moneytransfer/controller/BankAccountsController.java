package com.bank.moneytransfer.controller;

import com.bank.moneytransfer.exception.ErrorMessages;
import com.bank.moneytransfer.exception.types.AccountNotFoundException;
import com.bank.moneytransfer.exception.types.IllegalAccountParamsException;
import com.bank.moneytransfer.exception.types.IncompleteRequestParamsException;
import com.bank.moneytransfer.model.AddBankAccountRequest;
import com.bank.moneytransfer.service.BankAccountsService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/bankAccount")
public class BankAccountsController {

    private BankAccountsService bankAccountsService = BankAccountsService.getInstance();

    private void validateAddRequest(AddBankAccountRequest request) throws IncompleteRequestParamsException {
        if ((request == null) || (request.getId() == null) || (request.getBalance() == null)) {
            throw new IncompleteRequestParamsException(ErrorMessages.INCOMPLETE_REQUEST_PARAMS.getValue());
        }
    }

    private Integer transformIdRequest(String requestId) throws IllegalAccountParamsException {
        Integer id;
        try {
            id = Integer.valueOf(requestId);
        } catch (NumberFormatException nfe) {
            throw new IllegalAccountParamsException(ErrorMessages.ILLEGAL_GET_ACCOUNT_PARAMETERS.getValue());
        }
        return id;
    }

    @POST
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addBankAccount(AddBankAccountRequest addBankAccountRequest) throws Exception {
        validateAddRequest(addBankAccountRequest);
        bankAccountsService.addBankAccount(addBankAccountRequest);
        return Response.status(Response.Status.OK).build();
    }

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllAccounts() {
        return Response.status(Response.Status.OK).entity(bankAccountsService.getAllBankAccounts()).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccountById(@PathParam("id") String requestId)
            throws AccountNotFoundException, IllegalAccountParamsException {

        Integer id = transformIdRequest(requestId);
        return Response.status(Response.Status.OK).entity(bankAccountsService.getAccountById(id)).build();
    }
}
