package sylvernale.bank;

import java.util.Scanner;


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
