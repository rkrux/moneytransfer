package com.bank.moneytransfer.controller;

import com.bank.moneytransfer.exception.ErrorMessages;
import com.bank.moneytransfer.exception.types.IncompleteRequestParamsException;
import com.bank.moneytransfer.dto.TransferMoneyRequest;
import com.bank.moneytransfer.service.TransferMoneyService;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/transferMoney")
public class TransferMoneyController {

    private TransferMoneyService transferMoneyService = TransferMoneyService.getInstance();

    //basic validations on the request data
    private void validateTransferRequest(TransferMoneyRequest request) throws IncompleteRequestParamsException {
        if ((request == null) || (request.getFrom() == null) ||
                (request.getTo() == null) || (request.getAmount() == null))
            throw new IncompleteRequestParamsException(ErrorMessages.INCOMPLETE_REQUEST_PARAMS.getValue());
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response transfer(TransferMoneyRequest transferMoneyRequest) throws Exception {
        validateTransferRequest(transferMoneyRequest);
        return Response.status(Response.Status.OK).entity(transferMoneyService.transfer(transferMoneyRequest)).build();
    }
}
