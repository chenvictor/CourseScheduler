package ui.tabs;

import SSC.Campus;
import SSC.SSCClient;
import Scrapers.CreditScraper;
import model.Course;
import model.Student;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CreditsTab implements Tab{

    private SSCClient client;

    private boolean fetched = false;
    private boolean dontPrompt = false;

    private JLabel creditsLabel;
    private JPanel gradesList;

    private Student student;

    private boolean alternate = false;

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


        gradesList = new JPanel();
        gradesList.setLayout(new BoxLayout(gradesList, BoxLayout.Y_AXIS));
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
        alternate = false;
        gradesList.removeAll();
        int total = 0;
        for(Course course : student) {
            gradesList.add(createCreditPanel(course));
            total += course.getCredits();
        }
        gradesList.revalidate();
        gradesList.repaint();
        creditsLabel.setText(total + " credit(s)");
    }


    private JPanel createCreditPanel(Course course) {
        JPanel panel = new JPanel();
        panel.add(new Label(course.toString()));
        panel.add(Box.createHorizontalStrut(100));
        panel.add(new Label(course.getCredits() + ".0"));
        if(alternate) {
            panel.setBackground(Color.LIGHT_GRAY);
        }
        alternate = !alternate;
        return panel;
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

}