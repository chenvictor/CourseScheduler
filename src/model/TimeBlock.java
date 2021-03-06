package model;

import java.time.DayOfWeek;

public class TimeBlock {

    private final String term;
    private final DayOfWeek day;
    private int startHour;  //times in 24 hour format
    private int endHour;    //eg, 2:30pm = 1430
    private final boolean unScheduled;

    public TimeBlock (String term, DayOfWeek day, String start, String end) {
        this.term = term;
        this.day = day;
        if (start.length() > 0) {
            startHour = Integer.parseInt(start.replace(":", ""));
            endHour = Integer.parseInt(end.replace(":", ""));
            unScheduled = false;
        } else {
            unScheduled = true;
        }
    }

    boolean sameTerm(TimeBlock block) {
        return this.term.equals(block.term);
    }

    boolean conflicts(TimeBlock block) {
        if (unScheduled) {
            return true;
        }
        if (!this.term.equals(block.term))
            return false;
        if (!this.day.equals(block.day))
            return false;
        //check the times don't overlap
        if (this.endHour <= block.startHour)
            return false;   //this ends before block starts
        return this.startHour < block.endHour;
    }

    public DayOfWeek getDay() {
        return day;
    }

    String getTerm() {
        return this.term;
    }

    //in blocks, each is a 30 min block
    public int getLength() {
        return getEndIndex() - getStartIndex();
    }

    public int getStartIndex() {
        return index(startHour);
    }

    public int getEndIndex() {
        return index(endHour);
    }

    private int index(int hour24Format){
        hour24Format -= 800;
        int idx = 0;
        idx += (hour24Format / 100) * 2;
        idx += (hour24Format % 100) / 30;
        return idx;
    }

}
