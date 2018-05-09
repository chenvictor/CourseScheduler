
import ui.CourseScheduler;

public class Main {

    public static void main(String[] args){

        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
        java.util.logging.Logger.getLogger("org.apache.http").setLevel(java.util.logging.Level.OFF);

        CourseScheduler scheduler = new CourseScheduler();

//        JFrame testFrame = new JFrame();
//        testFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
//
//        TimetableVisual testVisual = new TimetableVisual();
//        SubjectManager manager = SubjectManager.getInstance();
//        List<Section> sections = new ArrayList<>();
//
//        Course course = manager.getSubject("MATH").getCourse("200");
//        Section s1 = new Section(course, "001");
//        Section s2 = new Section(course, "002");
//
//        TimeBlock b1 = new TimeBlock("1", DayOfWeek.MONDAY, "8:00", "9:00");
//        TimeBlock b2 = new TimeBlock("1", DayOfWeek.WEDNESDAY, "8:00", "9:00");
//        TimeBlock b3 = new TimeBlock("1", DayOfWeek.FRIDAY, "8:00", "9:00");
//        s1.addBlock(b1);
//        s1.addBlock(b2);
//        s1.addBlock(b3);
//
//        sections.add(s1);
//
//        testFrame.add(testVisual.generateContent(sections));
//
//        testFrame.pack();
//        testFrame.setVisible(true);

    }

}
