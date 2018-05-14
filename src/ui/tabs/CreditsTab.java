package ui.tabs;

import SSC.Campus;
import SSC.SSCClient;
import Scrapers.CreditScraper;
import model.Course;
import model.Student;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CreditsTab implements Tab{

    private final SSCClient client;

    private boolean fetched = false;
    private boolean dontPrompt = false;

    private JLabel creditsLabel;
    private JTable gradesList;
    private DefaultTableModel model;

    private final Student student;

    public CreditsTab(SSCClient sscClient, Student student) {
        this.student = student;
        client = sscClient;
    }

    @Override
    public JComponent generateContent() {
        JComponent content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        JPanel header = new JPanel();
        JButton fetch = new JButton("Fetch");
        fetch.addActionListener(new FetchButtonListener());
        creditsLabel = new JLabel("No credit(s)");
        header.add(creditsLabel, BorderLayout.WEST);
        header.add(Box.createHorizontalStrut(50));
        header.add(fetch, BorderLayout.EAST);

        model = new UneditableTableModel();
        gradesList = new JTable(model);
        gradesList.setRowSelectionAllowed(false);
        gradesList.setColumnSelectionAllowed(false);
        model.addColumn("Course");
        model.addColumn("Credit");
        JScrollPane gradeScroller = new JScrollPane(gradesList);
        gradeScroller.setPreferredSize(new Dimension(400, 400));

        content.add(header);
        content.add(gradeScroller);
        return content;
    }

    @Override
    public String getTitle() {
        return "Credits";
    }

    @Override
    public void onSelected() {
        if (fetched) {
            return; //grades already fetched
        }
        if (dontPrompt) {
            return;
        }
        if(prompt(false)) {
            fetchGrades();
        }
    }

    private void updateCredits() {
        //remove all rows
        model.setRowCount(student.numCourses());
        int total = 0;
        int row = 0;
        for(Course course : student) {
            total += course.getCredits();
            model.setValueAt(course, row, 0);
            model.setValueAt(course.getCredits(), row++, 1);
        }
        gradesList.revalidate();
        gradesList.repaint();
        creditsLabel.setText(total + " credit(s)");
    }

    private void fetchGrades() {
        CreditScraper scraper = new CreditScraper(client);
        student.setCampus(Campus.VANCOUVER);
        student.setTransferCredits(scraper.transferCredit(student.getCampus()));
        student.setCourseCredits(scraper.courseCredit());
        fetched = true;
        updateCredits();
    }

    private boolean prompt(boolean forced){
        JPanel promptPanel = new JPanel();
        promptPanel.setLayout(new BoxLayout(promptPanel, BoxLayout.Y_AXIS));
        JCheckBox dontPromptAgain = new JCheckBox("Don't prompt me again");
        promptPanel.add(new JLabel("Fetch Grades from SSC?"));

        if (!forced) {
            promptPanel.add(dontPromptAgain);
        }
        int result = JOptionPane.showConfirmDialog(null, promptPanel, "Prompt", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE, null);
        if (result == JOptionPane.OK_OPTION) {
            return true;
        } else {
            if (!forced) {
                dontPrompt = dontPromptAgain.isSelected();
            }
            return false;
        }
    }

    private class FetchButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if(prompt(true)) {
                fetchGrades();
            }
        }
    }

    private class UneditableTableModel extends DefaultTableModel {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }

}