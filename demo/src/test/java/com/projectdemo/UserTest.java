package com.projectdemo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import java.util.ArrayList;

public class UserTest {
    

    Account testAccount1;
    Account testAccount2;
	User testUser;
	Bank bank;
    Controller testController;
	
	
	public void setUp() {
        bank = new Bank("test");
        testUser = bank.addUser("test", "case", "1234");
		testAccount1 = new Account("Checking", testUser, bank);
        testUser.addAccount(testAccount1);
        testAccount2 = new Account("Checking", testUser, bank);
        testUser.addAccount(testAccount2);
        testController = new Controller();
	}




    @Test
    public void testGetUUID() {
        setUp();
        String uuid = testUser.getUUID();
        assertTrue(uuid!= null);
        assertTrue(uuid.length() > 0);
    }


    @Test
    public void testAddAccount() {
        setUp();
        Account account3 = new Account("Credit", testUser, bank);
        testUser.addAccount(account3);
        assertEquals(1, testController.getNumOfAccFromBank(bank));
    }

    @Test
    public void testNumAccounts() {
        setUp();
        
        assertEquals(1, testController.getNumOfAccFromBank(bank));
    }
    


    @Test
    public void testGetAccount() {
        setUp();
        assertEquals(testAccount1, testUser.getAccount(1));
        assertEquals(testAccount2, testUser.getAccount(2));
    }


    @Test
    public void testGetAcctBalance() {
        setUp();
        assertEquals(0, testUser.getAcctBalance(0), 0);
        assertEquals(0, testUser.getAcctBalance(1), 0);
    }


    @Test
    public void testGetAcctUUID() {
        setUp();
        String uuid1 = testAccount1.getUUID();
        String uuid2 = testAccount2.getUUID();
        assertEquals(uuid1, testUser.getAcctUUID(1));
        assertEquals(uuid2, testUser.getAcctUUID(2));
    }


    @Test
    public void testAddAcctTransaction() {
        setUp();
        testUser.addAcctTransaction(0, 100, "deposit");
        testUser.addAcctTransaction(0, -50, "withdrawal");
        assertEquals(50, testUser.getAcctBalance(0), 0);
    }

    @Test
    public void testValidatePin() {
        setUp();
        assertTrue(testUser.validatePin("1234"));
        assertTrue(false == testUser.validatePin("1111"));
    }


    
    @Test
    public void testGetTransferLimit() {
        setUp();
        assertEquals(10000, testUser.getTransferLimit(), 0);
    }

}