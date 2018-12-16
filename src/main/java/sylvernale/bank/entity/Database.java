package sylvernale.bank.entity;

import java.util.ArrayList;
import java.util.List;

public class Database {
	private List<User> users;
	private List<Account> accounts;

	
	public Database() {
		// TODO: Create Separate constructor which reads from file
		users = new ArrayList<User>();
		accounts = new ArrayList<Account>();
	}
	
	public Boolean addUser(User user) {
		return false;
	}
	public Boolean containsUser(User user) {
		return false;
	}

}
