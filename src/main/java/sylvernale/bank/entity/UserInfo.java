package sylvernale.bank.entity;

import java.security.InvalidParameterException;

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
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		if (isNameValid(firstName))
			this.firstName = firstName;
		else
			throw new InvalidParameterException(firstName + " isn't a valid name");
	}
	
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		if (isNameValid(lastName))
			this.lastName = lastName;
		else
			throw new InvalidParameterException(lastName + " isn't a valid name");
	}
	
	public String getSocialSecurityNumber() {
		return socialSecurityNumber;
	}
	public void setSocialSecurityNumber(String socialNumber) {
		if (isSocialSecurityNumberValid(socialNumber))
			this.socialSecurityNumber = socialNumber;
		else
			throw new InvalidParameterException(socialNumber + " isn't a valid social number");
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		if (isAddressValid(address))
			this.address = address;
		else
			throw new InvalidParameterException(address + " isn't a valid address");
	}
	


}
