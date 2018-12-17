package sylvernale.bank;

import java.util.Arrays;
import java.util.Scanner;

import sylvernale.bank.entity.User;

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
			input = scanner.nextLine();
			input = input.toLowerCase();
			
			switch(input) {
			case "create":
				System.out.println("Switching to the Account Wizard...");
				state = State.AccountCreation;
				return;
			case "login":
				System.out.println("Switching to Login Screen...");
				state = State.LoggingIn;
				return;
			}
			
		} while(!hasEnteredExitKeyword(input));
		state = State.Exiting;
	}

	public void runLogin() {
		System.out.println("Login Screen: Ente\n");
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
			String name = input;
			System.out.println("Enter your password: ");
			String password = scanner.nextLine();

			if (database.containsUser(name, password)) {
				
			}
			
			
			
		} while(!hasEnteredExitKeyword(input));
	}

	public void runAccountCreation() {

	}

	public void runLoggedIn() {

	}
	public Boolean hasEnteredExitKeyword(String input) {
		return Arrays.stream(exitKeywords).anyMatch(input::equals);
	}

	public void setScanner(Scanner scanner) {
		this.scanner = scanner;
	}

}
