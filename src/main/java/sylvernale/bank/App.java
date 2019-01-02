package sylvernale.bank;

import java.util.Scanner;

import dao.UserDao;
import sylvernale.bank.entity.User;


public class App 
{
    public static void main( String[] args )
    {
    	System.err.println("Standard error line");   	
    	
    	try (Terminal term = new Terminal(new Scanner(System.in))) {
    		term.recreateDatabases();
        	term.fillDatabase();
			term.runTerminal();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println( "[Program Dead]" );
    }
}
