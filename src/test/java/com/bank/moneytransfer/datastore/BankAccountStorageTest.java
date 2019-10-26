package com.bank.moneytransfer.datastore;

import com.bank.moneytransfer.exception.types.FundsInsufficientTransferException;
import com.bank.moneytransfer.model.BankAccount;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Collection;

import static org.junit.Assert.*;

//Unit Tests
public class BankAccountStorageTest {

    private static BankAccountStorage bankAccountStorage;

    //create bankAccount 2
    private Integer bankAccountIdFrom = 2, bankAccountIdTo = 10;

    @Before
    public void setup() {
        bankAccountStorage = BankAccountStorage.getInstance();
    }

    @Test
    public void testBankAccountStorage() throws Exception {
        //add account 2
        boolean added = bankAccountStorage.addBankAccount(new BankAccount(bankAccountIdFrom,
                new BigDecimal(10).setScale(2, BigDecimal.ROUND_HALF_EVEN)));
        assertTrue(added);

        //get account 2
        BankAccount bankAccount = bankAccountStorage.getBankAccount(bankAccountIdFrom);
        assertNotNull(bankAccount);
        assertEquals(new Integer(bankAccountIdFrom), bankAccount.getId());
        assertEquals(new BigDecimal(10).setScale(2, BigDecimal.ROUND_HALF_EVEN), bankAccount.getBalance());

        //add account 2 again
        added = bankAccountStorage.addBankAccount(new BankAccount(bankAccountIdFrom,
                new BigDecimal(10).setScale(2, BigDecimal.ROUND_HALF_EVEN)));
        assertFalse(added);

        //add account 10
        added = bankAccountStorage.addBankAccount(new BankAccount(bankAccountIdTo,
                new BigDecimal(10).setScale(2, BigDecimal.ROUND_HALF_EVEN)));
        assertTrue(added);

        //transfer amount from 2 to 10
        bankAccountStorage.updateAccounts(bankAccountIdFrom, bankAccountIdTo,
                new BigDecimal(3).setScale(2, BigDecimal.ROUND_HALF_EVEN));

        //get account 2 again
        bankAccount = bankAccountStorage.getBankAccount(bankAccountIdFrom);
        assertNotNull(bankAccount);
        assertEquals(new Integer(bankAccountIdFrom), bankAccount.getId());
        assertEquals(new BigDecimal(7).setScale(2, BigDecimal.ROUND_HALF_EVEN), bankAccount.getBalance());

        //get account 10 again
        bankAccount = bankAccountStorage.getBankAccount(bankAccountIdTo);
        assertNotNull(bankAccount);
        assertEquals(new Integer(bankAccountIdTo), bankAccount.getId());
        assertEquals(new BigDecimal(13).setScale(2, BigDecimal.ROUND_HALF_EVEN), bankAccount.getBalance());

        //get all accounts
        Collection<BankAccount> collection  = bankAccountStorage.getAllBankAccounts();
        assertTrue(collection.size() > 0);
    }
}
