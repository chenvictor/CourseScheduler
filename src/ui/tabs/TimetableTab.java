package ui.tabs;

import Scrapers.CourseScraper;
import model.Course;
import model.Section;
import model.Timetable;
import model.TimetableVisual;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Queue;

public class TimetableTab implements Tab, Observer {

    private CourseScraper scraper;
    private Set<Course> courses;
    private List<Timetable> timetables;

    private JPanel coursesListPanel;
    private TimetableVisual visual;

    private JLabel numCoursesLabel;
    private JLabel numCreditsLabel;

    public TimetableTab(CourseScraper scraper) {
        this.scraper = scraper;
        courses = new LinkedHashSet<>();
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
        coursesListPanel.setLayout(new BoxLayout(coursesListPanel, BoxLayout.Y_AXIS));
        JScrollPane gradeScroller = new JScrollPane(coursesListPanel);
        gradeScroller.setPreferredSize(new Dimension(400, 400));
        
        content.add(buttonsHeaderPanel);
        content.add(infoHeaderPanel);
        content.add(new JScrollPane(coursesListPanel));
        return content;
    }

    private void generateTimetables() {
        new Thread(() -> {
            timetables = createTimetables(courses);
            displayTimetables();
        }).start();
    }

    private void displayTimetables() {
        if (timetables.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No Timetables were generated!\nSome courses may conflict.");
        } else {
            StringBuilder courseList = new StringBuilder("Courses[" + courses.size() + "]:");
            for(Course course : courses) {
                courseList.append(" ").append(course);
            }
            JFrame timetableFrame = new JFrame("Timetables: " + courseList);
            timetableFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            JPanel wrapper = new JPanel();
            wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));

            JTabbedPane timetableTabs = new JTabbedPane();
            timetableTabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
            int counter = 1;

            //sort timetables
            Collections.sort(timetables);
            for(Timetable table : timetables) {
                timetableTabs.addTab(("Timetable " + counter++), table.getJContent());
            }

            wrapper.add(timetableTabs);
            wrapper.add(visual.generateContent(timetables.get(0).getSections()));

            timetableTabs.addChangeListener(e -> {
                JComponent newContent = visual.generateContent(timetables.get(timetableTabs.getSelectedIndex()).getSections());
                newContent.revalidate();
                newContent.repaint();
            });

            timetableFrame.add(wrapper);
            timetableFrame.pack();
            timetableFrame.setVisible(true);
        }
    }

    private List<Timetable> createTimetables(Set<Course> courses) {
        Queue<List<Section>> toTake = new PriorityQueue<>(Comparator.comparingInt(List::size));
        for (Course course : courses) {
            scraper.sections(course);
            toTake.addAll(course.getSectionsByType().values());
        }

        List<Timetable> init = new ArrayList<>();
        init.add(new Timetable());
        return _createTimetables(toTake, init);
    }

    private List<Timetable> _createTimetables(Queue<List<Section>> toTake, List<Timetable> timetables) {
        if (toTake.isEmpty()) {
            //base case
            return timetables;
        }
        //System.out.println("Total check: " + (toTake.size() + timetables.get(0).getSections().size()));
        toTake = new ArrayDeque<>(toTake);  //clone toTakeList
        List<Section> first = toTake.remove();     //reduction step
        List<Timetable> fullList = new LinkedList<>();
        for (Section section : first) {
            List<Timetable> nextTimetables = new LinkedList<>();
            for (Timetable table : timetables) {
                Timetable next = new Timetable(table);  //clone table
                if(next.add(section)) {
                    nextTimetables.add(next);
                }
            }
            if (nextTimetables.size() > 100) {
                nextTimetables = nextTimetables.subList(0, 100);    //take only 100
            }
            fullList.addAll(_createTimetables(toTake, nextTimetables));
        }
        return fullList;
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
