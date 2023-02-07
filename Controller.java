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

	/**
	 * Process a fund withdraw from an account.
	 * @param currentUser	the logged-in User object
	 * @param scanner		the Scanner object used for user input
	 */
	public void withdrawFunds(User currentUser, int accountIndex, double amount, String memo) {
		// do the withdrwal
		currentUser.addAcctTransaction(accountIndex, -1*amount, memo);
		
	}
	
	/**
	 * Process a fund deposit to an account.
	 * @param currentUser	the logged-in User object
	 * @param scanner		the Scanner object used for user input
	 */
	public void depositFunds(User currentUser, int accountIndex, double amount, String memo) {
		currentUser.addAcctTransaction(accountIndex, amount, memo);
		
	}

	/**
	 * Process transferring funds from one account to another.
	 * @param currentUser	the logged-in User object
	 * @param scanner		the Scanner object used for user input
	 */
	public void transferFunds(User currentUser, int fromAcct, int toAcct, double amount) {
		
		// finally, do the transfer 
		currentUser.addAcctTransaction(fromAcct, -1*amount, String.format(
				"Transfer to account %s", currentUser.getAcctUUID(toAcct)));
		currentUser.addAcctTransaction(toAcct, amount, String.format(
				"Transfer from account %s", currentUser.getAcctUUID(fromAcct)));
	}

	public ArrayList<Transaction> getTransactionHistory(User user, int accountIndex) {
		Account selectedAccount = user.getAccount(accountIndex);
		ArrayList<Transaction> transactions = selectedAccount.getTransHistory();

		return transactions;
	}
}
