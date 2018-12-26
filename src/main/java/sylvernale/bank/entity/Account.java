package sylvernale.bank.entity;

import java.security.InvalidParameterException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dao.UserDao;

public class Account {
	protected int accountID;
	protected int user_id;
	protected List<Integer> joint_owners;
	protected double balance;

	public Account() {
	}

	public Account(User owner, int accountID, double startingBalance) {
		this.accountID = accountID;
		user_id = owner.getUserID();
		balance = startingBalance;
		joint_owners = new ArrayList<Integer>();
	}

	public Account(ResultSet resultSet) throws SQLException {
		this.accountID = resultSet.getInt("id");
		this.balance = resultSet.getDouble("balance");
		this.user_id = resultSet.getInt("user_id");
		this.joint_owners = new ArrayList<Integer>();
		// TODO: new SQL query
	}

	@Override
	public String toString() {
		String margin = "\n         ";
		String ownersString = margin + "   " + UserDao.getUser(user_id).toPrettyString();
		if (joint_owners.size() > 0) {
			ownersString += margin + "Joint-Owners";
			for (Integer joint_owner : joint_owners)
				ownersString += margin  + "   " + UserDao.getUser(joint_owner).toPrettyString();
		}
		return String.format("Account #%s - Balance: %s %sOwner: %s", accountID, balance, margin, ownersString);
	}

	public void withdrawAmount(Double requestedAmount) {
		if (requestedAmount < 0)
			throw new InvalidParameterException("You cannot withdraw a negative amount!!");
		if (requestedAmount > balance)
			throw new InvalidParameterException("You cannot withdraw more than you have!!");
		balance -= requestedAmount;
		System.out.println("You successfully withdrew $" + requestedAmount.toString());
		// TODO DAO operation
	}

	public void depositAmount(Double givenAmount) {
		if (givenAmount < 0)
			throw new InvalidParameterException("You cannot deposit a negative amount!!");
		balance += givenAmount;
		// TODO: Format significant figures, DAO
		System.out.println("You succesfully deposited $" + givenAmount.toString());
	}

	public boolean containsOwner(User user) {
		if (user.getUserID() == user_id)
			return true;
		else if (joint_owners.contains(user.getUserID()))
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

}
