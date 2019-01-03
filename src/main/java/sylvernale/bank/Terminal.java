package sylvernale.bank;

import java.security.InvalidParameterException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

import dao.AccountAppDao;
import dao.AccountDao;
import dao.JointAccountDao;
import dao.UserDao;
import sylvernale.bank.entity.Account;
import sylvernale.bank.entity.AccountApp;
import sylvernale.bank.entity.User;
import sylvernale.bank.entity.UserInfo;

public class Terminal implements AutoCloseable {
	public static Connection connection;
	public static Boolean OnlyCache = false;
	protected State state;
	protected final String tab = "   ";

	protected enum State {
		SplashScreen, LoggingIn, AccountCreation, LoggedIn, Exiting
	}

	protected Scanner scanner;

	public Terminal(Scanner scanner) {
		this.scanner = scanner;
		state = State.SplashScreen;

		String url = "jdbc:postgresql://baasu.db.elephantsql.com:5432/kyhsxtgj";
		String user = "kyhsxtgj";
		String password = "a41I5riPacnHC9tuzW8xs0GyramwRr-G";
		try {
			Terminal.connection = DriverManager.getConnection(url, user, password);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void recreateDatabases() {
		String[] dropStatements = { "drop table IF EXISTS accountapps;", "drop table IF EXISTS jointowners;",
				"drop table IF EXISTS accounts;", "drop table IF EXISTS users;" };

		String[] createStatements = { "create table users ("
				+ "id serial primary key, username varchar(50) unique, password varchar(50), permission varchar(20), "
				+ "firstname varchar(50), lastname varchar(50), social varchar(9), address varchar(100));",

				"create table accounts ( " 
				+ "id serial primary key, " 
				+ "user_id serial references users(id) not NULL, "
				+ "account_type varchar(20),"
				+ "active bool,"
				+ "balance real not NULL,"
				+ "money_gambled real,"
				+ "money_won real);",

				"create table jointowners ( " 
				+ "acc_id serial references accounts(id) not NULL,"
				+ "user_id serial references users(id) not NULL," 
				+ "primary key(acc_id, user_id));",

				"create table accountapps (" + "id serial primary key, " + "user_id serial references users(id), "
						+ "state varchar(20), " + "balance real not NULL);" };

		try (Statement statement = connection.createStatement()) {
			for (String drop : dropStatements) {
				try {
					statement.executeUpdate(drop);
				} catch (SQLException e) {
					System.out.println("Error creating table: " + e.getMessage());
				}
			}
			for (String create : createStatements) {
				try {
					statement.executeUpdate(create);
				} catch (SQLException e) {
					System.out.println("Error creating table: " + e.getMessage());
				}
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void fillDatabase() {
		String[] names = { "u0", "u1", "d0", "d1", "p0", "p1" };
		Permissions[] permissions = { Permissions.User, Permissions.User, Permissions.Dealer, Permissions.Dealer,
				Permissions.Pitboss, Permissions.Pitboss };
		for (int i = 0; i < names.length; i++) {
			String n = names[i];
			User user = new User(i, n, n, permissions[i], n, n, n, n);
			UserDao.addUser(user);
			AccountDao.addAccount(user.getUserID(), Account.Checking, 100);
			AccountDao.addAccount(user.getUserID(), Account.Savings, 200);
			AccountAppDao.addAccountApp(user.getUserID(), 200.0);
		}
	}

	public void runTerminal() throws Exception {
		User currentUser = null;
		while (state != State.Exiting) {
			switch (state) {
			case SplashScreen:
				runSplashScreen();
				break;
			case LoggingIn:
				currentUser = runLogin();
				break;
			case AccountCreation:
				currentUser = runAccountCreation();
				break;
			case LoggedIn:
				runLoggedIn(currentUser);
				break;
			default:
				throw new Exception("Nonhandled Terminal State");
			}
		}
		System.out.println("I'm sorry, Dave. I'm afraid I can't do that");
		System.out.println("Daisy, daisy, give me you answer do...");
		System.out.println("...");
	}

	public void runSplashScreen() {
		System.out.println("------------------------------------------------------------------------------");
		System.out.println("Welcome to CasinoSharks!");
		while (state != State.Exiting) {
			System.out.println("Enter the designated number for each service:\n");
			System.out.println(tab + "1. Create a new Accout");
			System.out.println(tab + "2. Login to interact with our system");
			System.out.println(tab + "3. Terminate the program");
			String input = scanner.nextLine().toLowerCase();

			switch (input) {
			case "1":
				System.out.println("Switching to the Account Wizard...");
				state = State.AccountCreation;
				return;
			case "2":
				System.out.println("Switching to Login Screen...");
				state = State.LoggingIn;
				return;
			case "3":
				state = State.Exiting;
				return;
			default:
				System.out.println("Number not recognized, try again.");
			}
		}
	}

	public User runLogin() throws Exception {
		System.out.println("\n------------------------------------------------------------------------------");
		System.out.println("Login Screen: \n");

		while (state != State.SplashScreen || state != State.Exiting) {
			System.out.println(tab + "Enter your Username to begin login: ");
			System.out.println(tab + "1. Return to splashscreen: ");
			System.out.println(tab + "2. Terminate the program: ");

			String input = scanner.nextLine();
			if (input.equals("1")) {
				state = State.SplashScreen;
				return null;
			}
			if (input.equals("2")) {
				state = State.Exiting;
				return null;
			}

			String username = input;
			System.out.println("Enter your password: ");
			String password = scanner.nextLine();

			User user = UserDao.getUser(username, password);
			if (user != null) {
				String name = user.getUserInfo().getFirstName();
				state = State.LoggedIn;
				return user;
			} else
				System.out.println("Invalid credentials, please try again.");
		}
		return null;
	}

	public User runAccountCreation() {
		System.out.println("\n------------------------------------------------------------------------------");
		System.out.println("Account creation wizard: \n");
		String username, password, fname, lname, social, address;
		do {
			System.out.println("Enter your desired username: ");
			username = scanner.nextLine();
		} while (!UserInfo.isNameValid(username) || UserDao.containsUsername(username));
		do {
			System.out.println("Enter your password: ");
			password = scanner.nextLine();
		} while (!UserInfo.isNameValid(password));
		do {
			System.out.println("Enter your first name: ");
			fname = scanner.nextLine();
		} while (!UserInfo.isNameValid(fname));
		do {
			System.out.println("Enter your last name: ");
			lname = scanner.nextLine();
		} while (!UserInfo.isNameValid(lname));
		do {
			System.out.println("Enter your social security number without hyphens: ");
			social = scanner.nextLine();
		} while (!UserInfo.isSocialSecurityNumberValid(social));
		do {
			System.out.println("Enter your address: ");
			address = scanner.nextLine();
		} while (!UserInfo.isAddressValid(address));

		UserInfo userInfo = new UserInfo(fname, lname, social, address);
		User user = new User(null, username, password, Permissions.User, fname, lname, social, address);
		UserDao.addUser(user);
		System.out.println("User account created! Logging in...");
		state = State.LoggedIn;
		return user;
	}

	public void runLoggedIn(User currentUser) throws Exception {
		Permissions currentPermission = currentUser.getPermission();
		switch (currentPermission) {
		case User:
			runUserLoggedIn(currentUser);
			break;
		case Dealer:
			runDealerLoggedIn(currentUser);
			break;
		case Pitboss:
			runPitbossLoggedIn(currentUser);
			break;
		default:
			throw new Exception("Shouldn't be logged in without a permission");
		}
	}

	public void runUserLoggedIn(User currentUser) {

		while (state != State.Exiting) {
			// Basic login info
			System.out.println("\n------------------------------------------------------------------------------");
			System.out.println("User Portal  -- Logged in as " + currentUser.getUsername());
			System.out.println("------------------------------------------------------------------------------");

			// Change to method?
			List<Account> userAccounts = AccountDao.getUserAccounts(currentUser.getUserID());
			if (userAccounts.size() == 0) {
				System.out.println(tab + "You have no approved accounts :( ");
			} else {
				System.out.println(tab + "Accounts: ");
				for (Account account : userAccounts)
					System.out.println(tab + tab + account.toPartialString(tab + tab + tab, tab));
			}

			// Change to method?
			List<AccountApp> userAccountApps = AccountAppDao.getUserAccountApps(currentUser.getUserID());
			List<AccountApp> accountAppsPending = AccountAppDao.filterForAppState(userAccountApps, "Pending");
			List<AccountApp> accountAppsDenied = AccountAppDao.filterForAppState(userAccountApps, "Denied");

			if (accountAppsPending.size() == 0 && accountAppsDenied.size() == 0) {
				System.out.println(tab + "No Account Applications Pending or Denied.");
			} else {
				if (accountAppsPending.size() != 0) {
					System.out.println(tab + "Accounts Pending: ");
					for (AccountApp accountApp : accountAppsPending)
						System.out.println(tab + tab + accountApp.toString());
				}
				if (accountAppsDenied.size() != 0) {
					System.out.println(tab + "Accounts Denied: ");
					for (AccountApp accountApp : accountAppsDenied)
						System.out.println(tab + tab + accountApp.toString());
				}
			}

			System.out.println();
			System.out.println(tab + "Enter 'apply' to request a new account");
			System.out.println(tab + "Enter 'join' to apply for joint access");
			if (userAccounts.size() != 0)
				System.out.println(tab + "Enter 'transact' to withdraw/deposit money into/from our Casino");
			if (userAccounts.size() > 1)
				System.out.println(tab + "Enter 'transfer' to move money between accounts at our Casino");
			System.out.println(tab + "Enter 'leave' to logout");
			System.out.println(tab + "Enter 'exit' to stop the program");
			System.out.println();

			String input = scanner.nextLine();
			switch (input) {
			case "apply":
				applyForAccount(currentUser);
				break;
			case "join":
				applyForJointAccount(currentUser);
				break;
			case "transact":
				if (userAccounts.size() != 0)
					transactWithOneAccount(currentUser, userAccounts);
				else
					System.out.println("Invalid command");
				break;
			case "transfer":
				if (userAccounts.size() >= 2)
					tranferBetweenAccounts(currentUser, userAccounts);
				else
					System.out.println("Invalid command");
				break;
			case "leave":
				System.out.println("Logging out...");
				state = State.SplashScreen;
				return;
			case "exit":
				state = State.Exiting;
				return;
			default:
				System.out.println("Unrecognized command, try again");
			}
		}
	}

	private void runDealerLoggedIn(User currentUser) {
		while (state != State.Exiting || state != State.SplashScreen) {
			System.out.println("\n------------------------------------------------------------------------------");
			System.out.println("Dealer Portal  -- Logged in as " + currentUser.getUsername());
			System.out.println("------------------------------------------------------------------------------\n");

			// Change to method?
			List<Account> dealerAccounts = AccountDao.getUserAccounts(currentUser.getUserID());
			if (dealerAccounts.size() == 0) {
				System.out.println(tab + "You have no approved accounts :( ");
			} else {
				System.out.println(tab + "Accounts: ");
				for (Account account : dealerAccounts)
					System.out.println(tab + tab + account.toPartialString(tab + tab + tab, tab));
			}

			// Get *your* account applications
			List<AccountApp> userAccountApps = AccountAppDao.getUserAccountApps(currentUser.getUserID());
			List<AccountApp> accountAppsPending = AccountAppDao.filterForAppState(userAccountApps, "Pending");
			List<AccountApp> accountAppsDenied = AccountAppDao.filterForAppState(userAccountApps, "Denied");
			if (accountAppsPending.size() == 0 && accountAppsDenied.size() == 0) {
				System.out.println(tab + "No Account Applications Pending or Denied.");
			} else {
				if (accountAppsPending.size() != 0) {
					System.out.println(tab + "Accounts Pending: ");
					for (AccountApp accountApp : accountAppsPending)
						System.out.println(tab + tab + accountApp.toString());
				}
				if (accountAppsDenied.size() != 0) {
					System.out.println(tab + "Accounts Denied: ");
					for (AccountApp accountApp : accountAppsDenied)
						System.out.println(tab + tab + accountApp.toString());
				}
			}
			
			System.out.println();
			// Get all pending *User* account applications
			List<AccountApp> pendingApps = AccountAppDao.getPendingUserAccountApps();
			if (pendingApps.size() > 0)
				System.out.println(tab + "Enter 'review' to Approve or Deny pending account applications");
			System.out.println(tab + "Enter 'view' to view user information");
			System.out.println(tab + "Enter 'leave' to logout");
			System.out.println(tab + "Enter 'exit' to stop the program");
			System.out.println();

			String input = scanner.nextLine().toLowerCase();
			switch (input) {
			case "review":
				if (pendingApps.size() > 0)
					reviewAccountApplications(currentUser, pendingApps);
				else
					System.out.println("There are no pending applications!");
				break;
			case "view":
				viewUserInfo(currentUser);
				break;
			case "join":
				applyForJointAccount(currentUser);
				break;
			case "transact":
				transactWithOneAccount(currentUser, dealerAccounts);
				break;
			case "transfer":
				tranferBetweenAccounts(currentUser, dealerAccounts);
				break;
			case "leave":
				System.out.println("Logging out...");
				state = State.SplashScreen;
				return;
			case "exit":
				state = State.Exiting;
				return;
			default:
				System.out.println("Unrecognized command, try again");
			}
		}
	}

	private void viewUserInfo(User currentUser) {
		System.out.println("------------------------------------------------------------------------------");
		System.out.println("User Information Portal");

		while (state != State.Exiting) {
			System.out.println();
			System.out.println(tab + "Enter 'id [userID]' to search via numeric ID");
			System.out.println(tab + "Enter 'username [username] to search via alphanumeric ID");
			System.out.println(tab
					+ "Enter 'like [partial username]' to search for all usernames starting with the given partial name");
			System.out.println(tab + "E.g 'like Wil' would find usernames like 'Will', 'William', 'Wilbert', etc");

			String input = scanner.nextLine();
			if (input.equals("leave"))
				return;
			else if (input.equals("exit")) {
				state = State.Exiting;
				return;
			} else {
				String[] tokens = input.split(" ");
				if (tokens.length != 2) {
					System.out.println("Each command must be in the form '[id/username/like] [searchTerm]'");
					continue;
				}
				String command = tokens[0];
				Set<User> users = new TreeSet<User>();
				switch (command) {
				case "id":
					try {
						int userID = Integer.valueOf(tokens[1]);
						User user = UserDao.getUser(userID);
						if (user != null)
							users.add(user);
					} catch (NumberFormatException e) {
						System.out.println("ID isn't a valid number, try again");
						continue;
					}
					printAllUsersInfo(users);
					break;
				case "username":
					User user = UserDao.getUser(tokens[1]);
					if (user != null)
						users.add(user);
					printAllUsersInfo(users);
					break;
				case "like":
					users = UserDao.getUsernamesLike(tokens[1]);
					printAllUsersInfo(users);
					break;
				default:
					System.out.println("Unrecognized command. Only 'id', 'username' and 'like' are accepted");
				}
			}
		}
	}

	private void printAllUsersInfo(Set<User> users) {
		if (users.size() == 0)
			System.out.println("No users found with your search term");

		for (User user : users) {
			System.out.println(String.format(tab + "ID: %s - Username: %s - Permission: %s", user.getUserID(),
					user.getUsername(), user.getPermission()));
			System.out.println(tab + tab + "Name: " + user.getFullName());
			System.out.println(tab + tab + "Social: " + user.getUserInfo().getSocialSecurityNumber());
			System.out.println(tab + tab + "Address: " + user.getUserInfo().getAddress());
			List<Account> accounts = AccountDao.getUserAccounts(user.getUserID());

			String margin = tab + tab + tab;
			if (accounts.size() != 0) {
				System.out.println(tab + tab + "Accounts: ");

				for (Account account : accounts)
					System.out.println(tab + tab + tab + account.toPartialString(margin, tab));
			}

			List<AccountApp> accountApps = AccountAppDao.getUserAccountApps(user.getUserID());
			if (accountApps.size() != 0) {
				System.out.println(tab + tab + "Account Applications: ");
				for (AccountApp app : accountApps) {
					System.out.println(tab + tab + tab + app.toFullString());
				}
			}
		}
	}

	private void reviewAccountApplications(User currentUser, List<AccountApp> pendingApps) {
		System.out.println("------------------------------------------------------------------------------");
		System.out.println("Pending application wizard: \n");
		System.out.println(tab + "Enter 'approve' to accept the application and create a new account");
		System.out.println(tab + "Enter 'deny' to revoke the application");
		System.out.println(tab + "Enter 'pass' to defer judgement until a later time");
		System.out.println(tab + "Enter 'leave' or 'exit' to return to the last menu/exit respectively.\n");

		for (int i = pendingApps.size() - 1; i >= 0; i--) {
			AccountApp app = pendingApps.get(i);
			System.out.println(tab + app.toString());
			User user = UserDao.getUser(app.getUserID());
			System.out.println(String.format(tab + tab + "Name: %s - Username: %s - Permission: %s\n",
					user.getFullName(), user.getUsername(), user.getPermission()));
			System.out.println(tab + "Input judgement on Application: ");
			String input = scanner.nextLine().toLowerCase();

			switch (input) {
			case "approve":
				AccountAppDao.changeAccountAppState(app, "Approved");
				pendingApps.remove(i);
				break;
			case "deny":
				AccountAppDao.changeAccountAppState(app, "Denied");
				pendingApps.remove(i);
				break;
			case "pass":
				break;
			case "leave":
				return;
			case "exit":
				state = State.Exiting;
				return;
			default:
				System.out.println("Unrecognized command, try again");
				i++;
			}
		}
	}

	private void runPitbossLoggedIn(User currentUser) {
		while (state != State.Exiting || state != State.SplashScreen) {
			System.out.println("\n------------------------------------------------------------------------------");
			System.out.println("Pitboss Portal  -- Logged in as " + currentUser.getUsername());
			System.out.println("------------------------------------------------------------------------------\n");

			List<AccountApp> pendingApps = AccountAppDao.getAllPendingAccountApps();
			if (pendingApps.size() > 0)
				System.out.println(tab + "Enter 'review' to Approve or Deny pending account applications");
			System.out.println(tab + "Enter 'view' to view user information");
			System.out.println(tab + "Enter 'change' to cancel accounts");
			System.out.println(tab + "Enter 'leave' to logout");
			System.out.println(tab + "Enter 'exit' to stop the program");
			System.out.println();

			String input = scanner.nextLine().toLowerCase();
			switch (input) {
			case "review":
				if (pendingApps.size() > 0)
					reviewAccountApplications(currentUser, pendingApps);
				else
					System.out.println("There are no pending applications!");
				break;
			case "view":
				viewUserInfo(currentUser);
				break;
			case "join":
				applyForJointAccount(currentUser);
				break;
			case "transact":
				adminTransactWithOneAccount(currentUser);
				break;
			case "transfer":
				pitbossTranferWithOneAccount(currentUser);
				break;
			case "cancel":
				changeAccountStatus(currentUser);
				break;
			case "leave":
				System.out.println("Logging out...");
				state = State.SplashScreen;
				return;
			case "exit":
				state = State.Exiting;
				return;
			default:
				System.out.println("Unrecognized command, try again");
			}
		}
	}

	private void changeAccountStatus(User currentUser) {
		System.out.println("Cancel a user account: ");
		System.out.println(tab + "Enter 'cancel [accountID]' to cancel the account");
		System.out.println(tab + "Enter 'activate [accountID]' to activate the account");
		try {
			String[] tokens = scanner.nextLine().split(" ");
			if (tokens.length != 2)
				throw new InvalidParameterException();
			String operation = tokens[0];
			Integer account_id = Integer.valueOf(tokens[1]);
			Boolean newStatus;
			if (operation.equals("cancel"))
				newStatus = false;
			else if (operation.equals("activate"))
				newStatus = true;
			else
				throw new InvalidParameterException("Operation isn't valid.");
				
			Account existingAccount = AccountDao.getAccount(account_id);
			if (existingAccount == null) {
				throw new InvalidParameterException("AccountID is not valid");
			} 
			else {
				AccountDao.changeAccountStatus(account_id, newStatus);
				System.out.println(String.format("Account #%s is now Active: %s", account_id, newStatus));
			}				

		} catch (NumberFormatException e) {
			System.out.println("accountID isn't a valid number");
		}		
		catch (InvalidParameterException e) {
			System.out.println(e.getMessage());
		}
	}

	public void applyForAccount(User currentUser) {
		// TODO change to menu system?
		AccountAppDao.addAccountApp(currentUser.getUserID(), 200.0);
		System.out.println("Application submitted! It will be processed soon.");
	}

	public void applyForJointAccount(User currentUser) {
		System.out.println("Joint account application: ");
		System.out.println(tab + "Enter '[accountID] [ownerUsername] [ownerPassword]',");
		System.out.println(tab + "In order to gain access to another user's account");
		try {
			String[] tokens = scanner.nextLine().split(" ");
			if (tokens.length != 3) {
				System.out.println("Invalid number of arguments");
				return;
			}
			Integer account_id = Integer.valueOf(tokens[0]);
			String username = tokens[1];
			String password = tokens[2];
			Account existingAccount = AccountDao.getAccountWithUserCredentials(account_id, username, password);
			if (existingAccount != null) {
				JointAccountDao.addJointOwnerToAccount(account_id, currentUser.getUserID());
			} else
				System.out.println("No account found with given credentials");

		} catch (NumberFormatException e) {
			System.out.println("accountID isn't a valid number");
		}
	}

	public void transactWithOneAccount(User currentUser, List<Account> accounts) {
		System.out.println("Account transaction: Add or remove money from a single account");
		System.out.println(tab + "Enter '[accountID] withdraw [number]' for withdrawals");
		System.out.println(tab + "Enter '[accountID] deposit [number]' for deposits");
		try {
			String[] tokens = scanner.nextLine().split(" ");
			if (tokens.length != 3) {
				System.out.println("Malformed statement. Try '12 withdraw 200.5'");
				return;
			}

			int accountID = Integer.valueOf(tokens[0]);
			String operation = tokens[1];
			Double amount = Double.valueOf(tokens[2]);
			switch (operation) {
			case "withdraw":
				for (Account account : accounts) {
					if (account.getAccountID() == accountID) {
						account.withdrawAmount(amount);
						return;
					}
				}
			case "deposit":
				for (Account account : accounts) {
					if (account.getAccountID() == accountID) {
						account.depositAmount(amount);
						return;
					}
				}
			default:
				System.out.println("Invalid operation. Can only accept 'withdraw' or 'deposit'.");
			}
			// For my accounts, find ones with a given ID and withdraw from them b

		} catch (NumberFormatException e) {
			System.out.println("Numbers aren't formatted corrctly");
		} catch (InvalidParameterException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void adminTransactWithOneAccount(User currentUser) {
		System.out.println("Account transaction: Add or remove money from a single account");
		System.out.println(tab + "Enter '[accountID] withdraw [number]' for withdrawals");
		System.out.println(tab + "Enter '[accountID] deposit [number]' for deposits");
		try {
			String[] tokens = scanner.nextLine().split(" ");
			if (tokens.length != 3) {
				System.out.println("Malformed statement. Try '12 withdraw 200.5'");
				return;
			}

			int accountID = Integer.valueOf(tokens[0]);
			String operation = tokens[1];
			Double amount = Double.valueOf(tokens[2]);
			Account account = AccountDao.getAccount(accountID);
			if (!operation.equals("withdraw") && !operation.equals("deposit"))
				throw new InvalidParameterException("Invalid operation");
			if (amount <= 0)
				throw new InvalidParameterException("Amount isn't positive");
			if (account == null)
				throw new InvalidParameterException("Account doesn't exist");

			switch (operation) {
			case "withdraw":
				account.withdrawAmount(amount);
				break;
			case "deposit":
				account.depositAmount(amount);
				break;
			default:
				System.out.println("Invalid operation. Can only accept 'withdraw' or 'deposit'.");
			}
			// For my accounts, find ones with a given ID and withdraw from them b

		} catch (NumberFormatException e) {
			System.out.println("Numbers aren't formatted corrctly");
		} catch (InvalidParameterException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void tranferBetweenAccounts(User currentUser, List<Account> accounts) {
		try {
			System.out.println("Account transfer: Deposit/withdraw money between 2 accounts you can access");
			System.out.println(tab + "To move money, enter 'transfer [amount] from [accountID1] to [accountID2]'");
			System.out.println(tab + "Alternatively, simply enter '[amount] [accountID1] [accountID2]");
			System.out.println(tab + "e.g 'transfer 100.0 from 1101 to 1102");
			String[] tokens = scanner.nextLine().split(" ");
			Double amount;
			int account1ID;
			int account2ID;

			if (tokens.length == 6) {
				if (tokens[0].equals("transfer") || tokens[2].equals("from") || tokens[4].equals("to")) {
					amount = Double.valueOf(tokens[1]);
					account1ID = Integer.valueOf(tokens[3]);
					account2ID = Integer.valueOf(tokens[5]);
				} else {
					System.out.println("Statement misspelled, ending transaction");
					return;
				}
			} else if (tokens.length == 3) {
				amount = Double.valueOf(tokens[0]);
				account1ID = Integer.valueOf(tokens[1]);
				account2ID = Integer.valueOf(tokens[2]);
			} else {
				System.out.println("Malformed statement, ending transaction");
				return;
			}
			if (account1ID == account2ID)
				throw new InvalidParameterException("Error: Account IDs must be uniqiue");
			if (amount <= 0)
				throw new InvalidParameterException("Error: Amount must be a positive number");

			Optional<Account> account1 = accounts.stream().filter(A -> A.getAccountID() == account1ID).findAny();
			Optional<Account> account2 = accounts.stream().filter(A -> A.getAccountID() == account2ID).findAny();

			if (account1.isPresent() && account2.isPresent()) {
				if (amount > account1.get().getBalance())
					throw new InvalidParameterException(
							"Error: Attempting to withdraw more money than Account " + account1ID + " contains");
				else {
					account1.get().withdrawAmount(amount);
					account2.get().depositAmount(amount);
					System.out.println(String.format("Succesfully moved %s from Account #%s to Account #%s", amount,
							account1ID, account2ID));
				}
			} else
				throw new InvalidParameterException("Error: Cannot find both Account IDs");

		} catch (NumberFormatException e) {
			System.out.println("Numbers aren't formatted corrctly");
		} catch (InvalidParameterException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	public void pitbossTranferWithOneAccount(User currentUser) {
		try {
			System.out.println("Account transfer: Deposit/withdraw money between 2 accounts you can access");
			System.out.println(tab + "To move money, enter 'transfer [amount] from [accountID1] to [accountID2]'");
			System.out.println(tab + "Alternatively, simply enter '[amount] [accountID1] [accountID2]");
			System.out.println(tab + "e.g 'transfer 100.0 from 1101 to 1102");
			String[] tokens = scanner.nextLine().split(" ");
			Double amount;
			int account1ID;
			int account2ID;

			if (tokens.length == 6) {
				if (tokens[0].equals("transfer") || tokens[2].equals("from") || tokens[4].equals("to")) {
					amount = Double.valueOf(tokens[1]);
					account1ID = Integer.valueOf(tokens[3]);
					account2ID = Integer.valueOf(tokens[5]);
				} else {
					System.out.println("Statement misspelled, ending transaction");
					return;
				}
			} else if (tokens.length == 3) {
				amount = Double.valueOf(tokens[0]);
				account1ID = Integer.valueOf(tokens[1]);
				account2ID = Integer.valueOf(tokens[2]);
			} else {
				System.out.println("Malformed statement, ending transaction");
				return;
			}
			if (account1ID == account2ID)
				throw new InvalidParameterException("Error: Account IDs must be uniqiue");
			if (amount <= 0)
				throw new InvalidParameterException("Error: Amount must be a positive number");

			Account account1 = AccountDao.getAccount(account1ID);
			Account account2 = AccountDao.getAccount(account2ID);

			if (account1 != null && account2 != null) {
				if (amount > account1.getBalance())
					throw new InvalidParameterException(
							"Error: Attempting to withdraw more money than Account " + account1ID + " contains");
				else {
					account1.withdrawAmount(amount);
					account2.depositAmount(amount);
					System.out.println(String.format("Succesfully moved %s from Account #%s to Account #%s", amount,
							account1ID, account2ID));
				}
			} else
				throw new InvalidParameterException("Error: Cannot find both Account IDs");

		} catch (NumberFormatException e) {
			System.out.println("Numbers aren't formatted corrctly");
		} catch (InvalidParameterException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
	}

	public void setScanner(Scanner scanner) {
		this.scanner = scanner;
	}

	@Override
	public void finalize() {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("Connection not closed: " + e.getMessage());
			}
		}
	}

	@Override
	public void close() throws Exception {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("Connection not closed: " + e.getMessage());
			}
		}

	}

//	public static String convertToStrikethrough(String sentence) {
//		char[] charArray = sentence.toCharArray();
//		for (int i = 0; i < charArray.length; i++)
//			charArray[i] = convertCharToStrikethrough(charArray[i]);
//		return new String(charArray);
//	}
//	public static char convertCharToStrikethrough(char myChar) {
//		String regular = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
//		String striked = "A̶B̶C̶D̶E̶F̶G̶H̶I̶J̶K̶L̶M̶N̶O̶P̶Q̶R̶S̶T̶U̶V̶W̶X̶Y̶Z̶a̶b̶c̶d̶e̶f̶g̶h̶i̶j̶k̶l̶m̶n̶o̶p̶q̶r̶s̶t̶u̶v̶w̶x̶y̶z̶";
//		int index = regular.indexOf(myChar);
//		return index == -1 ? myChar : striked.charAt(index);
//	}

}
