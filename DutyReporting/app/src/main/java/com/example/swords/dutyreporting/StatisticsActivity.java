package com.example.swords.dutyreporting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Set;


public class StatisticsActivity extends ActionBarActivity implements WarningDisplay {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        Intent intent = getIntent();

        String username = intent.getStringExtra("USERNAME");
        TextView header = (TextView)findViewById(R.id.user_welcome);
        header.setText("Statistics for " + username);

        ParseHandler handler = new ParseHandler(username);
        ArrayList<String> hrsWorked = handler.getHoursWorkedPerWeek();
        handler.getWarnings(this, username);

        TextView hrsTextView = (TextView)findViewById(R.id.hours_worked);
        for (String s : hrsWorked) {
            hrsTextView.append(s +  "hrs" + '\n');
        }
    }

    public void addWarnings (Set<String> warnings, String resident) {
        TextView warningTextView = (TextView)findViewById(R.id.warnings);
        for (String s : warnings) {
            warningTextView.append(s + '\n');
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_statistics, menu);
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
