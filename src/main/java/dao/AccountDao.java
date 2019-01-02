package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import sylvernale.bank.Terminal;
import sylvernale.bank.entity.Account;
import sylvernale.bank.entity.User;

public final class AccountDao {
	private static Map<Integer, Account> accountMap = new TreeMap<Integer, Account>();

	public static List<Account> getUserAccounts(int userID) {
		List<Account> accounts = new ArrayList<Account>();
		String sql = String.format("select * from accounts where user_id='%s';", userID);
		try (Statement statement = Terminal.connection.createStatement();
				ResultSet resultSet = statement.executeQuery(sql)) {
			while (resultSet.next()) {
				accounts.add(new Account(resultSet));
			}

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return accounts;
	}

	public static Account getAccount(int account_id) {
		if (accountMap.containsKey(account_id))
			return accountMap.get(account_id);
		String sql = String.format("select * from accounts where id='%s';", account_id);
		try (Statement statement = Terminal.connection.createStatement();
				ResultSet resultSet = statement.executeQuery(sql)) {
			while (resultSet.next()) {
				Account account = new Account(resultSet);
				accountMap.put(account.getAccountID(), account);
				return account;
			}
		} catch (SQLException e) {
			System.out.println("Error gettingAccount " + account_id + ", " + e.getMessage());
			System.out.println(e.getStackTrace());
		}
		return null;
	}

	public static void addAccount(int userID, String type, double initialBalance) {
		String sql = String.format("insert into accounts (user_id, type, active, balance, money_gambled, money_won) values (%s, %s, %s, %s)",
				userID, type, "true", initialBalance, 0, 0);
		try (Statement statement = Terminal.connection.createStatement()) {
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			System.out.println("Add account error: " + e.getMessage());
		}
	}

	public static void changeAccountBalance(int account_id, double balanceDelta) {
		String sql = "update accounts set balance=balance + ? where id=?;";
		try (PreparedStatement statement = Terminal.connection.prepareStatement(sql)) {
			statement.setDouble(1, balanceDelta);
			statement.setInt(2, account_id);
			if (statement.executeUpdate() != 1)
				System.out.println("Error, couldn't update account");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static Account getAccountWithUserCredentials(int account_id, String username, String user_password) {
		user_password = User.hashPassword(user_password);
		String sql = "select count(*) as accounts_matching, accounts.* from accounts join users on accounts.user_id=users.id "
				+ "where accounts.id=? and users.username=? and users.password=?";

		try (PreparedStatement statement = Terminal.connection.prepareStatement(sql)) {
			statement.setInt(1, account_id);
			statement.setString(2, username);
			statement.setString(3, user_password);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					int accounts_matching = resultSet.getInt("accounts_matching");
					if (accounts_matching != 1)
						return null;
					else
						return new Account(resultSet);
				} else
					throw new SQLException("No rows returned from getAccountWithUserCredentials query");
			}

		} catch (SQLException e) {
			System.out.println("ContainsUsername error: " + e.getMessage());
		}
		return null;
	}

//	public static void addAccountWithJointOwner(User user, User joint) {
//		double initial_balance = 100.0;
//		String sql = String.format("insert into accounts (user_id, balance) values (%s, %d)", user.getUserID(),
//				joint.getUserID(), initial_balance);
//		// TODO Change this
//		try (Statement statement = Terminal.connection.createStatement()) {
//			statement.executeUpdate(sql);
//		} catch (SQLException e) {
//			System.out.println("Add account error: " + e.getMessage());
//		}
//	}

}
