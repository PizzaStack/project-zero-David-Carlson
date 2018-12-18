package sylvernale.bank;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.util.Map;
import java.util.TreeMap;

import sylvernale.bank.entity.Account;
import sylvernale.bank.entity.User;

public class Database {
	protected Map<String, User> users;
	protected Map<String, Account> accounts;
	protected String dataFilePath = "./data";
	protected String loggingPath = "./logs";
	protected String loadPath;
	protected String savePath;
	protected int nextUserID;
	protected int nextAccountID;

	public Database() {
		// TODO: Create Separate constructor which reads from file
		users = new TreeMap<String, User>();
		accounts = new TreeMap<String, Account>();
	}

	public Database(String loadPath, String savePath) {
		Path root = Paths.get(System.getProperty("user.dir"));
		if (loadPath != null)
			this.loadPath = Paths.get(root.toString(), loadPath).toString();
		if (savePath != null)
			this.savePath = Paths.get(root.toString(), savePath).toString();

		if (loadPath == null) {
			users = new TreeMap<String, User>();
			accounts = new TreeMap<String, Account>();
		} else {
			// TODO: Load files, add logging
		}

	}

	public void addUser(User user) {
		String username = user.getUsername();
		if (users.containsKey(username))
			throw new InvalidParameterException("Added a user that already exists");
		else
			users.put(username, user);
	}

	public Boolean containsUser(String username, String password) {
		return users.containsKey(username) && users.get(username).getPassword().equals(password);
	}

	public Boolean containsUser(String username) {
		return users.containsKey(username);
	}

	public User getUser(String username) throws Exception {
		if (users.containsKey(username))
			return users.get(username);
		else
			throw new Exception("Attempted to get non-existant user: " + username);
	}

	public int getNextUserID() {
		return nextUserID++;
	}

}
