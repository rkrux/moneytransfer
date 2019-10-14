package com.bank.moneytransfer.controller;

import com.bank.moneytransfer.exception.ErrorMessages;
import com.bank.moneytransfer.exception.types.IncompleteRequestParamsException;
import com.bank.moneytransfer.model.AddBankAccountRequest;
import com.bank.moneytransfer.service.BankAccountsService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/bankAccounts")
public class BankAccountsController {

    private BankAccountsService bankAccountsService = BankAccountsService.getInstance();

    private void validateAddRequest(AddBankAccountRequest request) {
        if ((request == null) || (request.getId() == null) || (request.getBalance() == null)) {
            throw new IncompleteRequestParamsException(ErrorMessages.INCOMPLETE_REQUEST_PARAMS.getValue());
        }
    }

    @POST
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addBankAccount(AddBankAccountRequest addBankAccountRequest) {
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
}
