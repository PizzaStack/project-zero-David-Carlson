package dao;

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

	public static List<Account> getUserAccounts(User user) {
		List<Account> accounts = new ArrayList<Account>();
		String sql = String.format("select * from accounts where user_id='%s';", user.getUserID());
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
		if (accountMap.containsKey(accountMap))
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

	public static void addAccount(User user) {
		double initial_balance = 100.0;
		String sql = String.format("insert into accounts (user_id, balance) values (%s, %s)", user.getUserID(),
				initial_balance);
		try (Statement statement = Terminal.connection.createStatement()) {
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			System.out.println("Add account error: " + e.getMessage());
		}
	}

	public static void addAccountWithJointOwner(User user, User joint) {
		double initial_balance = 100.0;
		String sql = String.format("insert into accounts (user_id, balance) values (%s, %d)",
				user.getUserID(), joint.getUserID(), initial_balance);
		// TODO Change this
		try (Statement statement = Terminal.connection.createStatement()) {
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			System.out.println("Add account error: " + e.getMessage());
		}
	}

}
