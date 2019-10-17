package com.bank.moneytransfer;

public class TestUtil {
    private static final String BANK_ACCOUNT_BASE_PATH  = "/bankAccount";
    public static final String BANK_ACCOUNT_GET_ALL_PATH  = "/bankAccount/all";
    public static final String BANK_ACCOUNT_ADD_PATH  = "/bankAccount/add";
    public static final String TRANSFER_MONEY_PATH = "/transferMoney";

    public static String getByIdPath(String id) {
        return (BANK_ACCOUNT_BASE_PATH + "/" + id);
    }
}
