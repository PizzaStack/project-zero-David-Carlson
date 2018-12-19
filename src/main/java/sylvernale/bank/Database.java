package sylvernale.bank;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import sylvernale.bank.entity.Account;
import sylvernale.bank.entity.AccountApplication;
import sylvernale.bank.entity.AccountType;
import sylvernale.bank.entity.User;

public class Database {
	protected Map<String, User> usersMap;
	protected List<Account> accountsList;
	protected List<AccountApplication> accountApps;
	protected String dataFilePath = "./data";
	protected String loggingPath = "./logs";
	protected String loadPath;
	protected String savePath;
	protected int nextUserID;
	protected int nextAccountID;

	public Database() {
		// TODO: Create Separate constructor which reads from file
		usersMap = new TreeMap<String, User>();
		accountsList = new ArrayList<Account>();
	}

	public Database(String loadPath, String savePath) {
		Path root = Paths.get(System.getProperty("user.dir"));
		if (loadPath != null)
			this.loadPath = Paths.get(root.toString(), loadPath).toString();
		if (savePath != null)
			this.savePath = Paths.get(root.toString(), savePath).toString();

		if (loadPath == null) {
			usersMap = new TreeMap<String, User>();
			accountsList = new ArrayList<Account>();
		} else {
			// TODO: Load files, add logging
		}

	}

	public void addUser(User user) {
		String username = user.getUsername();
		if (usersMap.containsKey(username))
			throw new InvalidParameterException("Added a user that already exists");
		else
			usersMap.put(username, user);
	}

	public Boolean containsUser(String username, String password) {
		return usersMap.containsKey(username) && usersMap.get(username).getPassword().equals(password);
	}

	public Boolean containsUser(String username) {
		return usersMap.containsKey(username);
	}

	public User getUser(String username) throws Exception {
		if (usersMap.containsKey(username))
			return usersMap.get(username);
		else
			throw new Exception("Attempted to get non-existant user: " + username);
	}

	public int getNextUserID() {
		return nextUserID++;
	}

	private int getNextAccountID() {
		return nextAccountID++;
	}

	public List<Account> getAllUserAccounts(List<Integer> accountIDs) {
		List<Account> accounts = new ArrayList<Account>();
		for (Integer id : accountIDs) {
			accounts.add(accountsList.get(id));
		}
		return accounts;
	}

	public void addAccountApp(AccountType type, User owner) {
		accountApps.add(new AccountApplication(type, owner));
	}

	public void addAccount(AccountType type, User owner) {
		Account account = new Account(owner, getNextAccountID(), 0, type);
		owner.addAccount(account);
		accountsList.add(account);
	}

}
