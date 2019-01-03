package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import sylvernale.bank.Terminal;
import sylvernale.bank.entity.AccountApp;
import sylvernale.bank.entity.User;

public class AccountAppDao {
	
	
	
	public static List<AccountApp> getPendingUserAccountApps() {
		List<AccountApp> accountApps = new ArrayList<AccountApp>();
		String sql = "select * from accountapps join users on accountapps.user_id=users.id where state='Pending' and permission='User';";
		try (Statement statement = Terminal.connection.createStatement();
				ResultSet resultSet = statement.executeQuery(sql);) {
			while (resultSet.next())
				accountApps.add(new AccountApp(resultSet));

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return accountApps;
	}
	
	public static List<AccountApp> getPendingUserAndDealerAccountApps() {
		List<AccountApp> accountApps = new ArrayList<AccountApp>();
		String sql = "select * from accountapps join users on accountapps.user_id=users.id where state='Pending' and permission!='Pitboss';";
		try (Statement statement = Terminal.connection.createStatement();
				ResultSet resultSet = statement.executeQuery(sql);) {
			while (resultSet.next())
				accountApps.add(new AccountApp(resultSet));

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return accountApps;
	}
	
	public static List<AccountApp> getAllPendingAccountApps() {
		List<AccountApp> accountApps = new ArrayList<AccountApp>();
		String sql = "select * from accountapps where state='Pending';";
		try (Statement statement = Terminal.connection.createStatement();
				ResultSet resultSet = statement.executeQuery(sql);) {
			while (resultSet.next())
				accountApps.add(new AccountApp(resultSet));

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return accountApps;
	}

	public static List<AccountApp> getUserAccountApps(int user_id) {
		List<AccountApp> accountApps = new ArrayList<AccountApp>();
		try (PreparedStatement statement = Terminal.connection
				.prepareStatement("select * from accountapps where user_id=?")) {
			statement.setInt(1, user_id);
			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next())
					accountApps.add(new AccountApp(resultSet));
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return accountApps;
	}

	public static void addAccountApp(int user_id, double balance) {
		try (PreparedStatement statement = Terminal.connection
				.prepareStatement("insert into accountapps (user_id, state, balance) values (?,?,?)")) {
			statement.setInt(1, user_id);
			statement.setString(2, "Pending");
			statement.setDouble(3, balance);
			int rows_affected = statement.executeUpdate();
			if (rows_affected != 1)
				throw new SQLException("AddAccountApp didn't affect 1 row");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void changeAccountAppState(AccountApp app, String newState) {
		app.setState(newState);
		String updateApp = "update accountapps set state=? where id=?";
		try (PreparedStatement statement = Terminal.connection.prepareStatement(updateApp)) {
			statement.setString(1, newState);
			statement.setInt(2, app.getUserID());
			if (statement.executeUpdate() != 1)
				throw new SQLException("Approved app didn't get updated correctly");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println(e.getMessage());
		}
		if (newState.equals("Approved")) {
			AccountDao.addAccount(app.getUserID(), "Checking", app.getBalance());
		}

	}

	public static List<AccountApp> filterForAppState(List<AccountApp> allApplications, String state) {
		return allApplications.stream().filter(A -> A.getState().equals(state)).collect(Collectors.toList());
	}

}
