import java.util.ArrayList;

public class Controller {
	private User currentUser;

	protected boolean loginUser(Bank bank, String userID, String pin) {
		currentUser = bank.userLogin(userID, pin);
		if(currentUser != null){
			return true;

		}
		return false;
	}

	protected String getSummary(){
		return currentUser.getAccountsSummary();
	}

	/**
	 * Process a fund withdraw from an account.
	 * @param currentUser	the logged-in User object
	 * @param scanner		the Scanner object used for user input
	 */
	protected void withdrawFunds(int accountIndex, double amount, String memo) {
		// do the withdrwal
		currentUser.addAcctTransaction(accountIndex, -1*amount, memo);
		
	}
	
	/**
	 * Process a fund deposit to an account.
	 * @param currentUser	the logged-in User object
	 * @param scanner		the Scanner object used for user input
	 */
	protected void depositFunds(int accountIndex, double amount, String memo) {
		currentUser.addAcctTransaction(accountIndex, amount, memo);
		
	}

	/**
	 * Process transferring funds from one account to another.
	 * @param currentUser	the logged-in User object
	 * @param scanner		the Scanner object used for user input
	 */
	protected void transferFunds(int fromAcct, int toAcct, double amount) {

		currentUser.addAcctTransaction(fromAcct, -1*amount, String.format(
				"Transfer to account %s", currentUser.getAcctUUID(toAcct)));
		currentUser.addAcctTransaction(toAcct, amount, String.format(
				"Transfer from account %s", currentUser.getAcctUUID(fromAcct)));
	}

	protected void transferExtFunds(int fromAcct, int toAcct, double amount, Bank theBank) {
		currentUser.addAcctTransaction(fromAcct, -1*amount, String.format(
				"Transfer to account %s", theBank.getAcctUUID(toAcct)));
		theBank.addAcctTransaction(toAcct, amount, String.format(
				"Transfer from account %s", currentUser.getAcctUUID(fromAcct)));
		//System.out.println("External Transferred Account Balance: " + theBank.getBalance(toAcct));
	}


	protected ArrayList<String> getTransactionHistory(int accountIndex) {
		Account selectedAccount = currentUser.getAccount(accountIndex);
		ArrayList<Transaction> transactions = selectedAccount.getTransHistory();

		ArrayList<String> transHistories = new ArrayList<String>();

		for (Transaction transaction : transactions) {
			transHistories.add(transaction.getSummaryLine());
		}

		return transHistories;
	}

	protected int getNumOfAccFromBank(Bank theBank)
	{
		return theBank.getAccounts().size();
	}

	protected int getSelectedBankIndex(Bank theBank, String uuid)
	{
		return theBank.selectBankAcc(uuid);
	}

	protected int getNumberOfAccounts(){
		return currentUser.numAccounts();
	}

	protected double getAccountBalance(int accountIndex){
		return currentUser.getAcctBalance(accountIndex);
	}

	protected void changeTransferLimit(double amount) {
		currentUser.setTransferLimit(amount);
	}

	protected void changeExternalTransferLimit(double amount) {
		currentUser.setExternalTransferLimit(amount);
	}

	protected void changeWithdrawalLimit(double amount) {
		currentUser.setWithdrawalLimit(amount);
	}

	protected double getTransferLimit() {
		return currentUser.getTransferLimit();
	}

	protected double getExternalTransferLimit() {
		return currentUser.getExternalTransferLimit();
	}

	protected double getWithdrawalLimit() {
		return currentUser.getWithdrawalLimit();
	}

	protected String getUserName() {
		return currentUser.getName();
	}

	protected boolean validatePin(String pin){
		return currentUser.validatePin(pin);
	}

	protected void changePin(String newPin){
		currentUser.changePin(newPin);
	}
}
