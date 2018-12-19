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
	protected User currentUser;
	protected Permissions currentPermission;
	protected State state;

	protected enum State {
		SplashScreen, LoggingIn, AccountCreation, LoggedIn, Exiting
	}

	protected Scanner scanner;
	protected String[] exitKeywords = { "quit", "exit", "close", "kill" };

	public Terminal() {
		database = new Database();
		state = State.SplashScreen;
		currentPermission = Permissions.None;
	}

	public Terminal(String loadPath, String savePath) {
		database = new Database(loadPath, savePath);
		state = State.SplashScreen;
		currentPermission = Permissions.None;
	}

	public void runTerminal() throws Exception {
		while (state != State.Exiting) {
			switch (state) {
			case SplashScreen:
				runSplashScreen();
				break;
			case LoggingIn:
				runLogin();
				break;
			case AccountCreation:
				runAccountCreation();
				break;
			case LoggedIn:
				runLoggedIn();
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

	public void runLogin() throws Exception {
		System.out.println("Login Screen: \n");
		String input = null;
		do {
			System.out.println("Enter 'leave' to return to splashscreen: ");
			System.out.println("Enter 'exit' to terminate the program: ");
			System.out.println("Enter your Username to begin login: ");

			input = scanner.nextLine();
			if (input.equals("leave")) {
				state = State.SplashScreen;
				return;
			}
			if (hasEnteredExitKeyword(input)) {
				state = State.Exiting;
				return;
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
				return;
			} else
				System.out.println("Invalid credentials, please try again.");

		} while (!hasEnteredExitKeyword(input));
	}

	public void runAccountCreation() {
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
		password = String.valueOf(password.hashCode());
		User user = new User(userInfo, username, password, database.getNextUserID());
		database.addUser(user);
		System.out.println("User account created! Logging in...");
		currentUser = user;
		state = State.LoggedIn;
		currentPermission = Permissions.User;
	}

	public void runLoggedIn() throws Exception {
		switch (currentPermission) {
		case User:
			runUserLoggedIn();
			break;
		case Dealer:
			break;
		case Pitboss:
			break;
		default:
			throw new Exception("Shouldn't be logged in without a permission");
		}
	}

	public void runUserLoggedIn() {

		while (state != State.Exiting) {
			System.out.println("User Portal  -- " + currentUser.getUsername());
			List<Account> userAccounts = currentUser.getAccounts();
			System.out.println("\tAccounts: ");
			for (Account account : userAccounts)
				System.out.println("\t\t" + account.toString());

			System.out.println("\nEnter 'apply' to request a new account");
			System.out.println("Enter 'join' to apply for joint access");
			System.out.println("Enter 'transact' to withdraw/deposit money into/from our Casino");
			if (userAccounts.size() > 1)
				System.out.println("Enter 'transfer' to move money between accounts at our Casino");

			String input = scanner.nextLine();
			switch (input) {
			case "apply":
				applyForAccount();
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

	public void applyForAccount() {
		// TODO: Add credit check?
		// Need type, 
		AccountType type;
		System.out.println("What type of account? Enter 'credit' or 'debit'");
		String input = scanner.nextLine().toLowerCase();
		switch (input) {
		case "credit":
			type = AccountType.Credit;
			break;
		case "debit":
			type = AccountType.Debit;
			break;
		case "sudo credit":
			database.addAccount(AccountType.Credit, currentUser);
			return;
		case "sudo debit":
			database.addAccount(AccountType.Debit, currentUser);
			return;
		default:
			System.out.println("Incorrect type, creating Debit Application");
			type = AccountType.Debit;				
		}
		database.addAccountApp(type, currentUser);
		System.out.println("Application submitted! It will be processed soon.");	
	}

	public void applyForJointAccount() {
		System.out.println("Joint account application");
		
	}

	public void transactWithOneAccount(List<Account> accounts) {
		System.out.print("Enter '[accountID] withdraw [number]' for withdrawals");
		System.out.print("Enter '[accountID] deposit [number]' for deposits");
		String[] tokens = scanner.nextLine().split(" ");
		if (tokens.length != 3)
		{
			System.out.println("Malformed statement. Try '12 withdraw 200.5'");
		}
		int accountID = Integer.valueOf(tokens[0]);
		String operation = tokens[1];
		Double amount = Double.valueOf(tokens[2]);
		accounts.stream().filter( A -> A.getAccountID() == accountID).map(A -> A.withdrawAmount())
		
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
