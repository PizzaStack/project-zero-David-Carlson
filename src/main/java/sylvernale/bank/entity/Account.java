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
		// TODO Auto-generated method stub
		return false;
	}

	public Object getAccountType() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getBalance() {
		// TODO Auto-generated method stub
		return null;
	}

	public void changeAccountType(AccountType newType) {
		// TODO Auto-generated method stub
		
	}

	public void addOwner(User user2) {
		// TODO Auto-generated method stub
		
	}

}
