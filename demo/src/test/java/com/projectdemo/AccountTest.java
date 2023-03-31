package com.projectdemo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import java.util.ArrayList;
public class AccountTest {
	

    Account checkingAccount;
	User testUser;
	Bank bank;
	
	
	public void setUp() {
        bank = new Bank("test");
        testUser = bank.addUser("test", "case", "1234");
		checkingAccount = new Account("Checking", testUser, bank);
        testUser.addAccount(checkingAccount);
        bank.addAccount(checkingAccount);
	}

    @Test
    public void getUUIDTest() {
        setUp();
        
            assertEquals(checkingAccount.getUUID(), bank.getAcctUUID(1));
        
    }
    
    @Test
    public void addTransactionTest() {
        setUp();
        double temp = 0;
        for(int x=1;x<10;x++){
        checkingAccount.addTransaction(x);
        temp += x;
        assertTrue(checkingAccount.getBalance()==temp);
        }
        
    }

    @Test
	public void GetSummaryLineTest() {
        setUp();
		checkingAccount.addTransaction(100.0);
		
		String summary = checkingAccount.getSummaryLine();
		assertEquals((bank.getAcctUUID(1)+" : $100.00 : Checking"), summary);
			
	}

}