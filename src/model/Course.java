package model;

public class Course {

    private String name;
    private int credits;

    public Course(String name, int credits){
        this.name = name;
        this.credits = credits;
    }

    public String getName() {
        return name;
    }

    public int getCredits() {
        return credits;
    }
}
