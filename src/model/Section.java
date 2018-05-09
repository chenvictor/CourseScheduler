package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Section {

    private Course course;
    private String sectionCode;
    private SectionType type;
    private List<TimeBlock> blocks;

    public Section(Course course, String sectionCode) {
        this.course = course;
        this.sectionCode = sectionCode;
        this.blocks = new ArrayList<>();
    }

    public void addBlock(TimeBlock block) {
        blocks.add(block);
    }

    public void setType(SectionType type) {
        this.type = type;
    }

    public Course getCourse() {
        return course;
    }

    public String getSectionCode() {
        return sectionCode;
    }

    public SectionType getType() {
        return type;
    }

    public boolean sameTerm(Section section) {
        return this.blocks.get(0).sameTerm(section.blocks.get(0));
    }

    public boolean conflicts(Section section) {
        for (TimeBlock thisBlock : this.blocks) {
            for (TimeBlock thatBlock : section.blocks) {
                if (thisBlock.conflicts(thatBlock))
                    return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(course, section.course) &&
                Objects.equals(sectionCode, section.sectionCode);
    }

    @Override
    public int hashCode() {

        return Objects.hash(course, sectionCode);
    }

    public List<TimeBlock> getBlocks() {
        return blocks;
    }

    public int getLength() {
        int total = 0;
        for (TimeBlock block : blocks) {
            total += block.getLength();
        }
        return total;
    }

}
