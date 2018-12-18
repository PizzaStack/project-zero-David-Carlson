package entitytest;

import org.junit.Test;

import sylvernale.bank.entity.UserInfo;

import static org.junit.Assert.*;

import org.junit.Before;

public class TestUserInfo {
	UserInfo userInfo;
	@Before
	public void setupTest() {
		userInfo = new UserInfo("Sean", "Bean", "123456789", "5 Guy Drive");
	}
	
	@Test
	public void testAddUser() {
		
	}
	
}
