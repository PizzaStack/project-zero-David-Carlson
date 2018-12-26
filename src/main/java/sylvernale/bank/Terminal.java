package sylvernale.bank;

import java.security.InvalidParameterException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import dao.AccountDao;
import dao.UserDao;
import sylvernale.bank.entity.Account;
import sylvernale.bank.entity.User;
import sylvernale.bank.entity.UserInfo;
import sylvernale.bank.entity.AccountType;

public class Terminal {
	public static Connection connection;
	protected Database database;
	protected State state;

	protected enum State {
		SplashScreen, LoggingIn, AccountCreation, LoggedIn, Exiting
	}

	protected Scanner scanner;
	protected String[] exitKeywords = { "quit", "exit", "close", "kill" };

	public Terminal(Scanner scanner) {
		this.scanner = scanner;
		database = new Database();
		state = State.SplashScreen;
		
		String url = "jdbc:postgresql://baasu.db.elephantsql.com:5432/kyhsxtgj";
    	String user = "kyhsxtgj";
    	String password = "a41I5riPacnHC9tuzW8xs0GyramwRr-G";
    	try { Terminal.connection = DriverManager.getConnection(url, user, password); }
    	catch(Exception e) {
    		System.out.println(e.getMessage());
    	}
	}
	
	public void recreateDatabases() {
		String[] dropStatements = {				
				"drop table IF EXISTS accountapps;",
				"drop table IF EXISTS jointowners;",
				"drop table IF EXISTS accounts;",
				"drop table IF EXISTS users;"};
		
		String[] createStatements = {
				"create table users ("
				+ "id serial primary key, username varchar(50) unique, password varchar(50), permission varchar(20), "
				+ "firstname varchar(50), lastname varchar(50), social varchar(9), address varchar(100));",
				 
				"create table accounts ( "
				+ "id serial primary key, "
				+ "user_id serial references users(id) not NULL,"
				+ "balance real not NULL);",
				
				"create table jointowners ( "
				+ "acc_id serial references accounts(id) not NULL,"
				+ "user_id serial references users(id) not NULL,"
				+ "primary key(acc_id, user_id));",
				
				"create table accountapps ("
				+ "id serial primary key, "
				+ "state varchar(20), "
				+ "user_id serial references users(id), "
				+ "balance real not NULL);"};		
//		"create table accountowners ("
//		+ "acc_id serial references accounts(id),"
//		+ "user_id serial references users(id),"
//		+ "primary key(acc_id, user_id));",
		try (Statement statement = connection.createStatement()) {
			for (String drop : dropStatements) {
				try { statement.executeUpdate(drop);}	
				catch(SQLException e) {System.out.println("Error creating table: " + e.getMessage());}
			}				
			for (String create : createStatements) {
				try { statement.executeUpdate(create);}	
				catch(SQLException e) {System.out.println("Error creating table: " + e.getMessage());}
			}
				
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}		
	}


	public void fillDatabase() {
		String[] names = { "u0", "u1", "d0", "d1", "p0", "p1" };
		Permissions[] permissions = { 
				Permissions.User, Permissions.User, 
				Permissions.Dealer, Permissions.Dealer, 
				Permissions.Pitboss, Permissions.Pitboss };
		for (int i = 0; i < names.length; i++) {
			String n = names[i];
			User user = new User(i, n, n, permissions[i], n, n, n, n);
			UserDao.addUser(user);
			AccountDao.addAccount(user);
//			AccountDao.addAccount(user);
		}
	}

	public Terminal(Scanner scanner, String loadPath, String savePath) {
		this.scanner = scanner;
		database = new Database(loadPath, savePath);
		state = State.SplashScreen;
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
		// Do in loop:
		// Print options
		// Parse input (valid? change state, maybe exit message)
		System.out.println("Welcome to CasinoSharks!");
		String input = null;
		do {
			System.out.println("Enter 'create' for new Accout Creation");
			System.out.println("Enter 'login' to interact with our system");
			System.out.println("Enter 'exit' to terminate the program");
			input = scanner.nextLine().toLowerCase();

			switch (input) {
			case "create":
				System.out.println("Switching to the Account Wizard...");
				state = State.AccountCreation;
				return;
			case "login":
				System.out.println("Switching to Login Screen...");
				state = State.LoggingIn;
				return;
			}

		} while (!hasEnteredExitKeyword(input));
		state = State.Exiting;
	}

	public User runLogin() throws Exception {
		System.out.println("User Login Screen: \n");

		while (true) {
			System.out.println("Enter your Username to begin login: ");
			System.out.println("Enter 'leave' to return to splashscreen: ");
			System.out.println("Enter 'exit' to terminate the program: ");
			

			String input = scanner.nextLine();
			if (input.equals("leave")) {
				state = State.SplashScreen;
				return null;
			}
			if (hasEnteredExitKeyword(input)) {
				state = State.Exiting;
				return null;
			}

			String username = input;
			System.out.println("Enter your password: ");
			String password = scanner.nextLine();
			
			User user = UserDao.getUser(username, password);
			if (user != null) {
				String name = user.getUserInfo().getFirstName();
				System.out.println("Welcome, " + name + ".");
				state = State.LoggedIn;
				return user;				
			}
			else
				System.out.println("Invalid credentials, please try again.");
		}
	}

