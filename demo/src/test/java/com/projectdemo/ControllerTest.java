package com.projectdemo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import java.util.ArrayList;

public class ControllerTest {

    Account checkingAccount;
    Account savingsAccount;
	User testUser;
	Bank bank;
    Controller testController;
	
	
	public void setUp() {
        bank = new Bank("test");
        testUser = bank.addUser("test", "case", "1234");
		checkingAccount = new Account("Checking", testUser, bank);
        testUser.addAccount(checkingAccount);
        testController = new Controller();
        testController.loginUser(bank, testUser.getUUID(), "1234");
	}


    @Test
	public void testWithdrawFunds() {
		// test withdrawing funds from an account
        setUp();
		testController.withdrawFunds(0, 100.00, "Test Withdrawal");
		assertEquals(-100.00, testController.getAccountBalance(0), 0.001);
	}


    @Test
	public void testDepositFunds() {
		// test depositing funds to an account
        setUp();
		testController.depositFunds(0, 50.00, "Test Deposit");
		assertEquals(50.00, testController.getAccountBalance(0), 0.001);
	}



    @Test
	public void testTransferFunds() {
		// test transferring funds between accounts
        setUp();
        checkingAccount = new Account("Checking2", testUser, bank);
        testUser.addAccount(checkingAccount);
		testController.transferFunds(0, 1, 50.00);
		assertEquals(-50.00, testController.getAccountBalance(0), 0.001);
		assertEquals(50.00, testController.getAccountBalance(1), 0.001);
	}



    @Test
	public void testTransferExtFunds() {
		// test transferring funds to an external bank account
        setUp();
		// test transferring funds to an external bank account
		Bank externalBank = new Bank("External Bank");
		User externalUser = externalBank.addUser("External", "User", "5678");
        checkingAccount = new Account("Checking2", testUser, bank);
        testUser.addAccount(checkingAccount);
		externalUser.addAccount(checkingAccount);
        savingsAccount = new Account("Savings", testUser, bank);
        testUser.addAccount(savingsAccount);
		externalUser.addAccount(savingsAccount);
		testController.transferExtFunds(0, 0, 100.00, externalBank);
		assertEquals(-100.00, testController.getAccountBalance(0), 0.001);
		assertEquals(100.00, externalBank.getAccounts().get(0).getBalance(), 0.001);
	}


    @Test
	public void testGetTransactionHistory() {
        setUp();
		// test getting transaction history for an account
		testController.depositFunds(0, 100.00, "Test Deposit");
		ArrayList<String> transactionHistory = testController.getTransactionHistory(0);
		assertEquals(1, transactionHistory.size());
		assertEquals("$100.00 : Test Deposit", transactionHistory.get(0).substring(36));
	}
    

    @Test
	public void testgetNumberOfAccounts() {
        setUp();
		// test getting the number of accounts from a bank
		assertEquals(1, testController.getNumOfAccFromBank(bank));
	}


    @Test
    public void testGetAccountBalance() {
        setUp();
        assertEquals(0.0, testController.getAccountBalance(0), 0.01);
    }


    @Test
    public void testChangeTransferLimit() {
        setUp();
        testController.changeTransferLimit(500.0);
        assertEquals(500.0, testController.getTransferLimit(), 0.01);
    }

    @Test
    public void testChangeExternalTransferLimit() {
        setUp();
        testController.changeExternalTransferLimit(1000.0);
        assertEquals(1000.0, testController.getExternalTransferLimit(), 0.01);
    }

    @Test
    public void testChangeWithdrawalLimit() {
        setUp();
        testController.changeWithdrawalLimit(200.0);
        assertEquals(200.0, testController.getWithdrawalLimit(), 0.01);
    }

    @Test
    public void testGetUserName() {
        setUp();
        assertEquals("test case", testController.getUserName());
    }

    @Test
    public void testValidatePin() {
        setUp();
        assertTrue(testController.validatePin("1234"));
        assertTrue(false == testController.validatePin("5678"));
    }

    @Test
    public void testChangePin() {
        setUp();
        testController.changePin("5678");
        assertTrue(testController.validatePin("5678"));
    }


   

    
}
