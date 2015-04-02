package com.example.swords.dutyreporting;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class ManualEntryActivity extends ActionBarActivity {
    private int day_selected, month_selected, year_selected, hours_worked;
    private Button save_button;
    private SeekBar hourBar;
    private TextView num_hours;
    private CalendarView calendar;
    private String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_entry);
        Intent intent = getIntent();
        username = intent.getStringExtra("USERNAME");
        save_button = (Button) findViewById(R.id.save_entry);
        hourBar = (SeekBar) findViewById(R.id.hourBar);
        num_hours = (TextView) findViewById(R.id.num_hours);
        calendar = (CalendarView) findViewById(R.id.manual_cal);
        setHourBar();
        setCalendar();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    };


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
        String datetext = month_selected + "-" + day_selected + "-" + year_selected;
        Toast.makeText(getApplicationContext(),"Your hours for "+datetext+" have been saved!", Toast.LENGTH_LONG).show();
        ParseHandler parseHandler = new ParseHandler(username);
        parseHandler.setHoursWorked(day_selected,month_selected,year_selected,hours_worked);
    }

    private void setHourBar(){
        num_hours.setText(" 0");
        hourBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekbar, int progressValue, boolean fromUser){
                hours_worked = progressValue;
                num_hours.setText(" " + hours_worked);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar){

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar){
                num_hours.setText(" " + hours_worked);
            }
        });
    }

    private void setCalendar(){

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                year_selected = year;
                month_selected = month;
                day_selected = dayOfMonth;
            }
        });
    }
}
