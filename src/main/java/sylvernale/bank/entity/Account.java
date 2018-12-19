package sylvernale.bank.entity;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class Account {
	protected List<User> owners;
	protected int accountID;
	protected double balance;
	
	public Account() {
		owners = new ArrayList<User>();
	}
	
	public Account(User owner, int accountID, double startingBalance) {
		owners = new ArrayList<User>();
		owners.add(owner);
		balance = startingBalance;
	}
	
	@Override
	public String toString() {
		String ownersString = "";
		for (User owner : owners)
			ownersString += owner.getFullName() + ", ";
		
		return String.format("Account #%s - Type: %s - Balance: %s - Owner/s: %s", 
				accountID, balance, ownersString);
	}	
	
	public void withdrawAmount(Double requestedAmount) {
		if (requestedAmount < 0)
			throw new InvalidParameterException("Withdrawal amount is negative");
		if (requestedAmount > balance)
			throw new InvalidParameterException("Withdrawing more money than owned");
		System.out.println("You successfully withdrew $" + requestedAmount.toString());
	}
	
	public List<User> getOwners() {
		return owners;
	}

	public boolean containsOwner(User user) {
		return owners.contains(user);
	}

	
	public int getAccountID() {
		return accountID;
	}

	public Double getBalance() {
		return balance;
	}

	public void addOwner(User newOwner) {
		if (!owners.contains(newOwner))
			owners.add(newOwner);		
	}

}
