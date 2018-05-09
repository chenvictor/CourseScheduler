package ui.tabs;

import SSC.SSCClient;
import Scrapers.CourseScraper;
import model.Course;
import model.Subject;
import model.SubjectManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.Observable;
import java.util.Observer;

public class CourseTab extends Observable implements Tab {

    private SSCClient client;
    private CourseScraper scraper;
    private Subject currentSubject;
    private Course currentCourse;

    private JComboBox subjectSelector;
    private JPanel subjectPanel;
    private JComboBox courseSelector;
    private JPanel coursePanel;

    private JPanel inner;

    private boolean fetched = false;

    public CourseTab(SSCClient client, Observer observer) {
        addObserver(observer);
        this.client = client;
        this.scraper = new CourseScraper(client);
        currentCourse = null;
    }

    @Override
    public JComponent generateContent() {
        JComponent content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JPanel search = new JPanel();

        subjectSelector = new JComboBox();
        subjectSelector.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                if (e.getItem().equals(currentSubject))
                    return; //no change
                currentSubject = (Subject) e.getItem();
                updateSubject();
            }
        });
        courseSelector = new JComboBox();
        courseSelector.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                if (e.getItem() == null) {
                    return;
                }
                currentCourse = (Course) e.getItem();
                updateCourse();
            }
        });
        JButton addButton = new JButton("Add to Timetabler");
        addButton.addActionListener(e -> {
            setChanged();
            notifyObservers(currentCourse);
        });

        search.add(subjectSelector);
        search.add(courseSelector);
        search.add(addButton);

        inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        subjectPanel = new JPanel();
        subjectPanel.setLayout(new BoxLayout(subjectPanel, BoxLayout.Y_AXIS));
        subjectPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subjectPanel.add(new JLabel());
        subjectPanel.add(new JLabel());

        coursePanel = new JPanel();
        coursePanel.setLayout(new BoxLayout(coursePanel, BoxLayout.Y_AXIS));
        coursePanel.add(new JLabel());
        JTextPane description = new JTextPane();
        description.setEditable(false);
        coursePanel.add(description);
        coursePanel.add(new JLabel());
        coursePanel.add(new JLabel());
        coursePanel.add(Box.createVerticalStrut(250));

        inner.add(subjectPanel);
        inner.add(coursePanel);

        content.add(search);
        content.add(inner);
        return content;
    }

    @Override
    public String getTitle() {
        return "Course";
    }

    @Override
    public void onSelected() {
        if(!fetched) {
            scraper.subjects();
            updateSubjects();
            fetched = true;
        }
    }

    private void updateSubjects() {
        subjectSelector.removeAllItems();
        for (Subject subject : SubjectManager.getInstance()) {
            subjectSelector.addItem(subject);
        }
        subjectSelector.revalidate();
        subjectSelector.repaint();
        updateSubject();
    }

    private void updateSubject(){
        updateSubjectLabel();
        scraper.courses(currentSubject);
        updateCourses();
    }

    private void updateCourses() {
        courseSelector.removeAllItems();
        for (Course course : currentSubject) {
            courseSelector.addItem(course);
        }
        courseSelector.revalidate();
        courseSelector.repaint();
    }

    private void updateCourse() {
        scraper.course(currentCourse);
        ((JLabel) coursePanel.getComponent(0)).setText(currentCourse.getTitle());
        ((JTextPane) coursePanel.getComponent(1)).setText(currentCourse.getDescription());
        ((JLabel) coursePanel.getComponent(2)).setText("Credits: " + currentCourse.getCredits());
        ((JLabel) coursePanel.getComponent(3)).setText("Pre-reqs: " + currentCourse.getPreReqs());
        coursePanel.invalidate();
        coursePanel.repaint();
    }

    private void updateSubjectLabel() {
        Subject selected = currentSubject;
        ((JLabel) subjectPanel.getComponent(0)).setText(selected.getCode() + " - " + selected.getTitle());
        ((JLabel) subjectPanel.getComponent(1)).setText(selected.getFacultySchool());
        subjectPanel.invalidate();
        subjectPanel.repaint();
    }

}
