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
	private void loginMenu(Bank theBank) {
		
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
	private void mainMenu(Bank theBank) {
		
		// init
		int choice = -1;

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

			try{
				choice = Integer.parseInt(this.scanner.nextLine());
				if (choice < 1 || choice > 6)
					System.out.println("Invalid choice. Please choose 1-6.");
			}
			catch(Exception e) {
                System.out.println("Invalid choice. Please input an integer.");
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
			default:
				throw new IllegalAccessError("int choice was invalid");
		}

		// redisplay this menu unless the user wants to quit
		if (choice != 6) {
			this.mainMenu(theBank);
		}
	}

	private void showAccountInformationMenu() {
		int accountchoice;
		int transactionchoice = -1;
		System.out.println(controller.getSummary());
		
		// prints account summary
		do {
			System.out.println("Do you want to show the account transaction history for the account?");
			System.out.println("1) Yes");
			System.out.println("2) No");
			System.out.printf("Enter the number (1-2): ");
			try{
				transactionchoice = Integer.parseInt(this.scanner.nextLine());
				if (transactionchoice < 1 || transactionchoice > 2)
					System.out.println("Invalid choice. Please choose 1-2.");
			}
			catch(Exception e) {
                System.out.println("Invalid choice. Please input an integer.");
            }
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
		double amount = -1;
		double accountBal;
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
			try{
				amount = Double.parseDouble(this.scanner.nextLine());
				if (amount < 0)
					System.out.println("Amount must be greater than zero.");
				else if (amount > accountBal)
					System.out.printf("Amount must not be greater than balance " + "of $%.02f.\n", accountBal);
				else if (amount > withdrawalLimit) 
					System.out.printf("Amount must not be greater than withdrawal limit " + "of $%.02f.\n", withdrawalLimit);
			}
			catch(Exception e) {
                System.out.println("Invalid choice. Please input a number.");
            }
		} while (amount < 0 || amount > accountBal || amount > withdrawalLimit);

		if(amount !=0)
			controller.withdrawFunds(selectedAcc, amount, "Withdrawal");
	}

	private void depositFundsMenu() {
		int selectedAcc;
		double amount = -1;

		System.out.println("Deposit funds");

		System.out.println(controller.getSummary());

		// get account to withdraw from
		selectedAcc = selectAccountMenu();
		
		// get amount to transfer
		do {
			System.out.printf("Enter the amount to deposit: $");
			try{
				amount = Double.parseDouble(this.scanner.nextLine());
				if (amount < 0) 
					System.out.println("Amount must be greater than zero.");
			}
			catch(Exception e) {
                System.out.println("Invalid choice. Please input a number.");
            }
		} while (amount < 0);

		if(amount != 0)
			controller.depositFunds(selectedAcc, amount, "Deposit");
	}

	private void transferFundsMenu(Bank theBank) {
		int fromAcc;
		int toAcc;
		int typeTransfer;
		double transferAmt = -1;
		double acctBal;
		double transferLimit;
		double externalTransferLimit;

		System.out.println("Transfer funds");

		System.out.println(controller.getSummary());
		
		// method to get the type of transfer user has selected
		typeTransfer = getTypeTransfer();
		// check using do while loop for the type of transfer user want to perform
		// if user want to do internal, typetransfer == 1 and the internal methods will run
		// if user want to do external, typetransfer == 2 and the external transfer methods will run
		
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
					try{
						transferAmt = Double.parseDouble(this.scanner.nextLine());
						if (transferAmt < 0) 
							System.out.println("Amount must be greater than zero.");
						else if (transferAmt > acctBal) 
							System.out.printf("Amount must not be greater than balance " + "of $%.2f.\n", acctBal);
						else if (transferAmt > transferLimit) 
							System.out.printf("Amount must not be greater than transfer limit " + "of $%.2f.\n",
									transferLimit);
					}
					catch(Exception e) {
                        System.out.println("Invalid choice. Please input a number.");
                    }
				} while (transferAmt < 0 || transferAmt > acctBal || transferAmt > transferLimit);

				if(transferAmt != 0)
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
					try{
						transferAmt = Double.parseDouble(this.scanner.nextLine());
						if (transferAmt < 0) 
							System.out.println("Amount must be greater than zero.");
						else if (transferAmt > acctBal) 
							System.out.printf("Amount must not be greater than balance " + "of $%.2f.\n", acctBal);
						else if (transferAmt > externalTransferLimit) 
							System.out.printf("Amount must not be greater than transfer limit " + "of $%.2f.\n",
									externalTransferLimit);
					}
					catch(Exception e) {
                        System.out.println("Invalid choice. Please input a number.");
                    }
				} while (transferAmt < 0 || transferAmt > acctBal || transferAmt > externalTransferLimit);

				if(transferAmt!= 0)
					controller.transferExtFunds(fromAcc, toAcc, transferAmt, theBank);
			}

			else {
				System.out.println("Invalid input. Please try again");
				return;
			}
		} while (typeTransfer < 0 || typeTransfer > 2);
	}

	private int selectAccountMenu(){
		int selectedAcc = -1;
		int numOfAccounts = controller.getNumberOfAccounts();
		
		do {
			System.out.printf("Enter the number (1-%d) of the account you would like to select: ", numOfAccounts);
			try{
				selectedAcc = Integer.parseInt(this.scanner.nextLine())-1;
                if (selectedAcc < 0 || selectedAcc >= numOfAccounts) 
                    System.out.println("Invalid account. Please try again.");
			}
			catch(Exception e) {
                System.out.println("Invalid choice. Please input an integer.");
            }
		} while (selectedAcc < 0 || selectedAcc >= numOfAccounts);

		return selectedAcc;
	}

	private int selectExtAccountMenu(Bank theBank){
		String uuid;
		
		do {
			System.out.printf("Enter the account UUID that you want to transfer to: \n");

			uuid = scanner.nextLine();
			// uuid must not be below or exceed 10 characters
			if (uuid.length() != 10) {
				System.out.println("Account UUID must have 10 characters. Please try again.");
			}
		} while (uuid.length() != 10);

		if (controller.getSelectedBankIndex(theBank, uuid) == -1)
		{
			System.out.println("Please try again!");
			selectExtAccountMenu(theBank);
		}

		return controller.getSelectedBankIndex(theBank, uuid);
	}


	private int getTypeTransfer()
	{
		int typeTransfer = -1;

		do {
			// prompts user to check for the type of transfer
			System.out.printf("1. Internal\n");
			System.out.printf("2. External (to 3rd party accounts)\n");
			System.out.printf("Enter the number (1-2) for the type of transfer " + 
			"you would like to perform: ");
			try{
				typeTransfer = Integer.parseInt(this.scanner.nextLine());
                if (typeTransfer < 1 || typeTransfer > 2 )
                    System.out.println("Invalid option. Please try again.");
			}
			catch(Exception e) {
                System.out.println("Invalid choice. Please input an integer.");
            }
		} while (typeTransfer < 1 || typeTransfer > 2 );

		return typeTransfer;
	}

	private void settingsMenu() {
		int choice = -1;

		do {
			
			System.out.println("Settings");
			System.out.println("  1) Transfer limit");
			System.out.println("  2) External transfer limit");
			System.out.println("  3) Withdrawal limit");
			System.out.println("  4) Change Pin No.");
			System.out.println("  5) Exit settings");
			System.out.println();
			System.out.print("Enter choice: ");

			try{
				choice = Integer.parseInt(this.scanner.nextLine());
                if (choice < 1 || choice > 5)
                    System.out.println("Invalid choice. Please choose 1-5.");
			}
			catch(Exception e) {
                System.out.println("Invalid choice. Please input an integer.");
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
			default:
				throw new IllegalAccessError("int choice was invalid");
		}
	}
	
	private void changeTransferLimitMenu() {
		double amount = -1;

		do {
			System.out.printf("Enter new transfer limit: $");
			try{
				amount = Double.parseDouble(this.scanner.nextLine());
                if (amount < 0)
                    System.out.println("Amount must be greater than zero.");
			}
			catch(Exception e) {
                System.out.println("Invalid choice. Please input a number.");
            }
		} while (amount < 0);
		
		controller.changeTransferLimit(amount);

		System.out.printf("Transfer limit has been set to $%.2f", amount);
	}

	private void changeExternalTransferLimitMenu() {
		double amount = -1;

		do {
			System.out.printf("Enter new external transfer limit: $");
			try{
				amount = Double.parseDouble(this.scanner.nextLine());
                if (amount < 0)
                    System.out.println("Amount must be greater than zero.");
			}
			catch(Exception e) {
                System.out.println("Invalid choice. Please input a number.");
            }
		} while (amount <= 0);
		
		controller.changeExternalTransferLimit(amount);

		System.out.printf("Transfer limit has been set to $%.2f", amount);
	}

	private void changeWithdrawalLimitMenu() {
		double amount = -1;

		do {
			System.out.printf("Enter new withdrawal limit: $");
			try{
				amount = Double.parseDouble(this.scanner.nextLine());
                if (amount < 0)
                    System.out.println("Amount must be greater than zero.");
			}
			catch(Exception e) {
                System.out.println("Invalid choice. Please input a number.");
            }
		} while (amount <= 0);
		
		controller.changeWithdrawalLimit(amount);

		System.out.printf("Withdrawal limit has been set to $%.2f", amount);
	}

	private void changePinNoMenu() {
		String currentPin, newPin, rePin;

		System.out.println();
		System.out.printf("Please enter the current pin: ");
		currentPin = scanner.nextLine();
		System.out.printf("Please enter the new pin: ");
		newPin = scanner.nextLine();
		System.out.printf("Please re-enter the new pin: ");
		rePin = scanner.nextLine();

		if(!newPin.equals(rePin))
			System.out.println("\nNew pin does not match.\n");
		else if(controller.validatePin(currentPin)){
			controller.changePin(newPin);
            System.out.println("\nPin has been changed.\n");
		}
		else
			System.out.println("\nInvalid pin. Please try again.\n");
		
		settingsMenu();
	}
}
