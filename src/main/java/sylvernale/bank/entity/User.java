package sylvernale.bank.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dao.AccountAppDao;
import dao.AccountDao;
import sylvernale.bank.Permissions;

public class User implements Comparable<User> {
	protected Integer userID;
	protected String username;
	public String password;
	protected Permissions permission;
	protected UserInfo info;

	public User() {
		permission = Permissions.User;
	}

	public User(Integer userID, String username, String password, Permissions permission, String firstName,
			String lastName, String socialSecurityNumber, String address) {
		this.userID = userID;
		this.username = username;
		this.password = User.hashPassword(password);
		this.permission = permission;
		this.info = new UserInfo(firstName, lastName, socialSecurityNumber, address);
}

	public User(ResultSet rs) throws SQLException {
		this.userID = rs.getInt("id");
		this.username = rs.getString("username");
		this.password = rs.getString("password");
		this.permission = Permissions.parsePermission(rs.getString("permission"));

		String fname = rs.getString("firstname");
		String lname = rs.getString("lastname");
		String social = rs.getString("social");
		String address = rs.getString("address");
		this.info = new UserInfo(fname, lname, social, address);
	}

	@Override
	public String toString() {
		return String.format("ID: %d - Username: %s - Password: %s - Permission: %s", userID, username, password,
				permission.toString());
	}

	public String toPrettyString() {
		return String.format("ID: %d - Username: %s - Permission: %s", userID, username, permission.toString());
	}

	public void setUserID(int newID) {
		userID = newID;
	}

	public int getUserID() {
		return userID;
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

	public List<AccountApp> getAccountApps() {
		return AccountAppDao.getUserAccountApps(this.userID);
	}

	public Permissions getPermission() {
		return permission;
	}

	public static String hashPassword(String password) {
		return String.valueOf(password.hashCode());
	}

	@Override
	public boolean equals(Object other) {
		if (other == this)
			return true;
		if (!(other instanceof User))
			return false;
		return userID == ((User) other).getUserID();
	}

	@Override
	public int hashCode() {
		return userID;
	}

	@Override
	public int compareTo(User o) {
		return username.compareTo(o.getUsername());
	}
}
