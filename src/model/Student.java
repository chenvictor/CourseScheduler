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

    public int numCourses() {
        return courseCredits.size() + transferCredits.size();
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

        private final Iterator<Course> transfer = transferCredits.iterator();
        private final Iterator<Course> course = courseCredits.iterator();

        @Override
        public boolean hasNext() {
            return transfer.hasNext() || course.hasNext();
        }

        @Override
        public Course next() {
            if (course.hasNext()) {
                return course.next();
            }
            return transfer.next();
        }
    }

}
