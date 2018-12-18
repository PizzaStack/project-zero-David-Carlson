package entitytest;

import org.junit.Test;

import sylvernale.bank.entity.Account;
import sylvernale.bank.entity.User;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;

public class AccountTest {
	double balance = 1000;
	Account.AccountType accountType = Account.AccountType.Credit;
	User user;
	Account account;
	
	@Before
	public void setupNecessaryClasses() {
		user = new User();		
		account = new Account(user, balance, accountType);
	}
	
	@Test 
	public void constructAccount() {
		Account account = new Account();
		assertNotNull(account);
	}
	@Test 
	public void constructAccountWithData() {
		User user = new User();
		double balance = 100;
		Account.AccountType type = Account.AccountType.Credit;
		Account account = new Account(user, balance, type);
		assertNotNull(account);
	}
	@Test 
	public void testChangeBalance() {
		double balanceDelta = -500;
		account.changeBalance(balanceDelta);
		assertEquals(account.getBalance(), balance + balanceDelta, 0.01);
	}
	@Test
	public void testOverdraw() {
		account.changeAccountType(Account.AccountType.Debit);
		double balanceDelta = -1500;
		account.changeBalance(balanceDelta);
		assertEquals(account.getBalance(), 0, 0.01);
	}
	
	@Test
	public void testGetOwners() {
		List<User> owners = account.getOwners();
		assertTrue(owners.contains(user));
	}
	
	@Test
	public void testAddOwner() {
		User user2 = new User();
		account.addOwner(user2);
		List<User> users = account.getOwners();
		assertTrue(users.contains(user2));
	}
	@Test 
	public void testContainsUser() {
		assertTrue(account.containsOwner(user));
		assertFalse(account.containsOwner(new User()));
	}
	@Test
	public void testGetAccountType() {
		assertEquals(account.getAccountType(), accountType);
	}
	@Test
	public void testGetBalance() {
		assertEquals(account.getBalance(), balance, 0.01);
	}
	
	@Test
	public void testChangeAccountType() {
		Account.AccountType newType = Account.AccountType.Debit;
		account.changeAccountType(newType);
	}
}