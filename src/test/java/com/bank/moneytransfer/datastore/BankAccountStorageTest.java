package com.bank.moneytransfer.datastore;

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
    private Integer bankAccountId = 2;

    @Before
    public void setup() {
        bankAccountStorage = BankAccountStorage.getInstance();
    }

    @Test
    public void testBankAccountStorage() {
        //add account 2
        boolean added = bankAccountStorage.addBankAccount(new BankAccount(bankAccountId,
                new BigDecimal(10).setScale(2, BigDecimal.ROUND_HALF_EVEN)));
        assertTrue(added);

        //get account 2
        BankAccount bankAccount = bankAccountStorage.getBankAccount(bankAccountId);
        assertNotNull(bankAccount);
        assertEquals(new Integer(bankAccountId), bankAccount.getId());
        assertEquals(new BigDecimal(10).setScale(2, BigDecimal.ROUND_HALF_EVEN), bankAccount.getBalance());

        //add account 2 again
        added = bankAccountStorage.addBankAccount(new BankAccount(bankAccountId,
                new BigDecimal(10).setScale(2, BigDecimal.ROUND_HALF_EVEN)));
        assertFalse(added);

        //update account 2
        bankAccountStorage.updateBankAccount(bankAccountId,
                new BigDecimal(20).setScale(2, BigDecimal.ROUND_HALF_EVEN));

        //get account 2 again
        bankAccount = bankAccountStorage.getBankAccount(bankAccountId);
        assertNotNull(bankAccount);
        assertEquals(new Integer(bankAccountId), bankAccount.getId());
        assertEquals(new BigDecimal(20).setScale(2, BigDecimal.ROUND_HALF_EVEN), bankAccount.getBalance());

        //get all accounts
        Collection<BankAccount> collection  = bankAccountStorage.getAllBankAccounts();
        assertTrue(collection.size() > 0);
    }
}
