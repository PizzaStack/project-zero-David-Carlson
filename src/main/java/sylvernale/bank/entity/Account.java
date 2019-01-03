package sylvernale.bank.entity;

import java.security.InvalidParameterException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dao.AccountDao;
import dao.JointAccountDao;
import dao.UserDao;

public class Account {
	protected int accountID;
	protected int user_id;
	protected String accountType;
	protected Boolean active;
	protected double balance;
	protected double moneyGambled = 0;
	protected double moneyWon = 0;
	public final static String Savings = "Savings";
	public final static String Checking = "Checking";

	public Account() {
	}

	public Account(User owner, int accountID, double startingBalance) {
		this.accountID = accountID;
		user_id = owner.getUserID();
		this.accountType = "Checking";
		this.active = true;
		balance = startingBalance;
	}

	public Account(ResultSet resultSet) throws SQLException {
		this.accountID = resultSet.getInt("id");
		this.balance = resultSet.getDouble("balance");
		this.user_id = resultSet.getInt("user_id");
		
		this.accountType = resultSet.getString("account_type");
		this.active = resultSet.getBoolean("active");
		this.moneyGambled = resultSet.getDouble("money_gambled");
		this.moneyWon = resultSet.getDouble("money_won");
	}

	public String toFullString(String margin, String tab) {
		String description = String.format(margin + "Account #%s - Type - %s Balance: %s", accountID, balance);
		description += "\n" + margin + tab + "Owner: ";
		description += "\n" + margin + tab + tab + UserDao.getUser(user_id).toPrettyString();
		
		String ownersString = margin + tab + UserDao.getUser(user_id).toPrettyString();
		List<User> jointOwners = JointAccountDao.getJointOwners(accountID);
		if (jointOwners.size() > 0) {
			description += "\n" + margin + tab + "Joint-Owners";
			for (User jointOwner : jointOwners)
				description += "\n" + margin + tab + tab + jointOwner.toPrettyString();
		}
		return description;
	}
	
	public String toPartialString(String margin, String tab) {
		String description = String.format("Account #%s - Balance: %s", accountID, balance);
		List<User> jointOwners = JointAccountDao.getJointOwners(accountID);
		if (jointOwners.size() > 0) {
			description += "\n" + margin + tab + "Joint-Owners";
			for (User jointOwner : jointOwners)
				description += "\n" + margin + tab + tab + jointOwner.toPrettyString();
		}
		return description;
	}

	public void withdrawAmount(Double withdrawAmount) {
		if (withdrawAmount < 0)
			throw new InvalidParameterException("You cannot withdraw a negative amount!!");
		if (withdrawAmount > balance)
			throw new InvalidParameterException("You cannot withdraw more than you have!!");
		balance -= withdrawAmount;
		AccountDao.changeAccountBalance(accountID, -withdrawAmount);
		System.out.println("You successfully withdrew $" + withdrawAmount.toString());
		// TODO DAO operation
	}

	public void depositAmount(Double depositAmount) {
		if (depositAmount < 0)
			throw new InvalidParameterException("You cannot deposit a negative amount!!");
		balance += depositAmount;
		AccountDao.changeAccountBalance(accountID, depositAmount);
		System.out.println("You succesfully deposited $" + depositAmount.toString());
	}

	public boolean containsOwner(User user) {
		if (user.getUserID() == user_id)
			return true;
		else if (JointAccountDao.getJointOwners(accountID).contains(user.getUserID()))
			return true;
		else
			return false;
	}
	
	// Getters and setters :  ************************************************************************************

	public int getAccountID() {
		return accountID;
	}
	
	public int getUser_ID() {
		return user_id;
	}

	public Double getBalance() {
		return balance;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other == this)
			return true;
		else if (other instanceof Account)
			return accountID == ((Account)other).getAccountID();
		else if (other instanceof Integer)
			return this.user_id == (int)other;
		else
			return false;						
	}
	
	@Override
	public int hashCode() {
		return accountID;
	}

}
