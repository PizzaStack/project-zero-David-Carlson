package sylvernale.bank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import sylvernale.bank.entity.Account;
import sylvernale.bank.entity.AccountType;
import sylvernale.bank.entity.User;


public class App 
{
    public static void main( String[] args )
    {
    	Terminal term = new Terminal(new Scanner(System.in));
    	try {
			term.runTerminal();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println( "App Ended" );
    }
}
