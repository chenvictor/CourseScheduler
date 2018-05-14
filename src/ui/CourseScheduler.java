package ui;

import SSC.SSCClient;
import Scrapers.CourseScraper;
import model.Student;
import ui.tabs.CourseTab;
import ui.tabs.CreditsTab;
import ui.tabs.Tab;
import ui.tabs.TimetableTab;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.ArrayList;
import java.util.List;

public class CourseScheduler {

    private final Student student;
    private SSCClient sscClient;

    private JFrame frame;
    private JTabbedPane tabs;
    private List<Tab> tabList;

    public CourseScheduler() {
        sscClient = new SSCClient();
        sscClient.setSaveAuthentication(true);
        student = new Student();
        frame = new JFrame("Course Scheduler");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        generateTabs();
        frame.pack();
        frame.setVisible(true);
        //first tab open
        currentTab().onSelected();
    }

    private void generateTabs() {
        tabs = new JTabbedPane();
        tabList = new ArrayList<>();
        CourseScraper courseScraper = new CourseScraper(sscClient);
        TimetableTab timetableTab = new TimetableTab(sscClient, courseScraper);
        tabList.add(new CourseTab(courseScraper, timetableTab));
        tabList.add(new CreditsTab(sscClient, student));
        tabList.add(timetableTab);
        for(Tab tab : tabList) {
            tabs.add(tab.getTitle(), tab.getContent());
        }
        tabs.addChangeListener(new TabChangeListener());
        frame.add(tabs);
    }

    private class TabChangeListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {
            currentTab().onSelected();
        }
    }

    private Tab currentTab() {
        return tabList.get(tabs.getSelectedIndex());
    }
}
