package entitytest;

import org.junit.Test;

import sylvernale.bank.entity.User;
import sylvernale.bank.entity.UserInfo;
import static org.junit.Assert.*;
import org.junit.Before;

public class TestUser {
	UserInfo userInfo;
	int accountID = 1234;
	User user;
	
	@Before 
	public void setupTest() {
		userInfo = new UserInfo("Sean", "Bean", "123456789", "5 Guy Drive");
		user = new User(userInfo, accountID);
	}
	
	@Test
	public void testAddUser() {
		
	}
	
}
