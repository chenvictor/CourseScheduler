package SSC;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import model.Course;
import model.Section;
import model.Subject;

import javax.swing.*;
import java.io.IOException;
import java.util.List;

public class SSCWorklist {

    private final List<Section> sections;

    public SSCWorklist (List<Section> sections) {
        this.sections = sections;
    }

    /**
     * Pushes a worklist to the SSC using the provided client
     * @param client    client to use
     */
    public void push(SSCClient client) {
        if (sections.size() == 0) {
            JOptionPane.showMessageDialog(null, "No courses to add!");
            return; //nothing to push
        }
        createWorklist(client);
        for (Section section : sections) {
            registerSection(client, section);
        }
        JOptionPane.showMessageDialog(null, "Worklist added!");
    }

    private void createWorklist(SSCClient client) {
        client.requestAuthentication("Adding Worklist");
        String worklistName = JOptionPane.showInputDialog("Enter a Worklist name");
        WebClient deepClient = client.getClient();
        try {
            HtmlPage loginPage = deepClient.getPage("https://courses.students.ubc.ca/cs/main");
            loginPage.getForms().get(1).getInputByName("IMGSUBMIT").click();
            //setup the section
            client.get(client.getSetupURL());
            HtmlPage newWorklistPage = deepClient.getPage("https://courses.students.ubc.ca/cs/main?pname=wlist&tname=wlist&attrSelectedWorklist=-2");
            HtmlForm newWorklistForm = newWorklistPage.getForms().get(3);
            newWorklistForm.getInputByName("attrWorklistName").setValueAttribute(worklistName);
            newWorklistForm.getInputByName("submit").click();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void registerSection(SSCClient client, Section section) {
        client.get(getSectionURL(section));
        //url automatically saves to worklist
    }

    private static String getSectionURL (Section section) {
        String format = "https://courses.students.ubc.ca/cs/main?pname=subjarea&tname=subjareas&req=5&dept=%s&course=%s&section=%s&submit=save";
        Course course = section.getCourse();
        Subject subject = course.getSubject();
        return String.format(format, subject.getCode(), course.getCourseCode(), section.getSectionCode());
    }

}
