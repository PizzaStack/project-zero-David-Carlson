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
	protected Map<String, User> usersMap = new TreeMap<String, User>();
	protected Map<Integer, Account> accountsMap = new TreeMap<Integer, Account>();
	protected List<AccountApplication> accountApps = new ArrayList<AccountApplication>();
	protected String dataFilePath = "./data";
	protected String loggingPath = "./logs";
	protected String loadPath;
	protected String savePath;
	protected int nextUserID = 0;
	protected int nextAccountID = 0;

	public Database() {
		
	}
	public Database(String loadPath, String savePath) {
		Path root = Paths.get(System.getProperty("user.dir"));
		if (loadPath != null)
			this.loadPath = Paths.get(root.toString(), loadPath).toString();
		if (savePath != null)
			this.savePath = Paths.get(root.toString(), savePath).toString();

		if (loadPath != null) {
			// Add code to load
		}
	}

	public void addUser(User user) {
		String username = user.getUsername();
		if (usersMap.containsKey(username))
			throw new InvalidParameterException("Added a user that already exists");
		else
			usersMap.put(username, user);
	}
	public User getUser(String username) throws Exception {
		if (usersMap.containsKey(username))
			return usersMap.get(username);
		else
			throw new Exception("Attempted to get non-existant user: " + username);
	}

	public Boolean containsUser(String username, String password) {
		password = User.hashPassword(password);
		return usersMap.containsKey(username) && usersMap.get(username).getPassword().equals(password);
	}
	public Boolean containsUser(String username) {
		return usersMap.containsKey(username);
	}

	public Boolean isAccountOwnedByUser(int accountID, String username, String password) {
		if (accountsMap.containsKey(accountID)) {
			Account account = accountsMap.get(accountID);
			for (User owner : account.getOwners()) {
				if (owner.getUsername().equals(username) && owner.getPassword().equals(password)) {
					return true;
				}
			}
		}
		return false;
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
			accounts.add(accountsMap.get(id));
		}
		return accounts;
	}

	public void addAccountApp(User owner) {
		accountApps.add(new AccountApplication(owner));
	}

	public void addAccount(User owner) {
		Account account = new Account(owner, getNextAccountID(), 100);
		owner.addAccount(account);
		accountsMap.put(account.getAccountID(), account);
	}

}
