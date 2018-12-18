package sylvernale.bank.entity;

import java.util.ArrayList;
import java.util.List;

public class Account {
	protected List<User> owners;
	protected double balance;
	protected AccountType accountType;
	public enum AccountType {
		Credit, Debit
	};
	
	public Account() {
		owners = new ArrayList<User>();
		accountType = AccountType.Debit;
	}
	
	public Account(User owner, double startingBalance, AccountType accountType) {
		this();
		owners.add(owner);
		balance = startingBalance;
		this.accountType = accountType;
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
