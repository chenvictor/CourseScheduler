package SSC;

import SSC.Exceptions.InvalidLoginException;
import com.gargoylesoftware.htmlunit.html.*;
import model.Course;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Scraper {

    private final SSCClient client;

    public Scraper(SSCClient client) {
        this.client = client;
        //make sure the client is logged in
        try {
            this.client.login();
        } catch (InvalidLoginException e) {
            System.out.println("Client is invalid");
        }
    }

    public List<Course> transferCredit(){
        try {
            //set campus to vancouver
            HtmlPage page = client.getClient().getPage(SSCURL.TRANSFER_CREDIT.toString());
            HtmlSelect select = page.getElementByName("comboBox");
            select.setSelectedIndex(1);

            if (hasTransferCredit(page)){
                return parseTransferCredits(page);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    private boolean hasTransferCredit(HtmlPage page){
        DomElement noData = page.getElementById("noData");
        String displayed = noData.getStyleElement("display").getValue();
        return displayed.equals("none");
    }

    private List<Course> parseTransferCredits(HtmlPage page){
        Elements credits = Jsoup.parse(page.getWebResponse().getContentAsString()).select("tr.UBC");

        List<Course> courses = new ArrayList<>();

        for(Element credit : credits) {
            courses.add(parseTransferCredit(credit));
        }
        return courses;
    }

    private Course parseTransferCredit(Element credit) {
        Elements cols = credit.getElementsByClass("listRow");
        String name = cols.get(1).text();
        int credits = Integer.parseInt(cols.get(3).text());
        return new Course(name, credits);
    }

}
