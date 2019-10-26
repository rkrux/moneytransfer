package com.bank.moneytransfer.exception;

import com.bank.moneytransfer.exception.types.*;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class ExceptionHandler implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception ex) {

        if ((ex instanceof IncompleteRequestParamsException) || (ex instanceof NegativeAmountTransferException) ||
                (ex instanceof SelfAccountTransferException) || (ex instanceof FundsInsufficientTransferException)) {
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        }

        if (ex instanceof AccountNotFoundException) {
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getMessage()).build();
        }

        if (ex instanceof AccountPresentException) {
            return Response.status(Response.Status.CONFLICT).entity(ex.getMessage()).build();
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
    }
}
