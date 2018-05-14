package model;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class Subject implements Iterable<Course> {

    private final String code;    //4 char code

    private String title;   //more descriptive title
    private String facultySchool;
    private final Map<String, Course> courses;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setFacultySchool(String facultySchool) {
        this.facultySchool = facultySchool;
    }

    Subject(String code) {
        this.code = code;
        courses = new LinkedHashMap<>();
    }

    public Course getCourse(String courseCode) {
        if (courses.containsKey(courseCode)) {
            return courses.get(courseCode);
        }
        Course newCourse = new Course(this, courseCode);
        courses.put(courseCode, newCourse);
        return newCourse;
    }

    public int numCourses() {
        return courses.size();
    }

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public String getFacultySchool() {
        return facultySchool;
    }

    @Override
    public String toString() {
        return code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subject subject = (Subject) o;
        return Objects.equals(code, subject.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code.charAt(0));    //hashcode based on first character
    }

    @Override
    public Iterator<Course> iterator() {
        return courses.values().iterator();
    }
}
