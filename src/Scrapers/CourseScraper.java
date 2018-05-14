package Scrapers;

import SSC.Campus;
import SSC.SSCClient;
import SSC.SSCURL;
import model.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CourseScraper {

    private final SSCClient client;

    private boolean scrapedSubjects;
    private final List<Subject> scraped;

    private static final String[] temp = {"Waiting List"};
    private static final List<String> blacklistTypes = Arrays.asList(temp);
    private static final String[] temp2 = {"Distance Education", "Flexible Learning"};
    private static final List<String> lectureEquivalents = Arrays.asList(temp2);

    public CourseScraper(SSCClient client) {
        this.client = client;
        scrapedSubjects = false;
        scraped = new ArrayList<>();

        //set up campus and session to search
        client.get(getSubjectURL());

    }

    public void subjects() {
        if (scrapedSubjects) {
            return; //already scraped
        }

        SubjectManager manager = SubjectManager.getInstance();
        //select a campus
        String URL = getSubjectURL();

        for(Element e : Jsoup.parse(client.get(URL)).getElementsByTag("tbody").get(0).select("tr")) {
            Elements elements = e.getElementsByTag("td");
            String code = elements.get(0).text();
            String title = elements.get(1).text();
            String faculty = elements.get(2).text();
            Subject subject = manager.getSubject(code);
            subject.setTitle(title);
            subject.setFacultySchool(faculty);
        }
        scrapedSubjects = true;
    }

    public void courses(Subject subject) {
        if (scraped.contains(subject)) {
            return; //already scraped
        }

        String URL = "https://courses.students.ubc.ca/cs/main?pname=subjarea&tname=subjareas&req=1&dept=" + subject.getCode();

        Document document = Jsoup.parse(client.get(URL));
        Element table = document.selectFirst("tbody");
        Elements elements = table.select("tr");
        for (Element e : elements) {
            Elements data = e.getElementsByTag("td");
            String courseCode = data.get(0).text().split(" ")[1];
            String courseTitle = data.get(1).text();
            Course course = subject.getCourse(courseCode);
            course.setTitle(courseTitle);
        }
        scraped.add(subject);
    }

    public void course(Course course) {
        if (course.hasInfo()) {
            return;
        }
        String courseURL = "https://courses.students.ubc.ca/cs/main?pname=subjarea&tname=subjareas&req=3&dept="
                + course.getSubject().getCode() + "&course=" + course.getCourseCode();

        Document document = Jsoup.parse(client.get(courseURL));
        Element element = document.selectFirst("div.content.expand");

        Elements elements = element.select("p");
        String desc = elements.get(0).text();
        int credits = Integer.parseInt(elements.get(1).text().split(" ")[1]);
        String preReq = "";
        try {
            preReq = elements.get(2).text();
        } catch (IndexOutOfBoundsException e) {
            //no preReq
        }

        course.setDescription(desc);
        course.setCredits(credits);
        if (preReq.startsWith("Pre-reqs: ")) {
            course.setPreReqs(preReq.substring(10));
        } else {
            course.setPreReqs("None");
        }
    }

    public void sections(Course course) {
        if (course.numSections() > 0) {
            return; //already scraped
        }
        String courseURL = "https://courses.students.ubc.ca/cs/main?pname=subjarea&tname=subjareas&req=3&dept="
                + course.getSubject().getCode() + "&course=" + course.getCourseCode();

        Document document = Jsoup.parse(client.get(courseURL));
        Element element = document.selectFirst("div.content.expand");
        Element table = element.selectFirst("tbody");

        Elements sectionRows = table.select("tr");

        String sectionCode = "ERROR";
        String type;
        String term;
        String days;
        String start;
        String end;
        for (Element row : sectionRows) {
            Elements columns = row.select("td");
            String newCode = columns.get(1).text();
            if (newCode.length() != 0) {
                //not overflow from a previous section column
                sectionCode = newCode.split(" ")[2];
                //otherwise, section code is same as before
            }
            if (sectionCode.startsWith("V")) {
                continue;   //ignore V sections (multiterm)
            }
            type = columns.get(2).text();
            if (blacklistTypes.contains(type)) {
                continue;
            } else if (lectureEquivalents.contains(type)) {
                type = "Lecture";   //same as lecture
            }
            term = columns.get(3).text();
            if (term.contains("-")) {
                continue;
            } else if (term.equals("A")) {
                term = "1";
            } else if (term.equals("C")) {
                term = "2";
            }
            days = columns.get(5).text();
            start = columns.get(6).text();
            end = columns.get(7).text();
            Section section = course.getSection(sectionCode);
            section.setType(SectionType.getType(type));
            section.setTerm(term);
            course.addTerm(term);

            for (String day : days.split(" ")) {
                TimeBlock block = new TimeBlock(term, getDay(day), start, end);
                section.addBlock(block);
            }
        }

    }

    private DayOfWeek getDay(String day) {
        switch (day) {
            case "Mon": return DayOfWeek.MONDAY;
            case "Tue": return DayOfWeek.TUESDAY;
            case "Wed": return DayOfWeek.WEDNESDAY;
            case "Thu": return DayOfWeek.THURSDAY;
            case "Fri": return DayOfWeek.FRIDAY;
            case "Sat": return DayOfWeek.SATURDAY;
            default:    return DayOfWeek.SUNDAY;
        }
    }

    private String getSubjectURL() {
        if (client.getSetupURL() != null) {
            return client.getSetupURL();
        }
        Elements selectors = Jsoup.parse(client.get(SSCURL.COURSE_MAINPAGE)).select("ul.dropdown-menu");
        Element campusElement = selectors.get(3);
        Element sessionElement = selectors.get(4);

        List<CampusSelect> campuses = getCampusSelects(campusElement);
        List<Session> sessions = getSessions(sessionElement);

        //CampusSelect campus = promptSelectCampus(campuses);
        CampusSelect campus = campuses.get(0);
        Session session = promptSelectSession(sessions);
        //Session session = sessions.get(1);

        String subjectURL = SSCURL.COURSE_SUBJECTS.toString() + "&campuscd=" + campus.campus.getCode()
                + "&sessyr=" + session.getYear() + "&sesscd=" + session.getCode();
        //save subjectURL somewhere
        client.setSetupURL(subjectURL);
        return subjectURL;
    }

    private CampusSelect promptSelectCampus(List<CampusSelect> campuses) {
        CampusSelect selection = (CampusSelect) JOptionPane.showInputDialog(null, "Campus:", "Select Campus",
                JOptionPane.PLAIN_MESSAGE, null, campuses.toArray(), campuses.get(0));
        if (selection == null) {
            selection = campuses.get(0);
        }
        return selection;
    }

    private Session promptSelectSession(List<Session> sessions) {
        Session selection = (Session) JOptionPane.showInputDialog(null, "Session", "Select Session",
                JOptionPane.PLAIN_MESSAGE, null, sessions.toArray(), sessions.get(0));
        if (selection == null) {
            selection = sessions.get(0);
        }
        return selection;
    }

    private List<Session> getSessions(Element sessionElement) {
        List<Session> sessions = new ArrayList<>();
        for (Element e : sessionElement.select("a")) {
            sessions.add(new Session(e.text()));
        }
        return sessions;
    }

    private List<CampusSelect> getCampusSelects(Element campusElement) {
        List<CampusSelect> campuses = new ArrayList<>();
        for (Element e : campusElement.select("a")) {
            campuses.add(new CampusSelect(e.text(), Campus.get(e.attr("title"))));
        }
        return campuses;
    }

    private class CampusSelect {
        private final String title;
        private final Campus campus;

        CampusSelect(String title, Campus campus) {
            this.title = title;
            this.campus = campus;
        }

        public String getTitle() {
            return title;
        }

        public Campus getCampus() {
            return campus;
        }

        @Override
        public String toString() {
            return title;
        }
    }

    private class Session {
        private final int year;
        private final char code;

        Session(String text) {
            String[] data = text.split(" ");
            year = Integer.parseInt(data[0]);
            code = data[1].charAt(0);
        }

        int getYear() {
            return year;
        }

        char getCode() {
            return code;
        }

        @Override
        public String toString() {
            return year + "" + code;
        }
    }

}
