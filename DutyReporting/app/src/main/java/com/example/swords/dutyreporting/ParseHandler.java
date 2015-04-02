package com.example.swords.dutyreporting;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by lukecarlson on 3/30/15.
 */
public class ParseHandler {
    private String user;
    public ParseHandler(String u) {
        // get parse information for that username
        user = u;
    }

    public Set<String> getHoursWorkedPerWeek() {
        // get parse data on hours per day and convert it to week
        Set<String> testData = new HashSet<String>();
        testData.add("Week 1 : 120");
        testData.add("Week 2 : 90");
        testData.add("Week 3 : 100");
        testData.add("Week 4 : 70");
        return testData;
    }

    public Set<String> getWarnings() {
        // get list of warnings from parse
        Set<String> testData = new HashSet<String>();
        testData.add("Too many hours - Week 1");
        testData.add("Not enough days off - Week 3");
        return testData;
    }

    public void setHoursWorked(int day, int month, int year, int hours) {
        //add hours to parse database for given date

    }
}
