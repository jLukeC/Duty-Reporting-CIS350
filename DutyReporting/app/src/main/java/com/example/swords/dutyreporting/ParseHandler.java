package com.example.swords.dutyreporting;

import android.util.Log;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
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
    public Set<String> warnings;

    public ParseHandler(String u) {
        // get parse information for that username
        username = u;
    }

    //get password associated with username
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
        query.whereEqualTo("isSupervisor", false);
        try {
            List<ParseObject> pObjs = query.find();
            for (ParseObject p : pObjs) {
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
                double hours = p.getDouble("hours");
                Date end = new Date((long) (start.getTime() + (hours * 1000 * 60 * 60)));

                double hrs = getDateDiff(start, end, TimeUnit.HOURS);
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

    /**
     * Calculates the average shift length from all shifts in the past month
     * @return
     */
    public Double getAverageShiftLength() {
        ArrayList<String> hrsData = new ArrayList<String>();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("HourEntry");
        query.setLimit(28);
        query.whereEqualTo("username", username);

        Double total = (double) 0;

        List<ParseObject> pObjs = null;

        try {
            pObjs = query.find();
            for (ParseObject obj : pObjs) {
                total += obj.getDouble("hours");
            }
        } catch (ParseException e) {
            Log.d("parsehandler", "failed, parse error");
        }

        Double average;
        if (pObjs == null || pObjs.size() == 0) {
            average = (double) 0;
        } else {
            average = total / pObjs.size();
        }

        return average;
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

                double hours = p.getDouble("hours");
                Date end = new Date((long) (start.getTime() + (hours * 1000 * 60 * 60)));

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

    /**
     * Tells the parse handler to fetch the warnings from cloud code, method is void because
     * when the info is retrieved it is passed back to the caller using an interface
     * @param disp
     * @param resident
     */
    public void getWarnings(final WarningDisplay disp, final String resident) {
        final Set<String> warnings = new TreeSet<String>();

        // get list of warnings from parse
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("username", username);
        ParseCloud.callFunctionInBackground("dutyViolations", params, new FunctionCallback<String>() {
            public void done(String result, ParseException e) {
                if (e == null) {
                    try {
                        JSONObject obj = new JSONObject(result);
                        Boolean monthViolation = obj.getBoolean("monthHourViolation");
                        JSONArray weekViolations = obj.getJSONArray("weekHourViolation");
                        JSONArray restViolations = obj.getJSONArray("restPeriodViolation");
                        JSONArray shiftViolations = obj.getJSONArray("shiftViolations");

                        if (monthViolation) {
                            warnings.add("Worked more than an average of 80 hours a week in the past month");
                        }
                        for (int i = 0; i < weekViolations.length(); i++) {
                            warnings.add("Too many days worked in a row starting at " + weekViolations.getString(i));
                        }
                        for (int i = 0; i < restViolations.length(); i++) {
                            warnings.add("Did not get 8hr break before shift at " + restViolations.getString(i));
                        }
                        for (int i = 0; i < shiftViolations.length(); i++) {
                            warnings.add("Worked more than 24+4 hrs at " + shiftViolations.getString(i));
                        }

                        disp.addWarnings(warnings, resident);

                    } catch (Exception exc) {
                        Log.d("score", "failed to parse JSON violations");
                    }
                } else {
                    Log.d("score","failed, parse Error");
                }
            }
        });

    }

    public boolean setHoursWorked(Calendar start, Calendar end) {
        //add hours to parse database for given date
        Date start_date = start.getTime();
        Date end_date = end.getTime();
        try {
            ParseObject new_time = new ParseObject("HourEntry");
            new_time.put("startTime", start_date);

            double hours = (end_date.getTime() - start_date.getTime()) / (1000 * 60 * 60);

            new_time.put("hours", end_date.getTime() - hours);
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
     * Calculates the average length between days off using parse callback
     * @param disp
     */
    public void averageLengthBetweenDayOff (final AveragesDisplay disp) {
        final Double length = null;

        // get list of warnings from parse
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("username", username);
        ParseCloud.callFunctionInBackground("averageLengthBetweenDayOff",
                params, new FunctionCallback<String>() {
            public void done(String result, ParseException e) {
                if (e == null) {
                    try {
                        disp.displayLength(Float.parseFloat(result));

                    } catch (Exception exc) {
                        Log.d("score", "failed to parse JSON violations");
                    }
                } else {
                    Log.d("score", "failed, parse Error");
                }
            }
        });
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
