
import java.security.MessageDigest;
import java.util.ArrayList;

public class User {

	/**
	 * The first name of the user.
	 */
	private String firstName;
	
	/**
	 * The last name of the user.
	 */
	private String lastName;
	
	/**
	 * The ID number of the user.
	 */
	private String uuid;
	
	/**
	 * The hash of the user's pin number.
	 */
	private byte pinHash[];
	
	/**
	 * The list of accounts for this user.
	 */
	private ArrayList<Account> accounts;
	
	/**
	 *  Transfer limits for this user
	 */
	private double transferLimit;

	/**
	 *  External transfer limits for this user
	 */
	private double externalTransferLimit;

	/**
	 * Withdrawal limits for this user
	 */
	private double withdrawalLimit;

	/**
	 * Create new user
	 * @param firstName	the user's first name
	 * @param lastName	the user's last name
	 * @param pin				the user's account pin number (as a String)
	 * @param theBank		the bank that the User is a customer of
	 */
	public User (String firstName, String lastName, String pin, Bank theBank) {
		
		// set user's name
		this.firstName = firstName;
		this.lastName = lastName;
		
		// store the pin's MD5 hash, rather than the original value, for 
		// security reasons
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			this.pinHash = md.digest(pin.getBytes());
		} catch (Exception e) {
			System.err.println("error, caught exeption : " + e.getMessage());
			System.exit(1);
		}
		
		// get a new, unique universal unique ID for the user
		this.uuid = theBank.getNewUserUUID();
		
		// create empty list of accounts
		this.accounts = new ArrayList<Account>();

		// set default transfer and withdrawal limits
		this.transferLimit = 10000;
		this.externalTransferLimit = 10000;
		this.withdrawalLimit = 10000;
		
		// print log message
		System.out.printf("New user %s, %s with ID %s created.\n", 
				lastName, firstName, this.uuid);
		
	}
	
	/**
	 * Get the user ID number
	 * @return	the uuid
	 */
	public String getUUID() {
		return this.uuid;
	}
	
	/**
	 * Add an account for the user.
	 * @param anAcct	the account to add
	 */
	public void addAccount(Account anAcct) {
		this.accounts.add(anAcct);
	}
	
	/**
	 * Get the number of accounts the user has.
	 * @return	the number of accounts
	 */
	public int numAccounts() {
		return this.accounts.size();
	}

		/**
	 * Get the balance of a particular account.
	 * @param accountIndex	the index of the account to use
	 * @return			the balance of the account
	 */
	public Account getAccount(int accountIndex){
		return accounts.get(accountIndex);

	}
	
	/**
	 * Get the balance of a particular account.
	 * @param acctIdx	the index of the account to use
	 * @return			the balance of the account
	 */
	public double getAcctBalance(int acctIdx) {
		return this.accounts.get(acctIdx).getBalance();
	}
	
	/**
	 * Get the UUID of a particular account.
	 * @param acctIdx	the index of the account to use
	 * @return			the UUID of the account
	 */
	public String getAcctUUID(int acctIdx) {
		return this.accounts.get(acctIdx).getUUID();
	}
	
	/**
	 * Print transaction history for a particular account.
	 * @param acctIdx	the index of the account to use
	 */
	public void printAcctTransHistory(int acctIdx) {
		this.accounts.get(acctIdx).getTransHistory();
	}
	
	/**
	 * Add a transaction to a particular account.
	 * @param acctIdx	the index of the account
	 * @param amount	the amount of the transaction
	 * @param memo		the memo of the transaction
	 */
	public void addAcctTransaction(int acctIdx, double amount, String memo) {
		this.accounts.get(acctIdx).addTransaction(amount, memo);
	}
	
	/**
	 * Check whether a given pin matches the true User pin
	 * @param aPin	the pin to check
	 * @return		whether the pin is valid or not
	 */
	public boolean validatePin(String aPin) {
		
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			return MessageDigest.isEqual(md.digest(aPin.getBytes()), 
					this.pinHash);
		} catch (Exception e) {
			System.err.println("error, caught exeption : " + e.getMessage());
			System.exit(1);
		}
		
		return false;
	}
	
	/**
	 * Print summaries for the accounts of this user.
	 */
	public String getAccountsSummary() {
		String summary = String.format("\n%s %s's accounts summary\n", firstName, lastName);
		
		for (int a = 0; a < this.accounts.size(); a++) {
			summary += String.format("%d) %s\n", a+1, accounts.get(a).getSummaryLine());
		}
		
		return summary;
	}

	public double getTransferLimit() {
		return transferLimit;
	}

	public void setTransferLimit(double transferLimit) {
		this.transferLimit = transferLimit;
	}

	public double getExternalTransferLimit() {
		return externalTransferLimit;
	}

	public void setExternalTransferLimit(double externalTransferLimit) {
		this.externalTransferLimit = externalTransferLimit;
	}

	public double getWithdrawalLimit() {
		return withdrawalLimit;
	}

	public void setWithdrawalLimit(double withdrawalLimit) {
		this.withdrawalLimit = withdrawalLimit;
	}

	@Override
	public String toString() {
  	return getClass().getSimpleName() + "[name=" + firstName + "]";
	}

	public String getName() {
		return String.format("%s %s", firstName, lastName);
	}

	public void changePin(String newPin){
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			this.pinHash = md.digest(newPin.getBytes());
		} catch (Exception e) {
			System.err.println("error, caught exeption : " + e.getMessage());
			System.exit(1);
		}
	}
}
