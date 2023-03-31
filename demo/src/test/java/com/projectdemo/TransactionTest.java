package com.projectdemo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TransactionTest {
    Bank bank;
    User testUser;
    Account checkingAccount;

    public void sampleDB(){
        bank = new Bank("Fleeca");

        // add two users, which also creates a Savings account
        testUser = bank.addUser("Jasmine", "Ng", "8831");

        // add a checking account for our user
        checkingAccount = new Account("Checking", testUser, bank);

        testUser.addAccount(checkingAccount);

        bank.addAccount(checkingAccount);
    }

    @Test
    public void getAmountTest(){
        Transaction trans = new Transaction(100,"deposit",checkingAccount);
        double amount = trans.getAmount();
        assertTrue(amount == 100);
    }

    @Test
    public void getSummaryLineTest(){
        Transaction trans = new Transaction(100,"deposit",checkingAccount);
        String summary = trans.getSummaryLine();
        String[] summarySection = summary.split(",");
        assertEquals(summarySection[1], " $100.00 : deposit");
    }
}
