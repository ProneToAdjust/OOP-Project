import java.util.ArrayList;
import java.util.Scanner;

public class ATM {
	private Controller controller;
	private Scanner scanner;

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

	public ATM() {
		this.scanner = new Scanner(System.in);
		this.controller = new Controller();
	}

	/** Print the ATM's login menu.
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
		} while(loginResult == false); 	// continue looping until we have a successful login
	}
	
	/** Print the ATM's menu for user actions.
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
		if (choice != 6) 
			this.mainMenu(theBank);
	}

	/** Prints menu for user to select if they want to display transaction history.
	 */
	private void showAccountInformationMenu() {
		int accountChoice;
		int transactionChoice = -1;
		System.out.println(controller.getSummary());
		
		// prints account summary
		do {
			System.out.println("Do you want to show the account transaction history for the account?");
			System.out.println("1) Yes");
			System.out.println("2) No");
			System.out.printf("Enter the number (1-2): ");
			try{
				transactionChoice = Integer.parseInt(this.scanner.nextLine());
				if (transactionChoice < 1 || transactionChoice > 2)
					System.out.println("Invalid choice. Please choose 1-2.");
			}
			catch(Exception e) {
                System.out.println("Invalid choice. Please input an integer.");
            }
		} while (transactionChoice < 1 || transactionChoice > 2);

		if (transactionChoice == 1) {
			accountChoice = selectAccountMenu();
			transactionHistoryMenu(accountChoice);
		} else if (transactionChoice == 2) {
			System.out.println("\nExiting Account Information\n");
		}
	}

	/** Prints the transction history of select account.
	 */
	private void transactionHistoryMenu(int account) {
		// print the transaction history
		ArrayList<String> transHistories = controller.getTransactionHistory(account);

		System.out.printf("\nTransaction history:\n");
		for (String transSummary : transHistories) {
			System.out.println(transSummary);
		}
		System.out.println();
	}

	/** Print the Withdrawal menu for user actions.
	 */
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

		if(amount != 0)
			controller.withdrawFunds(selectedAcc, amount, "Withdrawal");
	}

	/** Print the Deposit menu for user actions.
	 */
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

	/** Print the Transfer menu for user actions.
	 */
	private void transferFundsMenu(Bank theBank) {
		int fromAcc;
		int toAcc;
		int typeTransfer;
		double transferAmt = -1;
		double acctBal;
		double transferLimit;

		System.out.println("Transfer funds");
		System.out.println(controller.getSummary());
		typeTransfer = getTypeTransfer();
		
		// get account to transfer from
		System.out.println("Account to transfer from:");
		fromAcc = selectAccountMenu();
		acctBal = controller.getAccountBalance(fromAcc);

		switch(typeTransfer){
			case 1: //internal transfer
				// get account to transfer to
				System.out.println("Account to transfer to");
				toAcc = selectAccountMenu();

				transferLimit = controller.getTransferLimit();
				break;
			case 2: //external transfer
				// get account to transfer to
				toAcc = selectExtAccountMenu(theBank);
				transferLimit = controller.getExternalTransferLimit();
				break;
            default:
				throw new IllegalAccessError("int typeTransfer was invalid");
        }

		// get amount to transfer
		do {
			System.out.printf("Enter the amount to transfer (max $%.02f): $", transferLimit);
			try{
				transferAmt = Double.parseDouble(this.scanner.nextLine());
				if (transferAmt < 0) 
					System.out.println("Amount must be greater than zero.");
				else if (transferAmt > acctBal) 
					System.out.printf("Amount must not be greater than balance " + 
					"of $%.2f.\n", acctBal);
				else if (transferAmt > transferLimit) 
					System.out.printf("Amount must not be greater than transfer limit " + 
					"of $%.2f.\n", transferLimit);
			}
			catch(Exception e) {
				System.out.println("Invalid choice. Please input a number.");
			}
		} while (transferAmt < 0 || transferAmt > acctBal || transferAmt > transferLimit);
	
		if(transferAmt!= 0){
			switch(typeTransfer){
				case 1: //internal transfer
					controller.transferFunds(fromAcc, toAcc, transferAmt);
    	            break;
    	        case 2: //external transfer
					controller.transferExtFunds(fromAcc, toAcc, transferAmt, theBank);
    	            break;
    	        default:
    	            throw new IllegalAccessError("int typeTransfer was invalid");
			}
		}
	}

	/** Prints menu for user to select internal account.
	 */
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

	/** Prints menu for user to select external bank account.
	 */
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

	/** Returns integer correlating to transfer type
	 * 1 = Internal transfer
	 * 2 = External transfer
	 */
	private int getTypeTransfer(){
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

	/** Print the Settings menu for user actions.
	 */
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
                changeLimitMenu("transfer");
				break;
			case 2:
                changeLimitMenu("external transfer");
				break;
			case 3:
                changeLimitMenu("withdrawal");
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
	
	/** Prints menu for user to change account limits.
	 * - Internal transfer limit
	 * - External transfer limit 
	 * - Withdrawal limit
	 */
	private void changeLimitMenu(String limitType) {
		double amount = -1;

		do {
			System.out.printf("Enter new " + limitType + " limit: $");
			try{
				amount = Double.parseDouble(this.scanner.nextLine());
                if (amount <= 0)
                    System.out.println("Amount must be greater than zero.");
			}
			catch(Exception e) {
                System.out.println("Invalid choice. Please input a number.");
            }
		} while (amount <= 0);
		
		switch(limitType){
			case "transfer":
				controller.changeTransferLimit(amount);
                break;
            case "external transfer":
				controller.changeExternalTransferLimit(amount);
                break;
            case "withdrawal":
                controller.changeWithdrawalLimit(amount);
                break;
            default:
                throw new IllegalAccessError("String limitType was invalid");
		}

		System.out.printf("New "+ limitType +" limit has been set to $%.2f", amount);
	}

	/** Prints menu for user to change pin
	 */
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
		else if(newPin == "")
			System.out.println("\nNew pin cannot be empty.\n");
		else if(controller.validatePin(currentPin)){
			controller.changePin(newPin);
            System.out.println("\nPin has been changed.\n");
		}
		else
			System.out.println("\nInvalid pin. Please try again.\n");
		
		settingsMenu();
	}
}
