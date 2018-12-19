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

	public User(UserInfo info, int userID, String username, String password) {
		this.info = info;
		this.username = username;
		this.password = password;
		this.userID = userID;
		permission = Permissions.User;
		accounts = new ArrayList<Account>();
	}
	public User(int userID, String username, String password, Permissions permission,
			String firstName, String lastName, String socialSecurityNumber, String address) {
		this.userID = userID;
		this.username = username;
		this.password = password;
		this.info = new UserInfo(firstName, lastName, socialSecurityNumber, address);
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

	public Permissions getPermission() {
		return permission;
	}

	public static String hashPassword(String password) {
		return String.valueOf(password.hashCode());
	}

	// Creation Date
}
