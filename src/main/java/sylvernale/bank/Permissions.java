package sylvernale.bank;

import java.security.InvalidParameterException;

public enum Permissions {
	User, 
	Dealer, 
	Pitboss,
	None;
	
	public static Permissions parsePermission(String permission) {
		switch(permission) {
		case "User":
			return Permissions.User;
		case "Dealer":
			return Permissions.Dealer;
		case "Pitboss":
			return Permissions.Pitboss;
		default:
			throw new InvalidParameterException("Permission string not correctly formatted");	
		}
	}
	public String toString() {
		switch(this) {
		case User:
			return "User";
		case Dealer:
			return "Dealer";
		case Pitboss:
			return "Pitboss";
		default:
			return "None";	
		}
	}
	
}
