import java.util.ArrayList;
import java.util.Scanner;

public class ATM {
	
	public static void main(String[] args) {
		
		ATM atm = new ATM();
		
		// init Bank
		Bank bank = new Bank("Fleeca");
		
		// add a user, which also creates a Savings account
		User testUser = bank.addUser("Sibei", "Suei", "4444");
		
		// add a checking account for our user
		Account checkingAccount = new Account("Checking", testUser, bank);
		testUser.addAccount(checkingAccount);
		bank.addAccount(checkingAccount);
		
		// continue looping forever
		while (true) {
			
			// stay in login prompt until successful login
			atm.loginMenu(bank);
			
			// stay in main menu until user quits
			atm.mainMenu();
			
		}

	}

	private Controller controller;
	private Scanner scanner;
	private User currentUser;

	public ATM() {
		this.scanner = new Scanner(System.in);
		this.controller = new Controller();
	}
	
	/**
	 * Print the ATM's login menu.
	 * @param theBank	the Bank object whose accounts to use
	 * @param sc		the Scanner objec to use for user input
	 */
	public User loginMenu(Bank theBank) {
		
		// inits
		String userID;
		String pin;
		boolean loginResult;
		
		// prompt user for user ID/pin combo until a correct one is reached
		do {
			
			System.out.printf("\n\nWelcome to %s\n\n", theBank.getName());		
			System.out.print("Enter user ID: ");
			userID = this.scanner.nextLine();
			System.out.print("Enter pin: ");
			pin = this.scanner.nextLine();
			
			// try to get user object corresponding to ID and pin combo
			currentUser = theBank.userLogin(userID, pin);

			loginResult = controller.loginUser(theBank, userID, pin);
			if (loginResult == false) {
				System.out.println("Incorrect user ID/pin combination. " + 
						"Please try again");
			}
			
		} while(loginResult == false); 	// continue looping until we have a  
									// successful login
		
		return currentUser;
		
	}
	
	/**
	 * Print the ATM's menu for user actions.
	 */
	public void mainMenu() {
		
		// print a summary of the user's accounts
		controller.printSummary();;
		
		// init
		int choice;
		
		// user menu
		do {
			
			System.out.println("What would you like to do?");
			System.out.println("  1) Show account transaction history");
			System.out.println("  2) Withdraw");
			System.out.println("  3) Deposit");
			System.out.println("  4) Transfer");
			System.out.println("  5) Quit");
			System.out.println();
			System.out.print("Enter choice: ");
			choice = this.scanner.nextInt();
			
			if (choice < 1 || choice > 5) {
				System.out.println("Invalid choice. Please choose 1-5.");
			}
			
		} while (choice < 1 || choice > 5);
		
		// process the choice
		switch (choice) {
		
		case 1:
			this.transactionHistoryMenu();
			break;
		case 2:
			withdrawFundsMenu();
			break;
		case 3:
			depositFundsMenu();
			break;
		case 4:
			transferFundsMenu();
			break;
		case 5:
			// gobble up rest of previous input
			this.scanner.nextLine();
			break;
		}
		
		// redisplay this menu unless the user wants to quit
		if (choice != 5) {
			this.mainMenu();
		}
		
	}

	private void transactionHistoryMenu() {

		int theAcct;
		
		// get account whose transactions to print
		do {
			System.out.printf("Enter the number (1-%d) of the account\nwhose " + 
					"transactions you want to see: ", currentUser.numAccounts());
			theAcct = scanner.nextInt()-1;
			if (theAcct < 0 || theAcct >= currentUser.numAccounts()) {
				System.out.println("Invalid account. Please try again.");
			}
		} while (theAcct < 0 || theAcct >= currentUser.numAccounts());
		
		// print the transaction history
		
		ArrayList<Transaction> transactions = controller.getTransactionHistory(currentUser, theAcct);

		System.out.printf("\nTransaction history:\n");
		for (int t = transactions.size()-1; t >= 0; t--) {
			System.out.println(transactions.get(t).getSummaryLine());
		}
		System.out.println();

	}

