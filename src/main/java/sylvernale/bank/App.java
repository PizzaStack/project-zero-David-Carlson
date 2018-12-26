package sylvernale.bank;

import java.util.Scanner;

import dao.UserDao;
import sylvernale.bank.entity.User;


public class App 
{
    public static void main( String[] args )
    {
    	Terminal term = new Terminal(new Scanner(System.in));
    	term.recreateDatabases();
    	term.fillDatabase();
    	try {
			term.runTerminal();

//    		User user = UserDao.getUser("u0");
//    		System.out.println(user.toString());
//    		for (User u : UserDao.getAllUsers())
//    			System.out.println(u.toString());
    		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println( "[Program Dead]" );
    }
}
