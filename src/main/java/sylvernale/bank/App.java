package sylvernale.bank;

import java.util.Random;
import java.util.Scanner;

import dao.UserDao;
import sylvernale.bank.entity.User;


public class App 
{
    public static void main( String[] args )
    {
//    	stats();

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
    public static void stats() {
    	Double play = 0.0;
    	Double cheat = 0.0;
    	Double N = 1000000.0;
    	for(int i = 0; i < N; i++) {
    		play += 1000 * Terminal.getGaussian(-0.1, 5.0);
    		cheat += 1000 * Terminal.getGaussian(0.2, 5.0);
    	}
    	System.out.println("Play/cheat " + play/N + " " + cheat/N);
    }
}
