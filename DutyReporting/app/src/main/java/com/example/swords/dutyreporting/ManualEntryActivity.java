package com.example.swords.dutyreporting;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;


public class ManualEntryActivity extends ActionBarActivity {
    private Button save_button, start_time_button,end_time_button;
    private static TextView start_date, end_date;
    private CalendarView calendar;
    private String username;
    private static Calendar start, end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_entry);
        Intent intent = getIntent();
        username = intent.getStringExtra("USERNAME");
        setUpVariables();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onSaveButtonClicked(View view){
        if (!end_date.getText().toString().contains("-")){
            Toast.makeText(getApplicationContext(),"Please fill out the start time then the end time", Toast.LENGTH_SHORT).show();
        }
        else {
            ParseHandler parseHandler = new ParseHandler(username);
            boolean saved = parseHandler.setHoursWorked(start, end);
            if (saved) {
                Toast.makeText(getApplicationContext(), "Your hours were successfully saved!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Something went wrong! We could not save your hours", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static void updateStartText(){
        int month_selected = start.get(Calendar.MONTH) + 1;
        int day_selected = start.get(Calendar.DAY_OF_MONTH);
        int year_selected = start.get(Calendar.YEAR);
        String time = prettyTime(start.get(Calendar.HOUR_OF_DAY), start.get(Calendar.MINUTE));
        start_date.setText((month_selected) + "-" + day_selected + "-" + year_selected + "  " + time);
        end_date.setText("");
    }

    private static String prettyTime(int hour, int min){
        String ampm = "";
        String hourString = "";
        String minString = "";
        if (hour > 12){
            ampm = " PM";
        }
        else{
            ampm = " AM";
        }
        if (hour < 10){
            hourString = "0" + Integer.toString(hour);
        }
        else if((hour > 12) && (hour < 22)){
            hourString = "0" + Integer.toString(hour -12);
        }
        else{
            hourString = Integer.toString(hour);
        }
        if(min < 10){
            minString = "0" + Integer.toString(min);
        }
        else{
            minString = Integer.toString(min);
        }
        String pretty = hourString + ":" + minString + ampm;
        return pretty;
    }
    private static void updateEndText(){
        checkForNextDay();
        int month_selected = end.get(Calendar.MONTH) + 1;
        int day_selected = end.get(Calendar.DAY_OF_MONTH);
        int year_selected = end.get(Calendar.YEAR);
        String time = prettyTime(end.get(Calendar.HOUR_OF_DAY), end.get(Calendar.MINUTE));
        end_date.setText((month_selected) + "-" + day_selected + "-" + year_selected + "  " + time);
    }

    public void showStartTimeDiaglog(View v){
        DialogFragment newFragment = new startTimePickerFragment();
        newFragment.show(getFragmentManager(), "startPicker");
    }

    public void showEndTimeDiaglog(View v){
        DialogFragment newFragment = new endTimePickerFragment();
        newFragment.show(getFragmentManager(), "endPicker");
    }


    public static class startTimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = start.get(Calendar.HOUR_OF_DAY);
            int minute = start.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            start.set(Calendar.HOUR_OF_DAY, hourOfDay);
            start.set(Calendar.MINUTE, minute);
            updateStartText();
        }
    }


    public static class endTimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = end.get(Calendar.HOUR_OF_DAY);
            int minute = end.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            end.set(Calendar.HOUR_OF_DAY, hourOfDay);
            end.set(Calendar.MINUTE, minute);
            updateEndText();
        }
    }



    private static void checkForNextDay(){
        int hourdif = end.get(Calendar.HOUR_OF_DAY) - start.get(Calendar.HOUR_OF_DAY);
        int mindif = end.get(Calendar.MINUTE) - start.get(Calendar.MINUTE);
        if (end.get(Calendar.DATE) == start.get(Calendar.DATE)) {
            if (hourdif < 0) {
                end.add(Calendar.DATE, 1);
            } else if ((hourdif == 0) && (mindif < 0)) {
                end.add(Calendar.DATE, 1);
            }
        }
        else{
            if (hourdif > 0){
                end.set(Calendar.DATE, start.get(Calendar.DATE));
            }
            else if ((hourdif == 0) && (mindif > 0)) {
                end.set(Calendar.DATE, start.get(Calendar.DATE));
            }
        }
    }

    private void setUpVariables(){
        save_button = (Button) findViewById(R.id.save_entry);
        start_time_button = (Button) findViewById(R.id.pick_start_time);
        end_time_button = (Button) findViewById(R.id.pick_end_time);
        start_date = (TextView) findViewById(R.id.start_date_text);
        end_date = (TextView) findViewById(R.id.end_date_text);
        calendar = (CalendarView) findViewById(R.id.manual_cal);
        start = Calendar.getInstance();
        end = Calendar.getInstance();
        //set calendar listener
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                start.set(Calendar.YEAR, year);
                start.set(Calendar.MONTH, month);
                start.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                end.set(Calendar.YEAR, year);
                end.set(Calendar.MONTH, month);
                end.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                start_date.setText("");
                end_date.setText("");
            }
        });
    }

}
