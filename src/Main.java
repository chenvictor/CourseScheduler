
import SSC.SSCClient;
import Scrapers.CreditScraper;
import Scrapers.Scraper;
import model.Course;

import java.util.List;

public class Main {

    public static void main(String[] args){

        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
        java.util.logging.Logger.getLogger("org.apache.http").setLevel(java.util.logging.Level.OFF);

        SSCClient client = new SSCClient();

        client.authenticate("chen1999", "ViCh3251999!1");

        CreditScraper scraper = new CreditScraper(client);

        List<Course> courseCredits = scraper.courseCredit();
        List<Course> transferCredits = scraper.transferCredit();

        int total = 0;

        System.out.println("Course Credits:");
        for (Course c : courseCredits) {
            System.out.println(c.getName() + " - " + c.getCredits());
            total += c.getCredits();
        }
        System.out.println();
        System.out.println("Transfer Credits:");
        for (Course c : transferCredits) {
            System.out.println(c.getName() + " - " + c.getCredits());
            total += c.getCredits();
        }

    }

}
