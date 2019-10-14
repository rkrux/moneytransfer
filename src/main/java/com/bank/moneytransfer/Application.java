package com.bank.moneytransfer;

import com.bank.moneytransfer.controller.BankAccountsController;
import com.bank.moneytransfer.controller.TransferMoneyController;
import com.bank.moneytransfer.datastore.BankAccountStorage;
import com.bank.moneytransfer.exception.ExceptionHandler;
import com.bank.moneytransfer.model.BankAccount;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.jackson.JacksonFeature;

import java.math.BigDecimal;

public class Application {

    private void startServer() throws Exception {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        Server jettyServer = new Server(8080);
        jettyServer.setHandler(context);

        ServletHolder jerseyServlet = context.
                addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");
        jerseyServlet.setInitOrder(0);

        jerseyServlet.setInitParameter(
                "jersey.config.server.provider.classnames",
                TransferMoneyController.class.getCanonicalName() + ";"
                        +  BankAccountsController.class.getCanonicalName() + ";"
                        +  ExceptionHandler.class.getCanonicalName() + ";"
                        + JacksonFeature.class.getCanonicalName());

        try {
            jettyServer.start();
            jettyServer.join();
        }  catch (Exception e){
            jettyServer.stop();
            jettyServer.destroy();
        }
    }

    //creates some sample bank accounts in the in-memory data-store
    private void createBankAccounts() {
        BankAccountStorage bankAccountStorage = BankAccountStorage.getInstance();
        bankAccountStorage.addBankAccount(new BankAccount(1,
                new BigDecimal(100).setScale(2, BigDecimal.ROUND_HALF_EVEN)));
        bankAccountStorage.addBankAccount(new BankAccount(2,
                new BigDecimal(50).setScale(2, BigDecimal.ROUND_HALF_EVEN)));
        bankAccountStorage.addBankAccount(new BankAccount(3,
                new BigDecimal(20.52).setScale(2, BigDecimal.ROUND_HALF_EVEN)));
        bankAccountStorage.addBankAccount(new BankAccount(4,
                new BigDecimal(30.61).setScale(2, BigDecimal.ROUND_HALF_EVEN)));
    }

    public static void main(String[] args) throws Exception {
        Application application = new Application();
        application.createBankAccounts();
        application.startServer();
    }
}
