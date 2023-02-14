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

	public ATM() {
		this.scanner = new Scanner(System.in);
		this.controller = new Controller();
	}
	
	/**
	 * Print the ATM's login menu.
	 * @param theBank	the Bank object whose accounts to use
	 * @param sc		the Scanner objec to use for user input
	 */
	public void loginMenu(Bank theBank) {
		
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

			loginResult = controller.loginUser(theBank, userID, pin);
			if (loginResult == false) {
				System.out.println("Incorrect user ID/pin combination. " + 
						"Please try again");
			}
			
		} while(loginResult == false); 	// continue looping until we have a  
									// successful login
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

		System.out.println();
		
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
		System.out.println("Account transaction history");

		// get account to view transaction history
		int selectedAcc = selectAccountMenu();
		
		// print the transaction history
		ArrayList<String> transHistories = controller.getTransactionHistory(selectedAcc);

		System.out.printf("\nTransaction history:\n");
		for (String transSummary : transHistories) {
			System.out.println(transSummary);
		}
		System.out.println();

	}

	private void withdrawFundsMenu() {
		int selectedAcc;
		double amount;
		double accountBal;
		String memo;

		System.out.println("Withdraw funds");
		
		// get account to withdraw from
		selectedAcc = selectAccountMenu();
		accountBal = controller.getAccountBalance(selectedAcc);
		
		// get amount to transfer
		do {
			System.out.printf("Enter the amount to withdraw (max $%.02f): $", 
					accountBal);
			amount = scanner.nextDouble();
			if (amount < 0) {
				System.out.println("Amount must be greater than zero.");
			} else if (amount > accountBal) {
				System.out.printf("Amount must not be greater than balance " +
						"of $%.02f.\n", accountBal);
			}
		} while (amount < 0 || amount > accountBal);
		
		// get a memo
		System.out.print("Enter a memo: ");
		memo = scanner.nextLine();

		controller.withdrawFunds(selectedAcc, amount, memo);
	}

	private void depositFundsMenu() {
		int selectedAcc;
		double amount;
		String memo;

		System.out.println("Deposit funds");

		// get account to withdraw from
		selectedAcc = selectAccountMenu();
		
		// get amount to transfer
		do {
			System.out.printf("Enter the amount to deposit: $");
			amount = scanner.nextDouble();
			if (amount < 0) {
				System.out.println("Amount must be greater than zero.");
			} 
		} while (amount < 0);
		
		// get a memo
		System.out.print("Enter a memo: ");
		memo = scanner.nextLine();

		controller.depositFunds(selectedAcc, amount, memo);
	}

	private void transferFundsMenu() {
		int fromAcc;
		int toAcc;
		double transferAmt;
		double acctBal;

		System.out.println("Transfer funds");
		
		// get account to transfer from
		System.out.println("Account to transfer from:");
		fromAcc = selectAccountMenu();
		acctBal = controller.getAccountBalance(fromAcc);
		
		// get account to transfer to
		System.out.println("Account to transfer to");
		toAcc = selectAccountMenu();
		
		// get amount to transfer
		do {
			System.out.printf("Enter the amount to transfer (max $%.02f): $", 
					acctBal);
			transferAmt = scanner.nextDouble();
			if (transferAmt < 0) {
				System.out.println("Amount must be greater than zero.");
			} else if (transferAmt > acctBal) {
				System.out.printf("Amount must not be greater than balance " +
						"of $.02f.\n", acctBal);
			}
		} while (transferAmt < 0 || transferAmt > acctBal);

		controller.transferFunds(fromAcc, toAcc, transferAmt);
	}

	private int selectAccountMenu(){
		int selectedAcc;
		int numOfAccounts = controller.getNumberOfAccounts();
		
		do {
			System.out.printf("Enter the number (1-%d) of the account you would like to select: ", numOfAccounts);

			selectedAcc = scanner.nextInt()-1;

			if (selectedAcc < 0 || selectedAcc >= numOfAccounts) {
				System.out.println("Invalid account. Please try again.");
			}
		} while (selectedAcc < 0 || selectedAcc >= numOfAccounts);

		return selectedAcc;
	}

}
