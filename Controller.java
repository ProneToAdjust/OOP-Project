import java.util.ArrayList;

public class Controller {
	private User currentUser;

	public boolean loginUser(Bank bank, String userID, String pin) {
		currentUser = bank.userLogin(userID, pin);
		if(currentUser != null){
			return true;

		}
		return false;
	}

	public void printSummary(){
		currentUser.printAccountsSummary();
	}

	public void printAccountsNumberAndType()
	{
		currentUser.printAccountNumbersAndType();
	}

	public void printSingleAccountSummary(int account)
	{
		currentUser.printSingleAccountSummary(account);
	}

	 
	/**
	 * Process a fund withdraw from an account.
	 * @param currentUser	the logged-in User object
	 * @param scanner		the Scanner object used for user input
	 */
	public void withdrawFunds(int accountIndex, double amount, String memo) {
		// do the withdrwal
		currentUser.addAcctTransaction(accountIndex, -1*amount, memo);
		
	}
	
	/**
	 * Process a fund deposit to an account.
	 * @param currentUser	the logged-in User object
	 * @param scanner		the Scanner object used for user input
	 */
	public void depositFunds(int accountIndex, double amount, String memo) {
		currentUser.addAcctTransaction(accountIndex, amount, memo);
		
	}

	/**
	 * Process transferring funds from one account to another.
	 * @param currentUser	the logged-in User object
	 * @param scanner		the Scanner object used for user input
	 */
	public void transferFunds(int fromAcct, int toAcct, double amount) {

		currentUser.addAcctTransaction(fromAcct, -1*amount, String.format(
				"Transfer to account %s", currentUser.getAcctUUID(toAcct)));
		currentUser.addAcctTransaction(toAcct, amount, String.format(
				"Transfer from account %s", currentUser.getAcctUUID(fromAcct)));
	}

	public ArrayList<String> getTransactionHistory(int accountIndex) {
		Account selectedAccount = currentUser.getAccount(accountIndex);
		ArrayList<Transaction> transactions = selectedAccount.getTransHistory();

		ArrayList<String> transHistories = new ArrayList<String>();

		for (Transaction transaction : transactions) {
			transHistories.add(transaction.getSummaryLine());
		}

		return transHistories;
	}

	public int getNumberOfAccounts(){
		return currentUser.numAccounts();
	}

	public double getAccountBalance(int accountIndex){
		return currentUser.getAcctBalance(accountIndex);
	}

	public void changeTransferLimit(double amount) {
		currentUser.setTransferLimit(amount);
	}

	public void changeWithdrawalLimit(double amount) {
		currentUser.setWithdrawalLimit(amount);
	}

	public double getTransferLimit() {
		return currentUser.getTransferLimit();
	}

	public double getWithdrawalLimit() {
		return currentUser.getWithdrawalLimit();
	}
}
