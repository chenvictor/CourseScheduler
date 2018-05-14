package model;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class SubjectManager implements Iterable<Subject>{

    private static SubjectManager instance;

    private final Map<String, Subject> subjects;

    public static SubjectManager getInstance(){
        if (instance == null) {
            instance = new SubjectManager();
        }
        return instance;
    }

    private SubjectManager(){
        subjects = new LinkedHashMap<>();
    }

    public Subject getSubject(String code) {
        if (subjects.containsKey(code)) {
            return subjects.get(code);
        }
        Subject newSubject = new Subject(code);
        subjects.put(code, newSubject);
        return newSubject;
    }

    public int numSubjects() {
        return subjects.size();
    }

    @Override
    public Iterator<Subject> iterator() {
        return subjects.values().iterator();
    }

    public Subject[] toArray() {
        return (Subject[]) subjects.values().toArray();
    }

}
