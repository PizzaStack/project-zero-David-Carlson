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
	protected Integer joint_owner;
	protected double balance;

	public Account() {
	}

	public Account(User owner, int accountID, double startingBalance) {
		this.accountID = accountID;
		user_id = owner.getUserID();
		balance = startingBalance;
		joint_owner = null;
	}

	public Account(ResultSet resultSet) throws SQLException {
		this.accountID = resultSet.getInt("id");
		this.balance = resultSet.getDouble("balance");
		this.user_id = resultSet.getInt("user_id");
		this.joint_owner = null;
	}

	@Override
	public String toString() {
		String margin = "\n         ";
		String ownersString = margin + "   " + UserDao.getUser(user_id).toPrettyString();
		if (joint_owner != null)
			ownersString += margin  + "   " + UserDao.getUser(joint_owner).toPrettyString();

		return String.format("Account #%s - Balance: %s %sOwner/s: %s", accountID, balance, margin, ownersString);
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
		// TODO: Format significant figures
		System.out.println("You succesfully deposited $" + givenAmount.toString());
	}

	public boolean containsOwner(User user) {
		if (user.getUserID() == user_id)
			return true;
		else if (joint_owner != null && user.getUserID() == joint_owner)
			return true;
		else
			return false;
	}

	public int getAccountID() {
		return accountID;
	}

	public Double getBalance() {
		return balance;
	}

}
