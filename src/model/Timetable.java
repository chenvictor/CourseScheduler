package model;

import javax.swing.*;
import java.util.LinkedList;
import java.util.List;

public class Timetable implements Comparable{

    private final List<Section> sections;

    public Timetable() {
        sections = new LinkedList<>();
    }

    public Timetable(Timetable source) {
        sections = new LinkedList<>(source.sections);
    }

    public JComponent getJContent() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        return content;
    }

    public List<Section> getSections() {
        return sections;
    }

    /**
     * Tests adding a section
     * @param section   section to add
     * @return          true if section can be added without time conflict, false otherwise
     */
    public boolean add(Section section) {
        for (Section sec : sections) {
            if (sec.getCourse().equals(section.getCourse())) {
                //if same course, check if same term
                if (!sec.getBlocks().get(0).getTerm().equals(section.getBlocks().get(0).getTerm()))
                    return false;   //failed, sections not in same term
            }
            if (section.conflicts(sec))
                return false;   //failed, course conflicts
        }
        sections.add(section);
        return true;
    }

    @Override
    public int compareTo(Object o) {
        return ((Timetable) o).goodness() - this.goodness();
    }

    //higher number is better
    private int goodness() {
        int level = 0;
        //check hours balance per term
        int term1 = getHours("1");
        int term2 = getHours("2");
        int difference = Math.abs(term1 - term2);

        level -= difference;

        return level;
    }

    private int getHours(String term) {
        int total = 0;
        for (Section section : sections) {
            if (section.getBlocks().get(0).getTerm().equals(term)) {
                total += section.getLength();
            }
        }
        return total;
    }

}
