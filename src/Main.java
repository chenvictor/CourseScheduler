import SSC.Exceptions.InvalidLoginException;
import SSC.SSCClient;
import SSC.Scraper;
import model.Course;

import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args){

        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
        java.util.logging.Logger.getLogger("org.apache.http").setLevel(java.util.logging.Level.OFF);

//        Scanner scan = new Scanner(System.in);
//
//        System.out.println("Enter username:");
//        String username = scan.nextLine();
//
//        System.out.println("Enter password:");
//        String password = scan.nextLine();

        SSCClient client = new SSCClient("chen1999","ViCh3251999!1");

        try {
            client.login();
        } catch (InvalidLoginException e) {
            System.out.println("Failed to log in with");
            System.out.println("User: " + e.getUser());
            System.out.println("Pass: " + e.getPass());
            return;
        }

        Scraper scraper = new Scraper(client);

        List<Course> transferCredits = scraper.transferCredit();

        System.out.println("Transfer Credits:");
        for (Course c : transferCredits) {
            System.out.println(c.getName() + " - " + c.getCredits());
        }

    }

}
