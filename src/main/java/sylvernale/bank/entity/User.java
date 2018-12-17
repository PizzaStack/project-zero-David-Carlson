package sylvernale.bank.entity;

import sylvernale.bank.Permissions;

public class User {
	protected UserInfo info;
	protected int accountID;	
	protected Permissions permission;
	
	public User() {
		permission = Permissions.None;
	}
	public User(UserInfo info, int accountID) {
		this.info = info;
		this.accountID = accountID;
		permission = Permissions.User;
	}
	
	// Creation Date
}