	public User runAccountCreation() {
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
		User user = new User(database.getNextUserID(), username, password, Permissions.User, fname, lname, social,
				address);
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
			System.out.println("\n\n-----------------------------------------------------");
			System.out.println("User Portal  -- Logged in as " + currentUser.getUsername());
			List<Account> userAccounts = AccountDao.getUserAccounts(currentUser);
			if (userAccounts.size() == 0) {
				System.out.println("   You have no approved accounts :( ");
			} else {
				System.out.println("   Accounts: ");
				for (Account account : userAccounts)
					System.out.println("      " + account.toString());
			}

			// Commands to try on account
			System.out.println("\nEnter 'apply' to request a new account");
			System.out.println("Enter 'join' to apply for joint access");
			if (userAccounts.size() != 0)
				System.out.println("Enter 'transact' to withdraw/deposit money into/from our Casino");
			if (userAccounts.size() > 1)
				System.out.println("Enter 'transfer' to move money between accounts at our Casino");
			System.out.println("Enter 'leave' to logout");
			System.out.println("Enter 'exit' to stop the program");

			String input = scanner.nextLine();
			switch (input) {
			case "apply":
				applyForAccount(currentUser);
				break;
			case "join":
				applyForJointAccount();
				break;
			case "transact":
				if (userAccounts.size() != 0)
					transactWithOneAccount(userAccounts);
				else
					System.out.println("Invalid command");
				break;
			case "transfer":
				if (userAccounts.size() >= 2) 					
					tranferBetweenAccounts(userAccounts);
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
			}
		}
	}
	private void runDealerLoggedIn(User currentUser) {
		while (state != State.Exiting) {
			// Basic login info
			System.out.println("\n\n-----------------------------------------------------");
			System.out.println("Dealer Portal  -- " + currentUser.getUsername());
			List<Account> userAccounts = currentUser.getAccounts();
			if (userAccounts.size() == 0) {
				System.out.println("\tYou have no approved accounts :( ");
			} else {
				System.out.println("\tAccounts: ");
				for (Account account : userAccounts)
					System.out.println("\t\t" + account.toString());
			}

			// Commands to try on account
			if (userAccounts.size() != 0)
				System.out.println("Enter 'transact' to withdraw/deposit money into/from our Casino");
			if (userAccounts.size() > 1)
				System.out.println("Enter 'transfer' to move money between accounts at our Casino");
			// Go through 
			System.out.println("Enter 'leave' to logout");
			System.out.println("Enter 'exit' to stop the program");

			String input = scanner.nextLine();
			switch (input) {
			case "apply":
				applyForAccount(currentUser);
				break;
			case "join":
				applyForJointAccount();
				break;
			case "transact":
				if (userAccounts.size() != 0)
					transactWithOneAccount(userAccounts);
				else
					System.out.println("Invalid command");
				break;
			case "transfer":
				if (userAccounts.size() >= 2) 					
					tranferBetweenAccounts(userAccounts);
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
			}
		}

	}

	private void runPitbossLoggedIn(User currentUser) {
		// TODO Auto-generated method stub
		
	}

	public void applyForAccount(User currentUser) {
		database.addAccountApp(currentUser);
		System.out.println("Application submitted! It will be processed soon.");
	}

	public void applyForJointAccount() {
		System.out.println("Joint account application");
		System.out.println("Enter '[accountID] [ownerUsername] [ownerPassword]'");
		System.out.println("In order to gain access to another user's account");
		try {
			String[] tokens = scanner.nextLine().split(" ");
			if (tokens.length != 3) {
				System.out.println("Invalid number of arguments");
				return;
			}
			Integer accountID = Integer.valueOf(tokens[0]);
			String username = tokens[1];
			String password = tokens[2];
			if (database.isAccountOwnedByUser(accountID, username, password)) {

			} else {
				System.out.println("No account found with given credentials");
				return;
			}
		} catch (NumberFormatException e) {
			System.out.println("accountID isn't a valid number");
		} catch (Exception e) {

		}
	}


	public void transactWithOneAccount(List<Account> accounts) {
		System.out.println("");
		System.out.println("Enter '[accountID] withdraw [number]' for withdrawals");
		System.out.println("Enter '[accountID] deposit [number]' for deposits");
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

	public void tranferBetweenAccounts(List<Account> accounts) {
		try {
			System.out.println("To move money, enter 'transfer [amount] from [accountID1] to [accountID2]'");
			System.out.println("Alternatively, simply enter '[amount] [accountID1] [accountID2]");
			System.out.println("e.g 'transfer 100.0 from 1101 to 1102");
			String[] tokens = scanner.nextLine().split(" ");
			Double amount; int account1; int account2;
			
			if (tokens.length == 6) {
				if (tokens[0].equals("transfer") || tokens[2].equals("from") || tokens[4].equals("to")) {
					amount = Double.valueOf(tokens[1]);
					account1 = Integer.valueOf(tokens[3]);
					account2 = Integer.valueOf(tokens[5]);
				}
				else {
					System.out.println("Statement misspelled, ending transaction");
					return;
				}						
			}
			else if (tokens.length == 3) {
				amount = Double.valueOf(tokens[0]);
				account1 = Integer.valueOf(tokens[1]);
				account2 = Integer.valueOf(tokens[2]);
			}
			else {
				System.out.println("Malformed statement, ending transaction");
				return;
			}
			// TODO: Replace all with connection call?
			if (accounts.stream().anyMatch(A -> A.getAccountID() == account1|| A.getAccountID() == account2)
					&& account1 != account2) {
				
				
				
			}
			else {
				System.out.println("Account ID's need to be unique and under your control");
			}			

		} catch (NumberFormatException e) {
			System.out.println("Numbers aren't formatted corrctly");
		} catch (InvalidParameterException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public Boolean hasEnteredExitKeyword(String input) {
		input = input.toLowerCase();
		return Arrays.stream(exitKeywords).anyMatch(input::equals);
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

}
