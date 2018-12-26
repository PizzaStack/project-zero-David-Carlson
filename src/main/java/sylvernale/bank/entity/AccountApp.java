package sylvernale.bank.entity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountApp {
	protected int id;
	protected int user_id;
	protected String state;
	protected double balance;
	// Date?

	public AccountApp(ResultSet resultSet) throws SQLException {
		this.id = resultSet.getInt("id");
		this.user_id = resultSet.getInt("user_id");
		this.state = resultSet.getString("state");
		this.balance = resultSet.getDouble("balance");

	}
	@Override
	public String toString() {
		return String.format("Application #%s - Initial Balance : %s", id, balance);
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

	public double getBalance() {
		return balance;
	}

}
