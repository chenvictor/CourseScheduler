package Scrapers;

import SSC.SSCClient;

public class Scraper {

    private final SSCClient client;

    private final CreditScraper creditScraper;
    private final CourseScraper courseScraper;

    public Scraper(SSCClient client) {
        this.client = client;
        this.creditScraper = new CreditScraper(client);
        this.courseScraper = new CourseScraper(client);
    }



}
