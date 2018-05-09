package Scrapers;

import SSC.Campus;
import SSC.SSCClient;
import SSC.SSCURL;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import model.Course;
import model.Subject;
import model.SubjectManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CreditScraper {

    private final SSCClient client;

    public CreditScraper(SSCClient client) {
        this.client = client;
    }

    public List<Course> courseCredit(){
        if(!client.requestAuthentication("Access Course Credits")) {
            return Collections.emptyList();
        }
        String response = client.get(SSCURL.COURSE_CREDIT);
        Element table = Jsoup.parse(response).getElementsByTag("tbody").get(0);
        Elements credits = table.getElementsByTag("tr");

        List<Course> courses = new ArrayList<>();

        for(int i = 1; i < credits.size(); i++) {
            Element credit = credits.get(i);
            courses.add(parseCredit(credit));
        }

        client.deAuthenticate();

        return courses;
    }

    private Course parseCredit(Element credit) {
        Elements cols = credit.getElementsByTag("td");
        String name[] = cols.get(0).text().split(" ");
        String sub = name[0];
        String code = name[1];

        int credits = (int) Double.parseDouble(cols.get(2).attr("credits"));

        Subject subject = SubjectManager.getInstance().getSubject(sub);
        Course course = subject.getCourse(code);
        course.setCredits(credits);
        return course;
    }

    public List<Course> transferCredit(Campus campus){
        if(!client.requestAuthentication("Access Transfer Credits")) {
            return Collections.emptyList();
        }
        try {
            //set campus to vancouver
            HtmlPage page = client.getClient().getPage(SSCURL.TRANSFER_CREDIT.toString());
            HtmlSelect select = page.getElementByName("comboBox");
            select.setSelectedIndex(campus.getTransferIndex());

            if (hasTransferCredit(page)){
                return parseTransferCredits(page);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            client.deAuthenticate();
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

        String name[] = cols.get(1).text().split(" ");
        String sub = name[0];
        String code = name[1];

        int credits = Integer.parseInt(cols.get(3).text());

        Subject subject = SubjectManager.getInstance().getSubject(sub);
        Course course = subject.getCourse(code);
        course.setCredits(credits);
        return course;
    }

}
