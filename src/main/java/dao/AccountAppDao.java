package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import sylvernale.bank.Terminal;
import sylvernale.bank.entity.AccountApp;
import sylvernale.bank.entity.User;

public class AccountAppDao {
	public static List<AccountApp> getPendingAccountApps() {
		List<AccountApp> accountApps = new ArrayList<AccountApp>();
		String sql = "select * from accountapps where state='Pending'";
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
	
	public static List<AccountApp> getPendingAccountAppsWithSimilarUsername(String username) {
		return null;
	}
	
	public static List<AccountApp> getUserAccountApps(int user_id) {
		List<AccountApp> accountApps = new ArrayList<AccountApp>();
		try (PreparedStatement statement = Terminal.connection.prepareStatement(
				"select * from accountapps where user_id=?")) {
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
		try (PreparedStatement statement = Terminal.connection.prepareStatement(
				"insert into accountapps (user_id, state, balance) values (?,?,?)")) {
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

//	try (PreparedStatement statement = Terminal.connection.prepareStatement(
//			"select * from accounts where user_id < ?;");) {
//	statement.setInt(1, 3);
//	try (ResultSet resultSet = statement.executeQuery();){
//		
//	}
//	} catch (SQLException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
}
