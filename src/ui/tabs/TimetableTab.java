package ui.tabs;

import SSC.SSCClient;
import Scrapers.CourseScraper;
import model.Course;
import model.CourseTermCombo;
import model.Timetable;
import model.TimetableVisual;
import ui.TimetableViewer;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeSet;

public class TimetableTab implements Tab, Observer {

    private SSCClient client;
    private CourseScraper scraper;
    private TreeSet<CourseTermCombo> courses;
    private List<Timetable> timetables;

    private JPanel coursesListPanel;
    private TimetableVisual visual;

    private JLabel numCoursesLabel;
    private JLabel numCreditsLabel;

    public TimetableTab(SSCClient client, CourseScraper scraper) {
        this.client = client;
        this.scraper = scraper;
        courses = new TreeSet<>();
        visual = new TimetableVisual();
    }

    @Override
    public JComponent generateContent() {
        JComponent content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JPanel buttonsHeaderPanel = new JPanel();
        JButton clearButton = new JButton("Clear Courses");
        clearButton.addActionListener(e -> clearCourses());
        JButton generateButton = new JButton("Generate Timetables");
        generateButton.addActionListener(e -> generateTimetables());
        buttonsHeaderPanel.add(clearButton);
        buttonsHeaderPanel.add(generateButton);

        JPanel infoHeaderPanel = new JPanel();
        numCoursesLabel = new JLabel("0");
        numCreditsLabel = new JLabel("0");
        infoHeaderPanel.add(new JLabel("Courses: "));
        infoHeaderPanel.add(numCoursesLabel);
        infoHeaderPanel.add(new JLabel("Credits: "));
        infoHeaderPanel.add(numCreditsLabel);

        coursesListPanel = new JPanel();
        GridLayout layout = new GridLayout(0,1);
        layout.setVgap(5);
        coursesListPanel.setLayout(layout);
        JScrollPane gradeScroller = new JScrollPane(coursesListPanel);
        gradeScroller.setPreferredSize(new Dimension(400, 400));
        
        content.add(buttonsHeaderPanel);
        content.add(infoHeaderPanel);
        content.add(gradeScroller);
        return content;
    }

    private void generateTimetables() {
        new Thread(() -> {
            TimetableViewer viewer = new TimetableViewer(client, courses);
        }).start();
    }

    private void clearCourses() {
        courses.clear();
        updateDisplay();
    }

    @Override
    public String getTitle() {
        return "Timetable";
    }

    @Override
    public void onSelected() {

    }

    @Override
    public void update(Observable o, Object arg) {
        Course course = (Course) arg;
        scraper.sections(course);
        CourseTermCombo combo = new CourseTermCombo(course, this);
        if (courses.add(combo)) {
            updateDisplay();
        }
    }

    public void remove(CourseTermCombo combo) {
        courses.remove(combo);
    }

    public void updateDisplay() {
        coursesListPanel.removeAll();
        int total = 0;
        for(CourseTermCombo course : courses) {
            coursesListPanel.add(course.makePanel());
            total += course.getCredits();
        }
        coursesListPanel.revalidate();
        coursesListPanel.repaint();

        updateInfoHeader(courses.size(), total);
    }

    private void updateInfoHeader(int numCourses, int numCredits) {
        numCoursesLabel.setText(String.valueOf(numCourses));
        numCreditsLabel.setText(String.valueOf(numCredits));

        numCoursesLabel.revalidate();
        numCoursesLabel.repaint();
        numCreditsLabel.revalidate();
        numCreditsLabel.repaint();
    }


}
