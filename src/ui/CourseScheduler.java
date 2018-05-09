package ui;

import SSC.SSCClient;
import model.Student;
import ui.tabs.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.ArrayList;
import java.util.List;

public class CourseScheduler {

    private Student student;
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
        TimetableTab timetableTab = new TimetableTab();
        tabList.add(new CourseTab(sscClient, timetableTab));
        tabList.add(new CreditsTab(sscClient, student));
        tabList.add(timetableTab);
        tabList.add(new OptionsTab(sscClient));
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
