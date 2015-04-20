package com.example.swords.dutyreporting;

import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

/**
 * Created by lukecarlson on 3/30/15.
 */
public class ParseHandler {
    private static String username;
    public ParseHandler(String u) {
        // get parse information for that username
        username = u;
    }

    public static Set<String> getPassword() {
        Set<String> passwords = new HashSet<>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserType");
        query.whereEqualTo("username", username);
        try {
            List<ParseObject> pObj = query.find();
            for (ParseObject p : pObj) {
                passwords.add(p.getString("password"));
            }
        } catch (Exception e) {
            Log.d("score","failed, parse Error");
        }
        return passwords;
    }

    /* returns a list of all supervisors */
    public static Set<String> getSupervisors() {
        Set<String> supervisors = new HashSet<>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserType");
        query.whereEqualTo("isSupervisor",true);
        try {
            List<ParseObject> pObjs = query.find();
            for (ParseObject p : pObjs) {
                supervisors.add(p.getString("name"));
            }
        }
        catch (ParseException e) {
            Log.d("score","failed, parse Error");
        }
        return supervisors;
    }

    /* returns a list of all residents */
    public static Set<String> getResidents() {
        Set<String> residents = new HashSet<>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserType");
        query.whereEqualTo("isSupervisor",false);
        try {
            List<ParseObject> pObjs = query.find();
            for (ParseObject p : pObjs) {
                residents.add(p.getString("name"));
                residents.add(p.getString("name"));
            }
        }
        catch (ParseException e) {
            Log.d("score","failed, parse Error");
        }
        return residents;
    }

    /*Returns time of check-in and check-out*/
    public ArrayList<String> getInAndOut() {
        // get parse data on hours per day and convert it to week
        ArrayList<String> hrsData = new ArrayList<String>();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("HourEntry");
        query.whereEqualTo("username", username);

        try {
            List<ParseObject> pObjs = query.find();
            for (ParseObject p : pObjs) {
                Date start = p.getDate("startTime");
                Date end = p.getDate("endTime");
                double hrs = getDateDiff(start,end, TimeUnit.HOURS);
                DateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
                String startString = df.format(start);
                String endString = df.format(end);
                hrsData.add("Check-In: " + startString + "\n" +
                            "Check-Out: " + endString + "\n" +
                            "Hours: " + Double.toString(hrs) +"\n");
            }
        }
        catch (ParseException e) {
            Log.d("score","failed, parse Error");
        }
        Collections.sort(hrsData);
        return hrsData;
    }

    /*Returns hours worked per week, sorted by date*/
    public ArrayList<String> getHoursWorkedPerWeek() {
        // get parse data on hours per day and convert it to week
        ArrayList<String> hrsData = new ArrayList<String>();

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
                hrsData.add(reportDate + " " + Double.toString(hrs));
            }
        }
        catch (ParseException e) {
            Log.d("score","failed, parse Error");
        }
        Collections.sort(hrsData);
        return hrsData;
    }

    public Set<String> getWarnings() {
        // get list of warnings from parse
        Set<String> warnings = new TreeSet<String>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("HourEntry");
        query.whereEqualTo("username", username);
        try {
            List<ParseObject> pObjs = query.find();
            // hours between shifts
            Date lastShiftEnd = null;
            Date startOfWeek = null;
            int hrsThisWeek= 0;
            int workDaysInARow = 0;
            for (ParseObject p : pObjs) {
                Date start = p.getDate("startTime");
                Date end = p.getDate("endTime");
                double hrsWorked = getDateDiff(start,end, TimeUnit.HOURS);
                DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                String reportDate = df.format(start);

                if (hrsWorked > 28) {
                    warnings.add("Worked more than 24+4 hrs on " + reportDate);
                }

                if (lastShiftEnd != null) {
                    double hrsOff= getDateDiff(lastShiftEnd,start, TimeUnit.HOURS);

                    if (hrsOff < 8) {
                        warnings.add("Did not get 8hr break between shifts on " + reportDate);
                    }

                    if (hrsOff < 24) {
                        workDaysInARow++;
                    }

                    if (workDaysInARow >= 7) {
                        warnings.add("Too many days worked in a row ending at " + reportDate);
                        workDaysInARow = 0;
                    }
                }

                if (startOfWeek == null) {
                    startOfWeek = start;
                }
                double howFarIntoWeek = getDateDiff(startOfWeek,end, TimeUnit.DAYS);

                if (howFarIntoWeek < 7) {
                    hrsThisWeek += hrsWorked;
                } else {
                    // reset the week
                    startOfWeek = start;
                    hrsThisWeek = 0;
                }

                if (hrsThisWeek > 80) {
                    warnings.add("Make sure you average 80hrs/week after " + reportDate);
                    hrsThisWeek = 0;
                }

                lastShiftEnd = end;
            }
        }
        catch (ParseException e) {
            Log.d("score","failed, parse Error");
        }

        return warnings;
    }

    public boolean setHoursWorked(Calendar start, Calendar end) {
        //add hours to parse database for given date
        Date start_date = start.getTime();
        Date end_date = end.getTime();
        try {
            ParseObject new_time = new ParseObject("HourEntry");
            new_time.put("startTime", start_date);
            new_time.put("endTime", end_date);
            new_time.put("username", "testuser");
            new_time.saveInBackground();
            return  true;
        }
        catch(Exception e){
            Log.d("adding hours","could not add hour entry");
            return false;
        }
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
