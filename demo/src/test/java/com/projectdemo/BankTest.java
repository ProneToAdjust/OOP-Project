package com.projectdemo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import java.util.ArrayList;

public class BankTest {
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
    public void getNewUserUUIDTest() {
        sampleDB();
        for(int x=1;x<1000;x++){
            assertNotEquals(testUser.getUUID(), bank.getNewUserUUID());
        }
    }
    
    @Test
    public void getNewAccountUUIDTest() {
        sampleDB();
        for(int x=1;x<1000;x++){
            assertNotEquals(checkingAccount.getUUID(), bank.getNewAccountUUID());
        }
    }

    @Test
    public void userLoginTest(){
        sampleDB();
        User invalidUser0 = bank.userLogin("", "");
        assertEquals(null, invalidUser0);
        User invalidUser1 = bank.userLogin("", "8831");
        assertEquals(null, invalidUser1);
        User invalidUser2 = bank.userLogin(testUser.getUUID(), "");
        assertEquals(null, invalidUser2);
        User validUser = bank.userLogin(testUser.getUUID(), "8831");
        assertEquals(testUser, validUser);
    }

    @Test
    public void getNameTest(){
        bank = new Bank("Fleeca");
        String name = bank.getName();
        assertEquals("Fleeca", name);
    }

    @Test
    public void getAndAddUsersTest(){
        sampleDB();
        ArrayList<User> users = bank.getUsers();
        assertEquals(testUser , users.get(0));
        bank.addUser("Zefrom", "Ong", "9942");
        users = bank.getUsers();
        assertEquals(2 , users.size());
    }

    @Test
    public void getAccountsTest(){
        sampleDB();
        ArrayList<Account> accounts = bank.getAccounts();
        assertEquals(checkingAccount , accounts.get(1));
    }

    @Test
    public void selectBankAccTest(){
        sampleDB();
        int invalidBankAcc = bank.selectBankAcc("");
        assertEquals(-1, invalidBankAcc);
        int validBankAcc = bank.selectBankAcc(checkingAccount.getUUID());
        assertEquals(1, validBankAcc);
    }

    @Test
    public void uuidDataTest(){
        sampleDB();
        ArrayList<String> uuidData = bank.uuidData();
        assertEquals(checkingAccount.getUUID(), uuidData.get(1));
    }

    @Test
    public void getBalanceAndTransactionTest(){
        sampleDB();
        double balance = bank.getBalance(1);
        assertTrue(null, balance == 0);
        bank.addAcctTransaction(1,100,"deposit");
        balance = bank.getBalance(1);
        assertTrue(null, balance == 100);
    }

    @Test
    public void getAcctUUIDTest(){
        sampleDB();
        String UUID = bank.getAcctUUID(1);
        assertEquals(checkingAccount.getUUID(), UUID);
    }
}
