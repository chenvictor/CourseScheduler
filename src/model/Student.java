package model;

import SSC.Campus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Student implements Iterable<Course>{

    private List<Course> courseCredits;
    private List<Course> transferCredits;

    private Campus campus;

    public Student(){
        courseCredits = new ArrayList<>();
        transferCredits = new ArrayList<>();
        campus = Campus.VANCOUVER;      //default vancouver
    }

    public Student(Campus campus){
        courseCredits = new ArrayList<>();
        transferCredits = new ArrayList<>();
        this.campus = campus;
    }

    public List<Course> getCourseCredits() {
        return courseCredits;
    }

    public void setCourseCredits(List<Course> courseCredits) {
        this.courseCredits = courseCredits;
    }

    public List<Course> getTransferCredits() {
        return transferCredits;
    }

    public void setTransferCredits(List<Course> transferCredits) {
        this.transferCredits = transferCredits;
    }

    public void printCredits(){
        int total = 0;

        System.out.println("Credits: ");
        System.out.println();
        System.out.println("Transfer Credits:");
        for (Course c : getTransferCredits()) {
            System.out.println(c + " - " + c.getCredits());
            total += c.getCredits();
        }
        System.out.println();
        System.out.println("Course Credits:");
        for (Course c : getCourseCredits()) {
            System.out.println(c + " - " + c.getCredits());
            total += c.getCredits();
        }

        System.out.println("Total Credits: " + total);
    }

    public Campus getCampus() {
        return campus;
    }

    public void setCampus(Campus campus) {
        this.campus = campus;
    }

    @Override
    public Iterator<Course> iterator() {
        return new CourseIterator();
    }

    private class CourseIterator implements Iterator<Course> {

        private Iterator<Course> transfer = transferCredits.iterator();
        private Iterator<Course> course = courseCredits.iterator();

        @Override
        public boolean hasNext() {
            return transfer.hasNext() || course.hasNext();
        }

        @Override
        public Course next() {
            if (transfer.hasNext()) {
                return transfer.next();
            }
            return course.next();
        }
    }

}
