package sylvernale.bank.entity;

public class UserInfo {
	private String firstName;
	private String lastName;
	private String socialSecurityNumber;
	private String address;
	
	public UserInfo(String firstName, String lastName, String socialSecurityNumber, String address) { 
		
	}
	
	public static Boolean isNameValid(String name) {
		return isStringNonEmpty(name);
	}
	public static Boolean isSocialSecurityNumberValid(String socialNumber) {
		return isStringNonEmpty(socialNumber);
	}
	public static Boolean isAddressValid(String streetAddress) {
		return isStringNonEmpty(streetAddress);
	}

	private static Boolean isStringNonEmpty(String word) {
		if (word == null )
			return false;
		word = word.trim();
		if (word == "")
			return false;
		else
			return true;
	}

}
