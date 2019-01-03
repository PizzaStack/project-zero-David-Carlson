package sylvernale.bank;

import java.security.InvalidParameterException;

public enum AccountType {
	Checking,
	Savings;
	
	public static AccountType parseAccountType(String accountType) {
		switch(accountType) {
		case "Checking":
			return AccountType.Checking;
		case "Savings":
			return AccountType.Savings;
			default:
				throw new InvalidParameterException("Invalid accountType string");
		}
	}
	
	public String toString() {
		switch(this) {
		case Checking:
			return "Checking";
		case Savings:
			return "Savings";
		default:
			throw new InvalidParameterException("Account Type somehow invalid in toString method");
		}
	}

}
