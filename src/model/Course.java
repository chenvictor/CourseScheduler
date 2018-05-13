package model;

import java.util.*;

public class Course implements Iterable<Section>, Comparable<Course>{

    private Subject subject;
    private String courseCode;
    private List<String> terms;

    private int credits;

    private String title;
    private String description;
    private String preReqs;

    private Map<String, Section> sections;

    Course(Subject subject, String courseCode) {
        this.subject = subject;
        this.courseCode = courseCode;
        sections = new LinkedHashMap<>();
        terms = new ArrayList<>();
    }

    public void addTerm(String term) {
        if (!terms.contains(term))
            terms.add(term);
    }

    List<String> getTerms() {
        return terms;
    }

    public Section getSection(String sectionCode) {
        if (sections.containsKey(sectionCode)) {
            return sections.get(sectionCode);
        }
        Section newSection = new Section(this, sectionCode);
        sections.put(sectionCode, newSection);
        return newSection;
    }

    public Map<SectionType, List<Section>> getSectionsByType(String term) {
        Map<SectionType, List<Section>> map = new LinkedHashMap<>();
        for (Section section : this) {
            if (!section.getTerm().equals(term))
                continue;   //not right term, skip
            SectionType type = section.getType();
            List<Section> category;
            if (map.containsKey(type)) {
                category = map.get(type);
            } else {
                category = new LinkedList<>();
                map.put(type, category);
            }
            category.add(section);
        }
        return map;
    }

    public int numSections() {
        return sections.size();
    }

    public Subject getSubject() {
        return subject;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPreReqs() {
        return preReqs;
    }

    public void setPreReqs(String preReqs) {
        this.preReqs = preReqs;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public int getCredits() {
        return credits;
    }

    @Override
    public String toString() {
        return subject.getCode() + " " + String.valueOf(courseCode);
    }

    public boolean hasInfo() {
        return credits != 0 && title != null && description != null && preReqs != null;
    }

    @Override
    public Iterator<Section> iterator() {
        return sections.values().iterator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course sections = (Course) o;
        return Objects.equals(subject, sections.subject) &&
                Objects.equals(courseCode, sections.courseCode);
    }

    @Override
    public int hashCode() {

        return Objects.hash(subject, courseCode);
    }

    @Override
    public int compareTo(Course o) {
        return this.toString().compareTo(o.toString());
    }
}
