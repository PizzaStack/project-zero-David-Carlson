package sylvernale.bank.entity;

import java.util.ArrayList;
import java.util.List;

import sylvernale.bank.Permissions;

public class User {
	protected UserInfo info;
	protected String username;
	protected String password;
	protected int userID;
	protected Permissions permission;
	protected List<Account> accounts;

	public User() {
		permission = Permissions.None;
		accounts = new ArrayList<Account>();
	}

	public User(UserInfo info, String username, String password, int userID) {
		this.info = info;
		this.username = username;
		this.password = password;
		this.userID = userID;
		permission = Permissions.User;
		accounts = new ArrayList<Account>();
	}

	public String getUsername() {
		return username;
	}

	public String getFirstName() {
		return info.getFirstName();
	}

	public String getFullName() {
		return info.getFirstName() + " " + info.getLastName();
	}

	public UserInfo getUserInfo() {
		return info;
	}

	public String getPassword() {
		return password;
	}

	public List<Account> getAccounts() {
		return accounts;
	}

	public void addAccount(Account account) {
		accounts.add(account);		
	}

	// Creation Date
}
