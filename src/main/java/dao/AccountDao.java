package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import sylvernale.bank.AccountType;
import sylvernale.bank.Terminal;
import sylvernale.bank.entity.Account;
import sylvernale.bank.entity.User;

public final class AccountDao {
//	private static Map<Integer, Account> accountMap = new TreeMap<Integer, Account>();

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
//		if (accountMap.containsKey(account_id))
//			return accountMap.get(account_id);
		String sql = String.format("select * from accounts where id='%s';", account_id);
		try (Statement statement = Terminal.connection.createStatement();
				ResultSet resultSet = statement.executeQuery(sql)) {
			while (resultSet.next()) {
				Account account = new Account(resultSet);
//				accountMap.put(account.getAccountID(), account);
				return account;
			}
		} catch (SQLException e) {
			System.out.println("Error gettingAccount " + account_id + ", " + e.getMessage());
			System.out.println(e.getStackTrace());
		}
		return null;
	}
	
	public static void changeAccountStatus(int account_id, Boolean activeStatus) {
		String sql = "update accounts set active=? where id=?;";
		try (PreparedStatement statement = Terminal.connection.prepareStatement(sql)) {
			statement.setBoolean(1, activeStatus);
			statement.setInt(2, account_id);
			if (statement.executeUpdate() != 1)
				System.out.println("Error, couldn't update account");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void addAccount(int userID, AccountType type) {
		addAccount(userID, type, 0, 0, 0);
	}
	public static void addAccount(int userID, AccountType type, double initialBalance) {
		addAccount(userID, type, initialBalance, 0, 0);
	}
	public static void addAccount(int userID, AccountType type, double initialBalance, double money_gambled, double money_won) {
		String sql = "insert into accounts (user_id, account_type, active, balance, money_gambled, money_won) values (?, ?, ?, ?, ?, ?);";
		try (PreparedStatement statement = Terminal.connection.prepareStatement(sql)) {
			statement.setInt(1, userID);
			statement.setString(2, type.toString());
			statement.setBoolean(3, true);
			statement.setDouble(4, initialBalance);
			statement.setDouble(5, money_gambled);
			statement.setDouble(6, money_won);
			if (statement.executeUpdate() != 1)
				throw new SQLException("Insert didn't affect 1 row");
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
	public static void gambleOnAccount(Account account, Double delta, Double wager) {
		account.gambleAmount(delta, wager);
		String sql = "update accounts set balance=balance + ?, "
					 + "money_gambled=money_gambled + ?, "
					 + "money_won=money_won + ? where id=?;";
		try (PreparedStatement statement = Terminal.connection.prepareStatement(sql)) {
			statement.setDouble(1, delta);
			statement.setDouble(2, wager);
			statement.setDouble(3, delta);
			statement.setInt(4, account.getAccountID());
			if (statement.executeUpdate() != 1)
				System.out.println("Error, couldn't update account");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static Account getAccountWithUserCredentials(int account_id, String username, String user_password) {
		user_password = User.hashPassword(user_password);
		String sql = "select * from accounts join users on accounts.user_id=users.id "
				+ "where accounts.id=? and users.username=? and users.password=?";

		try (PreparedStatement statement = Terminal.connection.prepareStatement(sql)) {
			statement.setInt(1, account_id);
			statement.setString(2, username);
			statement.setString(3, user_password);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					return new Account(resultSet);						
				} else
					throw new SQLException("No rows returned from getAccountWithUserCredentials query");
			}

		} catch (SQLException e) {
			System.out.println("getAccountWithUserCredentials error: " + e.getMessage());
		}
		return null;
	}

}
