package sylvernale.bank.entity;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class Account {
	protected List<User> owners = new ArrayList<User>();
	protected int accountID;
	protected double balance;
	
	public Account() {
	}
	
	public Account(User owner, int accountID, double startingBalance) {
		this.accountID = accountID;
		owners.add(owner);
		balance = startingBalance;
	}
	
	@Override
	public String toString() {
		String ownersString = "";
		for (User owner : owners)
			ownersString += owner.getFullName() + ", ";
		
		return String.format("Account #%s  - Balance: %s - Owner/s: %s", 
				accountID, balance, ownersString);
	}	
	
	public void withdrawAmount(Double requestedAmount) {
		if (requestedAmount < 0)
			throw new InvalidParameterException("You cannot withdraw a negative amount!!");
		if (requestedAmount > balance)
			throw new InvalidParameterException("You cannot withdraw more than you have!!");
		balance -= requestedAmount;
		System.out.println("You successfully withdrew $" + requestedAmount.toString());
	}
	public void depositAmount(Double givenAmount) {
		if (givenAmount < 0)
			throw new InvalidParameterException("You cannot deposit a negative amount!!");
		balance += givenAmount;
		// TODO: Format significant figures
		System.out.println("You succesfully deposited $" + givenAmount.toString());		
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
