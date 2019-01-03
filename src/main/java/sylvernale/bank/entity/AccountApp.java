package sylvernale.bank.entity;

import java.sql.ResultSet;
import java.sql.SQLException;

import sylvernale.bank.AccountType;

public class AccountApp {
	protected int id;
	protected int user_id;
	protected AccountType accountType;
	protected String state;

	public AccountApp(ResultSet resultSet) throws SQLException {
		this.id = resultSet.getInt("id");
		this.user_id = resultSet.getInt("user_id");
		this.accountType = AccountType.parseAccountType(resultSet.getString("account_type"));
		this.state = resultSet.getString("state");

	}

	@Override
	public String toString() {
		return String.format("Application #%s - Type : %s", id, accountType.toString());
	}

	public String toFullString() {
		return String.format("%s: Application #%s - Type : %s", state, id, accountType.toString());
	}

	// Getters and setters :
	// ********************************************************************************

	public int getAppID() {
		return id;
	}

	public int getUserID() {
		return user_id;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		if (state.equals("Approved") || state.equals("Denied") || state.equals("Pending"))
			this.state = state;
	}
	public AccountType getAccountType() {
		return accountType;
	}

}
