package sylvernale.bank.entity;

import java.util.ArrayList;
import java.util.List;

public class Account {
	protected List<User> owners;
	protected int accountID;
	protected double balance;
	protected AccountType accountType;
	
	public Account() {
		owners = new ArrayList<User>();
		accountType = AccountType.Debit;
	}
	
	public Account(User owner, int accountID, double startingBalance, AccountType accountType) {
		owners = new ArrayList<User>();
		owners.add(owner);
		balance = startingBalance;
		this.accountType = accountType;
	}
	
	@Override
	public String toString() {
		String type = accountType == AccountType.Debit ? "Debit" : "Credit";
		String ownersString = "";
		for (User owner : owners)
			ownersString += owner.getFullName() + ", ";
		
		return String.format("Account #%s - Type: %s - Balance: %s - Owner/s: %s", 
				accountID, type, balance, ownersString);
	}	
	public Double withdrawAmount(Double requestedAmount) {
		
		Double newBalance = Math.max(a, b)
	}
	
	public List<User> getOwners() {
		return owners;
	}

	public boolean containsOwner(User user) {
		return owners.contains(user);
	}

	public AccountType getAccountType() {
		return accountType;
	}
	public void changeBalance(double balanceDelta) {
		balance += balanceDelta;
		if (accountType == AccountType.Debit)
			balance = Math.max(balance, 0);
	}
	public Double getBalance() {
		return balance;
	}

	public void changeAccountType(AccountType newType) {
		accountType = newType;		
	}

	public void addOwner(User newOwner) {
		if (!owners.contains(newOwner))
			owners.add(newOwner);		
	}

}
