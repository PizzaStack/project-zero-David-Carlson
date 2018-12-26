package sylvernale.bank;

public enum Permissions {
	User, 
	Dealer, 
	Pitboss;
	
	public static Permissions parsePermission(String permission) {
		switch(permission) {
		case "user":
			return Permissions.User;
		case "dealer":
			return Permissions.Dealer;
		case "pitboss":
			return Permissions.Pitboss;
		default:
			return Permissions.User;
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
			return "User";				
		}
	}
	
}
