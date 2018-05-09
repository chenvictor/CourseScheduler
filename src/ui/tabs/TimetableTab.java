package ui.tabs;

import model.Course;
import model.Timetable;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class TimetableTab implements Tab, Observer {

    private Set<Course> courses;

    private JPanel coursesListPanel;

    private JLabel numCoursesLabel;
    private JLabel numCreditsLabel;

    public TimetableTab() {
        courses = new LinkedHashSet<>();
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
        coursesListPanel.setLayout(new BoxLayout(coursesListPanel, BoxLayout.Y_AXIS));
        JScrollPane gradeScroller = new JScrollPane(coursesListPanel);
        gradeScroller.setPreferredSize(new Dimension(400, 400));
        
        content.add(buttonsHeaderPanel);
        content.add(infoHeaderPanel);
        content.add(new JScrollPane(coursesListPanel));
        return content;
    }

    private void generateTimetables() {
        new Thread(() -> displayTimetables(createTimetables(courses))).start();
    }

    private void displayTimetables(List<Timetable> timetables) {
        if (timetables.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No Timetables were generated!\nSome courses may conflict.");
        } else {
            JFrame timetableFrame = new JFrame("Timetables");
            timetableFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            timetableFrame.setPreferredSize(new Dimension(400, 400));
            JTabbedPane timetableTabs = new JTabbedPane();
            int counter = 1;
            for(Timetable table : timetables) {
                timetableTabs.addTab(("Timetable " + counter++), table.getJContent());
            }
            timetableFrame.add(timetableTabs);
            timetableFrame.pack();
            timetableFrame.setVisible(true);
        }
    }

    private List<Timetable> createTimetables(Set<Course> courses) {
        List<Timetable> testList = new ArrayList<>();
        int count = 5;  //test 5 timetable tabs
        for (int i = 0; i < count; i++) {
            testList.add(new Timetable());
        }
        return testList;
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
        if(courses.add((Course) arg)) {
            updateDisplay();
        }
    }

    private boolean alternate = false;

    private void updateDisplay() {
        alternate = false;
        coursesListPanel.removeAll();
        int total = 0;
        for(Course course : courses) {
            coursesListPanel.add(createCreditPanel(course));
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


    private JPanel createCreditPanel(Course course) {
        JPanel panel = new JPanel();
        JButton removeButton = new JButton("Remove");
        removeButton.addActionListener(e -> removeCourse(course));
        panel.add(removeButton);
        panel.add(new Label(course.toString()));
        panel.add(Box.createHorizontalStrut(100));
        panel.add(new Label(course.getCredits() + ".0"));
        if(alternate) {
            panel.setBackground(Color.LIGHT_GRAY);
        }
        alternate = !alternate;
        return panel;
    }

    private void removeCourse(Course course) {
        if (courses.remove(course)) {
            updateDisplay();
        }
    }

}
