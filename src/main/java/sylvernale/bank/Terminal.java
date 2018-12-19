package sylvernale.bank;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import sylvernale.bank.entity.Account;
import sylvernale.bank.entity.User;
import sylvernale.bank.entity.UserInfo;
import sylvernale.bank.entity.AccountType;

public class Terminal {

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
	}
	
	public void fillDatabase() {
		String[] names = {"user", "user2", "dealer", "pitboss"};
		Permissions[] permissions = {
				Permissions.User, Permissions.User, 
				Permissions.Dealer, Permissions.Pitboss};
		for (int i = 0; i < names.length; i++) {
			String n = names[i];
			User user = new User(i, n, n, permissions[i], n,n,n,n);
			database.addUser(user);
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
		System.out.println("Login Screen: \n");
		String input = null;
		do {
			System.out.println("Enter 'leave' to return to splashscreen: ");
			System.out.println("Enter 'exit' to terminate the program: ");
			System.out.println("Enter your Username to begin login: ");

			input = scanner.nextLine();
			if (input.equals("leave")) {
				state = State.SplashScreen;
				return null;
			}
			if (hasEnteredExitKeyword(input)) {
				state = State.Exiting;
				return null;
			}

			// TODO: If database doesn't contain name, message and continue
			String username = input;
			System.out.println("Enter your password: ");
			String password = scanner.nextLine();

			if (database.containsUser(username, password)) {
				User user = database.getUser(username);
				String name = user.getUserInfo().getFirstName();
				System.out.println("Welcome, " + name + ".");
				state = State.LoggedIn;
				return user;
			} else
				System.out.println("Invalid credentials, please try again.");

		} while (!hasEnteredExitKeyword(input));
		return null;
	}

	public User runAccountCreation() {
		System.out.println("Account creation wizard: \n");
		String username, password, fname, lname, social, address;
		do {
			System.out.println("Enter your desired username: ");
			username = scanner.nextLine();
		} while (!UserInfo.isNameValid(username) || database.containsUser(username));
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
		
		// I'm 'protecting' the password
		User user = new User(userInfo, database.getNextUserID(), username, User.hashPassword(password));
		database.addUser(user);
		System.out.println("User account created! Logging in...");
		state = State.LoggedIn;
		return user;
	}

	public User runLoggedIn(User currentUser) throws Exception {
		Permissions currentPermission = currentUser.getPermission();
		switch (currentPermission) {
		case User:
			return runUserLoggedIn(currentUser);
		case Dealer:
			return null; // TODO Change
		case Pitboss:
			return null; // TODO change
		default:
			throw new Exception("Shouldn't be logged in without a permission");
		}
	}

	public void runUserLoggedIn(User currentUser) {

		while (state != State.Exiting) {
			System.out.println("\nUser Portal  -- " + currentUser.getUsername());
			List<Account> userAccounts = currentUser.getAccounts();
			if (userAccounts.size() == 0) {
				System.out.println("\tYou have no approved accounts :( ");
			} else {
				System.out.println("\tAccounts: ");
				for (Account account : userAccounts)
					System.out.println("\t\t" + account.toString());
			}

			System.out.println("\nEnter 'apply' to request a new account");
			System.out.println("Enter 'join' to apply for joint access");
			System.out.println("Enter 'transact' to withdraw/deposit money into/from our Casino");
			if (userAccounts.size() > 1)
				System.out.println("Enter 'transfer' to move money between accounts at our Casino");

			String input = scanner.nextLine();
			switch (input) {
			case "apply":
				applyForAccount(currentUser);
				break;
			case "join":
				applyForJointAccount();
				break;
			case "transact":
				break;
			case "transfer":
				if (userAccounts.size() < 1) {
					System.out.println("You only have access to one account, cannot transfer");
					break;
				} else {

				}
			}
		}
	}

	public void applyForAccount(User currentUser) {
		database.addAccountApp(currentUser);
		System.out.println("Application submitted! It will be processed soon.");
	}

	public void applyForJointAccount() {
		System.out.println("Joint account application");
		System.out.println("Enter '[accountID] [ownerUsername] [ownerPassword]'");
		System.out.print("In order to gain access to another user's account");
		try {
			String[] tokens = scanner.nextLine().split(" ");
			if (tokens.length != 3) {
				System.out.println("Invalid number of arguments");
				return;
			}
			Integer accountID = Integer.valueOf(tokens[0]);
			String username = tokens[1];
			String password = String.valueOf(tokens[2].hashCode());
			if (database.isAccountOwnedByUser(accountID, username, User.hashPassword(password))) {

			} else {
				System.out.println("No account found with given credentials");
				return;
			}
		} catch (Exception e) {

		}

	}

	public void transactWithOneAccount(List<Account> accounts) {
		System.out.print("Enter '[accountID] withdraw [number]' for withdrawals");
		System.out.print("Enter '[accountID] deposit [number]' for deposits");
		try {
			String[] tokens = scanner.nextLine().split(" ");
			if (tokens.length != 3) {
				System.out.println("Malformed statement. Try '12 withdraw 200.5'");
				return;
			}

			int accountID = Integer.valueOf(tokens[0]);
			String operation = tokens[1];
			Double amount = Double.valueOf(tokens[2]);
			// For my accounts, find ones with a given ID and withdraw from them b
			for (Account account : accounts) {
				if (account.getAccountID() == accountID)
					account.withdrawAmount(amount);
			}
		} catch (Exception e) {

		}

	}

	public void tranferBetweenAccounts(List<Account> accounts) {

	}

	public Boolean hasEnteredExitKeyword(String input) {
		input = input.toLowerCase();
		return Arrays.stream(exitKeywords).anyMatch(input::equals);
	}

	public void setScanner(Scanner scanner) {
		this.scanner = scanner;
	}

}
