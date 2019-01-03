package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import sylvernale.bank.Terminal;
import sylvernale.bank.entity.Account;
import sylvernale.bank.entity.User;

public final class JointAccountDao {
	
	public static List<User> getJointOwners(int accountID) {
		List<User> users = new ArrayList<User>();
		String sql = "select users.* from users join jointowners on users.id=jointowners.user_id " 
				+ "where jointowners.acc_id=?";
		try (PreparedStatement statement = Terminal.connection.prepareStatement(sql)) {
			statement.setInt(1, accountID);
			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) 
					users.add(new User(resultSet));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return users;
	}
	
	public static List<Account> getUsersJointAccounts(int userID) {
		List<Account> accounts = new ArrayList<Account>();
		String sql = "select accounts.* from accounts join jointowners on accounts.id=jointowners.acc_id "
				+ "where jointowners.user_id=? AND accounts.user_id != ?;";
		try (PreparedStatement statement = Terminal.connection.prepareStatement(sql)) {
			statement.setInt(1, userID);
			statement.setInt(2, userID);
			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) 
					accounts.add(new Account(resultSet));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return accounts;
	}
	
	public static void addJointOwnerToAccount(int account_id, int jointOwnerID) {
		String sql = "insert into jointowners (acc_id, user_id) values(?,?);";
		try (PreparedStatement statement = Terminal.connection.prepareStatement(sql)) {
			statement.setInt(1, account_id);
			statement.setInt(2, jointOwnerID);
			int rows_affected = statement.executeUpdate();
			if (rows_affected != 1)
				throw new SQLException("Error adding joint account (Do you already own this account?");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}