	private void withdrawFundsMenu() {
		int fromAcct;
		double amount;
		double acctBal;
		String memo;
		
		// get account to withdraw from
		do {
			System.out.printf("Enter the number (1-%d) of the account to " + 
					"withdraw from: ", currentUser.numAccounts());
			fromAcct = scanner.nextInt()-1;
			if (fromAcct < 0 || fromAcct >= currentUser.numAccounts()) {
				System.out.println("Invalid account. Please try again.");
			}
		} while (fromAcct < 0 || fromAcct >= currentUser.numAccounts());
		acctBal = currentUser.getAcctBalance(fromAcct);
		
		// get amount to transfer
		do {
			System.out.printf("Enter the amount to withdraw (max $%.02f): $", 
					acctBal);
			amount = scanner.nextDouble();
			if (amount < 0) {
				System.out.println("Amount must be greater than zero.");
			} else if (amount > acctBal) {
				System.out.printf("Amount must not be greater than balance " +
						"of $%.02f.\n", acctBal);
			}
		} while (amount < 0 || amount > acctBal);

		// gobble up rest of previous input
		scanner.nextLine();
		
		// get a memo
		System.out.print("Enter a memo: ");
		memo = scanner.nextLine();

		controller.withdrawFunds(currentUser, fromAcct, amount, memo);
	}

	private void depositFundsMenu() {
		int toAcct;
		double amount;
		String memo;

		// get account to withdraw from
		do {
			System.out.printf("Enter the number (1-%d) of the account to " + 
					"deposit to: ", currentUser.numAccounts());
			toAcct = scanner.nextInt()-1;
			if (toAcct < 0 || toAcct >= currentUser.numAccounts()) {
				System.out.println("Invalid account. Please try again.");
			}
		} while (toAcct < 0 || toAcct >= currentUser.numAccounts());
		
		// get amount to transfer
		do {
			System.out.printf("Enter the amount to deposit: $");
			amount = scanner.nextDouble();
			if (amount < 0) {
				System.out.println("Amount must be greater than zero.");
			} 
		} while (amount < 0);
		
		// gobble up rest of previous input
		scanner.nextLine();
		
		// get a memo
		System.out.print("Enter a memo: ");
		memo = scanner.nextLine();

		controller.depositFunds(currentUser, toAcct, amount, memo);
	}

	private void transferFundsMenu() {
		int fromAcct;
		int toAcct;
		double amount;
		double acctBal;
		
		// get account to transfer from
		do {
			System.out.printf("Enter the number (1-%d) of the account to " + 
					"transfer from: ", currentUser.numAccounts());
			fromAcct = scanner.nextInt()-1;
			if (fromAcct < 0 || fromAcct >= currentUser.numAccounts()) {
				System.out.println("Invalid account. Please try again.");
			}
		} while (fromAcct < 0 || fromAcct >= currentUser.numAccounts());
		acctBal = currentUser.getAcctBalance(fromAcct);
		
		// get account to transfer to
		do {
			System.out.printf("Enter the number (1-%d) of the account to " + 
					"transfer to: ", currentUser.numAccounts());
			toAcct = scanner.nextInt()-1;
			if (toAcct < 0 || toAcct >= currentUser.numAccounts()) {
				System.out.println("Invalid account. Please try again.");
			}
		} while (toAcct < 0 || toAcct >= currentUser.numAccounts());
		
		// get amount to transfer
		do {
			System.out.printf("Enter the amount to transfer (max $%.02f): $", 
					acctBal);
			amount = scanner.nextDouble();
			if (amount < 0) {
				System.out.println("Amount must be greater than zero.");
			} else if (amount > acctBal) {
				System.out.printf("Amount must not be greater than balance " +
						"of $.02f.\n", acctBal);
			}
		} while (amount < 0 || amount > acctBal);

		controller.transferFunds(currentUser, fromAcct, toAcct, amount);
	}

}
