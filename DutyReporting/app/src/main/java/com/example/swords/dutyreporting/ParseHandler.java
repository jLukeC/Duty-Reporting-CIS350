package com.example.swords.dutyreporting;

import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.text.DateFormat;

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

        ParseQuery<ParseObject> query = ParseQuery.getQuery("HourEntry");
        query.whereEqualTo("username", "testuser");

        try {
            List<ParseObject> pObjs = query.find();
            for (ParseObject p : pObjs) {
                Date start = p.getDate("startTime");
                Date end = p.getDate("endTime");
                double hrs = getDateDiff(start,end, TimeUnit.HOURS);
                DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                String reportDate = df.format(start);
                testData.add(reportDate + " " + Double.toString(hrs) + "hrs");
            }
        }
        catch (ParseException e) {
            Log.d("score","failed, parse Error");
        }

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

    /**
     * Get a diff between two dates
     * @param date1 the oldest date
     * @param date2 the newest date
     * @param timeUnit the unit in which you want the diff
     * @return the diff value, in the provided unit
     */
    private static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }
}
