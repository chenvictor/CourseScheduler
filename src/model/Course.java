package model;

public class Course {

    private Subject subject;
    private String courseCode;

    private int credits;

    private String title;
    private String description;
    private String preReqs;

    Course(Subject subject, String courseCode) {
        this.subject = subject;
        this.courseCode = courseCode;
    }

    public void display() {
        System.out.println("Course: " + toString());
        System.out.println("Title: " + getTitle());
        System.out.println("Desc: " + getDescription());
        System.out.println("Credits: " + getCredits());
        System.out.println("Pre-reqs: "+ getPreReqs());
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

}
