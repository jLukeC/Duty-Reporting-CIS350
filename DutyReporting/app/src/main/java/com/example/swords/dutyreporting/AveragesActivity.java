package com.example.swords.dutyreporting;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class AveragesActivity extends ActionBarActivity implements AveragesDisplay {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_averages);
        Intent intent = getIntent();

        String username = intent.getStringExtra("USERNAME");
        TextView header = (TextView)findViewById(R.id.user_welcome);
        header.setText("Averages for " + username + " in the past month:");
        ParseHandler handler = new ParseHandler(username);

        Double averageHrs = handler.getAverageShiftLength();

        TextView shiftLength = (TextView)findViewById(R.id.average_shift);
        shiftLength.append("Your average shift length is: " + averageHrs);

        String hrRec;
        if (averageHrs > 12) {
            hrRec = "Consider taking shorter shifts if possible!";
        } else {
            hrRec = "Your average shift lengths are within what is recommended!";
        }
        TextView shiftRec = (TextView)findViewById(R.id.hr_rec);
        shiftRec.append(hrRec);

        handler.averageLengthBetweenDayOff(this);
    }

    public void displayLength (Float length) {
        TextView dayOffLength = (TextView)findViewById(R.id.days_off);
        dayOffLength.append("Your average time between days off is: " + length);

        String offRec;
        if (length > 7) {
            offRec = "You should take more days off!";
        } else if (length < 4) {
            offRec = "You should probably work a bit more!";
        } else {
            offRec = "You are taking the right amount of days off!";
        }
        TextView shiftRec = (TextView)findViewById(R.id.dayoff_rec);
        shiftRec.append(offRec);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_averages, menu);
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
}
