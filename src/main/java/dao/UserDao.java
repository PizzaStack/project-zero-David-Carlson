package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import sylvernale.bank.Permissions;
import sylvernale.bank.Terminal;
import sylvernale.bank.entity.Account;
import sylvernale.bank.entity.User;
import sylvernale.bank.entity.UserInfo;

public final class UserDao {
	// Maps userIDs to users for caching purposes
	private static Map<Integer, User> userMap = new TreeMap<Integer, User>();

	
	public static User getUser(String username) {	
		String sql = String.format("select * from users where username='%s'", username);
		try (Statement statement = Terminal.connection.createStatement();
				ResultSet resultSet = statement.executeQuery(sql)) {

			while (resultSet.next()) {
				User user = new User(resultSet);
				userMap.put(user.getUserID(), user);
				return user;
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return null;		
	}

	public static User getUser(String username, String password) {
		password = User.hashPassword(password);
		String sql = String.format("select * from users where username='%s' AND password='%s'", username, password);
		try (Statement statement = Terminal.connection.createStatement();
				ResultSet resultSet = statement.executeQuery(sql)) {

			while (resultSet.next()) {
				User user = new User(resultSet);
				userMap.put(user.getUserID(), user);
				return user;
			}
		} catch (SQLException e) {
			System.out.println("getUser with name/password error: " + e.getMessage());
		}
		return null;
	}
	public static User getUser(int userID) {
		if (userMap.containsKey(userID)) {
			return userMap.get(userID);
		}
		String sql = String.format("select * from users where id='%s';", userID);
		try (Statement statement = Terminal.connection.createStatement();
				ResultSet resultSet = statement.executeQuery(sql)) {

			while (resultSet.next()) {
				User user = new User(resultSet);
				userMap.put(user.getUserID(), user);
				return user;
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	public static List<User> getAllUsers() {
		List<User> users = new ArrayList<User>();
		String sql = "select * from users";
		try (Statement statement = Terminal.connection.createStatement();
				ResultSet resultSet = statement.executeQuery(sql)) {

			while (resultSet.next()) {
				users.add(new User(resultSet));
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return users;
	}
	
	public static Boolean containsUsername(String username) {
		String sql = String.format("select * from users where username='%s';", username);
		try (Statement statement = Terminal.connection.createStatement();) {
			return statement.executeUpdate(sql) == 1;
		
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return false;
	}

	public static void addUser(User user) {
		UserInfo i = user.getUserInfo();
		String sql = "insert into users (username, password, permission, firstname, lastname, social, address) "
				+ String.format("values ('%s', '%s', '%s', '%s', '%s', '%s', '%s')", user.getUsername(),
						user.getPassword(), user.getPermission().toString(), i.getFirstName(), i.getLastName(),
						i.getSocialSecurityNumber(), i.getAddress());
		try (Statement statement = Terminal.connection.createStatement();) {
			statement.executeUpdate(sql);
			String id_sql = String.format("select id from users where username='%s';", user.getUsername());
			try (ResultSet resultSet = statement.executeQuery(id_sql)) {
				if (resultSet.next()) {
					int user_id = resultSet.getInt("id");
					user.setUserID(user_id);
					userMap.put(user_id, user);
				}
				else
					throw new SQLException("Couldn't retrieve user_id after adding new user");
			}
		} catch (SQLException e) {
			System.out.println("Add user error: " + e.getMessage());
		}
	}

	public static void updateUser(User user) {
		// TODO: Update row
	}

	// Creation Date
}
