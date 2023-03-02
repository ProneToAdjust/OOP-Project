import java.util.ArrayList;
import java.util.Scanner;

public class ATM {
	
	public static void main(String[] args) {
		
		ATM atm = new ATM();
		
		// init Bank
		Bank bank = new Bank("Fleeca");//fleeca bank
		
		// add two users, which also creates a Savings account
		User testUser = bank.addUser("Sibei", "Suei", "4444");
		User testUser2 = bank.addUser("Ryan", "Ong", "5555");

		// add a checking account for our user
		Account checkingAccount = new Account("Checking", testUser, bank);
		Account checkingAccount2 = new Account("Checking", testUser2, bank);
		testUser.addAccount(checkingAccount);
		testUser2.addAccount(checkingAccount2);
		bank.addAccount(checkingAccount);
		bank.addAccount(checkingAccount2);
		
		// continue looping forever
		while (true) {
			
			// stay in login prompt until successful login
			atm.loginMenu(bank);
			// stay in main menu until user quits
			atm.mainMenu(bank);
			
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
	public void mainMenu(Bank theBank) {
		
		// init
		int choice;

		System.out.printf("\nHello, %s", controller.getUserName());
		
		// user menu
		do {
			
			System.out.println("\n\nWhat would you like to do?");
			System.out.println("  1) Show Account information");
			System.out.println("  2) Withdraw");
			System.out.println("  3) Deposit");
			System.out.println("  4) Transfer");
			System.out.println("  5) Settings");
			System.out.println("  6) Log out");
			System.out.println();
			System.out.print("Enter choice: ");
			choice = this.scanner.nextInt();
			
			if (choice < 1 || choice > 6) {
				System.out.println("Invalid choice. Please choose 1-6.");
			}
			
		} while (choice < 1 || choice > 6);

		System.out.println();
		
		// process the choice
		switch (choice) {
		
		case 1:
			showAccountInformationMenu();
			break;
		case 2:
			withdrawFundsMenu();
			break;
		case 3:
			depositFundsMenu();
			break;
		case 4:
			transferFundsMenu(theBank);
			break;
		case 5:
			settingsMenu();
			break;
		case 6:
			System.out.println("Successfully logged out");
			break;
		}
		
		// redisplay this menu unless the user wants to quit
		if (choice != 6) {
			this.mainMenu(theBank);
		}
		
	}

	private void showAccountInformationMenu() {
		int accountchoice;
		int transactionchoice;
		System.out.println(controller.getSummary());
		// prints account summary
		do {
			System.out.print(
					"Do you want to show the account transaction history for the account?\n1) Yes\n2) No\nEnter the number (1-2): ");
			transactionchoice = scanner.nextInt();

		} while (transactionchoice < 1 || transactionchoice > 2);
		if (transactionchoice == 1) {

			accountchoice = selectAccountMenu();
			transactionHistoryMenu(accountchoice);

		} else if (transactionchoice == 2) {
			System.out.println("\nExiting Account Information\n");
		}
	}

	private void transactionHistoryMenu(int account) {
		// print the transaction history
		ArrayList<String> transHistories = controller.getTransactionHistory(account);

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
		double withdrawalLimit;

		System.out.println("Withdraw funds");
		
		System.out.println(controller.getSummary());
		
		// get account to withdraw from
		selectedAcc = selectAccountMenu();
		accountBal = controller.getAccountBalance(selectedAcc);
		withdrawalLimit = controller.getWithdrawalLimit();
		
		// get amount to transfer
		do {
			System.out.printf("Enter the amount to withdraw (max $%.2f): $", withdrawalLimit);
			amount = scanner.nextDouble();

			if (amount < 0) {
				System.out.println("Amount must be greater than zero.");
			} else if (amount > accountBal) {
				System.out.printf("Amount must not be greater than balance " + "of $%.02f.\n", accountBal);
			} else if (amount > withdrawalLimit) {
				System.out.printf("Amount must not be greater than withdrawal limit " + "of $%.02f.\n", withdrawalLimit);
			}

		} while (amount < 0 || amount > accountBal || amount > withdrawalLimit);
		
		// get a memo
		System.out.print("Enter a memo: ");
		memo = scanner.next();

		controller.withdrawFunds(selectedAcc, amount, memo);
	}

	private void depositFundsMenu() {
		int selectedAcc;
		double amount;
		String memo;

		System.out.println("Deposit funds");

		System.out.println(controller.getSummary());

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
		memo = scanner.next();

		controller.depositFunds(selectedAcc, amount, memo);
	}

	private void transferFundsMenu(Bank theBank) {
		int fromAcc;
		int toAcc;
		int typeTransfer;
		double transferAmt;
		double acctBal;
		double transferLimit;
		double externalTransferLimit;

		System.out.println("Transfer funds");

		System.out.println(controller.getSummary());
		
		// method to get the type of transfer user has selected
		typeTransfer = getTypeTransfer();
		// check using do while loop for the type of transfer user want to perform
		// if user want to do internal, typetransfer == 1 and the internal methods will run
		// if user want to do external, typetransfer == 2 and the externam transfer methods will run
		
		do {
			if (typeTransfer == 1) {

				// get account to transfer from
				System.out.println("Account to transfer from:");
				fromAcc = selectAccountMenu();
				acctBal = controller.getAccountBalance(fromAcc);

				// get account to transfer to
				System.out.println("Account to transfer to");
				toAcc = selectAccountMenu();

				transferLimit = controller.getTransferLimit();

				// get amount to transfer
				do {
					System.out.printf("Enter the amount to transfer (max $%.02f): $", transferLimit);

					transferAmt = scanner.nextDouble();

					if (transferAmt < 0) {
						System.out.println("Amount must be greater than zero.");
					} else if (transferAmt > acctBal) {
						System.out.printf("Amount must not be greater than balance " + "of $%.2f.\n", acctBal);
					} else if (transferAmt > transferLimit) {
						System.out.printf("Amount must not be greater than transfer limit " + "of $%.2f.\n",
								transferLimit);
					}

				} while (transferAmt < 0 || transferAmt > acctBal || transferAmt > transferLimit);

				controller.transferFunds(fromAcc, toAcc, transferAmt);
			}

			else if (typeTransfer == 2) {
				// get account to transfer from
				System.out.println("Account to transfer from:");
				fromAcc = selectAccountMenu();
				acctBal = controller.getAccountBalance(fromAcc);

				// get account to transfer to
				toAcc = selectExtAccountMenu(theBank);

				externalTransferLimit = controller.getExternalTransferLimit();

				// get amount to transfer
				do {
					System.out.printf("Enter the amount to transfer (max $%.02f): $", externalTransferLimit);

					transferAmt = scanner.nextDouble();

					if (transferAmt < 0) {
						System.out.println("Amount must be greater than zero.");
					} else if (transferAmt > acctBal) {
						System.out.printf("Amount must not be greater than balance " + "of $%.2f.\n", acctBal);
					} else if (transferAmt > externalTransferLimit) {
						System.out.printf("Amount must not be greater than transfer limit " + "of $%.2f.\n",
								externalTransferLimit);
					}

				} while (transferAmt < 0 || transferAmt > acctBal || transferAmt > externalTransferLimit);

				controller.transferExtFunds(fromAcc, toAcc, transferAmt, theBank);
			}

			else {
				System.out.println("Invalid input. Please try again");
			}
		} while (typeTransfer < 0 || typeTransfer > 2);
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

	private int selectExtAccountMenu(Bank theBank){
		String uuid;
		
		do {
			System.out.printf("Enter the account UUID that you want to transfer to: \n");

			uuid = scanner.next();
			// uuid must not be below or exceed 10 characters
			if (uuid.length() != 10) {
				System.out.println("Account UUID must have 10 characters. Please try again.");
			}
		} while (uuid.length() != 10);

		return controller.getSelectedBankIndex(theBank, uuid);
	}


	public int getTypeTransfer()
	{
		int typeTransfer;
		do {
			// prompts user to check for the type of transfer
			System.out.printf("Enter the number (1-2) for the type of transfer " + 
			"you would like to perform \n");
			System.out.printf("1. Internal\n");
			System.out.printf("2. External (to 3rd party accounts)\n");
			typeTransfer = scanner.nextInt();
			if (typeTransfer < 1 || typeTransfer > 2 ) {
				System.out.println("Invalid option. Please try again.");
			}
		} while (typeTransfer < 0 || typeTransfer > 2 );
		return typeTransfer;
	}

	private void settingsMenu() {
		int choice;

		do {
			
			System.out.println("Settings");
			System.out.println("  1) Transfer limit");
			System.out.println("  2) External transfer limit");
			System.out.println("  3) Withdrawal limit");
			System.out.println("  4) Change Pin No.");
			System.out.println("  5) Exit settings");
			System.out.println();
			System.out.print("Enter choice: ");
			choice = this.scanner.nextInt();
			
			if (choice < 1 || choice > 5) {
				System.out.println("Invalid choice. Please choose 1-5.");
			}
			
		} while (choice < 1 || choice > 5);

		switch (choice) {
			case 1:
				changeTransferLimitMenu();
				break;
			case 2:
				changeExternalTransferLimitMenu();
				break;
			case 3:
				changeWithdrawalLimitMenu();
				break;
			case 4:
				changePinNoMenu();
				break;
			case 5:
                break;
		}
	}
	
	private void changeTransferLimitMenu() {
		double amount;

		do {
			System.out.printf("Enter new transfer limit: $");
			amount = scanner.nextDouble();
			if (amount < 0) {
				System.out.println("Amount must be greater than zero.");
			} 
		} while (amount < 0);
		
		controller.changeTransferLimit(amount);

		System.out.printf("Transfer limit has been set to $%.2f", amount);
	}

	private void changeExternalTransferLimitMenu() {
		double amount;

		do {
			System.out.printf("Enter new external transfer limit: $");
			amount = scanner.nextDouble();
			if (amount < 0) {
				System.out.println("Amount must be greater than zero.");
			} 
		} while (amount < 0);
		
		controller.changeExternalTransferLimit(amount);

		System.out.printf("Transfer limit has been set to $%.2f", amount);
	}

	private void changeWithdrawalLimitMenu() {
		double amount;

		do {
			System.out.printf("Enter new withdrawal limit: $");
			amount = scanner.nextDouble();
			if (amount < 0) {
				System.out.println("Amount must be greater than zero.");
			} 
		} while (amount < 0);
		
		controller.changeWithdrawalLimit(amount);

		System.out.printf("Withdrawal limit has been set to $%.2f", amount);
	}

	private void changePinNoMenu() {
		String currentPin, newPin, rePin;

		System.out.println();
		System.out.printf("Please enter the current pin: ");
		currentPin = scanner.next();
		System.out.printf("Please enter the new pin: ");
		newPin = scanner.next();
		System.out.printf("Please re-enter the new pin: ");
		rePin = scanner.next();

		if(!newPin.equals(rePin)){
			System.out.println();
			System.out.println("New pin does not match.");
			System.out.println();
			settingsMenu();
		}
		else if(controller.validatePin(currentPin)){
			controller.changePin(newPin);
			System.out.println();
            System.out.println("Pin has been changed.");
			System.out.println();
            settingsMenu();
		}
		else{
			System.out.println();
			System.out.println("Invalid pin. Please try again.");
			System.out.println();
            settingsMenu();
		}

	}
}
