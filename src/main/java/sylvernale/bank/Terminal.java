package sylvernale.bank;

import java.util.Arrays;
import java.util.Scanner;

import sylvernale.bank.entity.User;
import sylvernale.bank.entity.UserInfo;

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
			}
			else
				System.out.println("Invalid credentials, please try again.");

		} while (!hasEnteredExitKeyword(input));
	}

	public void runAccountCreation() {
		System.out.println("Account creation wizard: \n");
		String username, password, fname, lname, social, address;
		do {
			System.out.println("Enter your desired username: ");
			username = scanner.nextLine();
		} while(!UserInfo.isNameValid(username) || database.containsUser(username));
		do {
			System.out.println("Enter your password: ");
			password = scanner.nextLine();
		} while(!UserInfo.isNameValid(password));
		do {
			System.out.println("Enter your first name: ");
			fname = scanner.nextLine();
		} while(!UserInfo.isNameValid(fname));
		do {
			System.out.println("Enter your last name: ");
			lname = scanner.nextLine();
		} while(!UserInfo.isNameValid(lname));
		do {
			System.out.println("Enter your social security number without hyphens: ");
			social = scanner.nextLine();
		} while(!UserInfo.isSocialSecurityNumberValid(social));
		do {
			System.out.println("Enter your address: ");
			address = scanner.nextLine();
		} while(!UserInfo.isAddressValid(address));
		
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

	public void runLoggedIn() {
		switch(currentPermission) {
		case User:
			break;
		case Dealer:
			break;
		case Pitboss:
			break;
		}
	}

	public Boolean hasEnteredExitKeyword(String input) {
		input = input.toLowerCase();
		return Arrays.stream(exitKeywords).anyMatch(input::equals);
	}

	public void setScanner(Scanner scanner) {
		this.scanner = scanner;
	}

}
