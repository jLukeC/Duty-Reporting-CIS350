package com.example.swords.dutyreporting;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Set;


public class InAndOutActivity extends ActionBarActivity {

    //displays a resident's check-in, check-out, and hours as well as violations
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        Intent intent = getIntent();

        String resident = intent.getStringExtra("resident");
        TextView header = (TextView)findViewById(R.id.user_welcome);
        header.setText("Statistics for " + resident);

        ParseHandler handler = new ParseHandler(resident);
        ArrayList<String> hrsWorked = handler.getInAndOut();
        Set<String> warnings = handler.getWarnings();

        TextView hrsTextView = (TextView)findViewById(R.id.hours_worked);
        for (String s : hrsWorked) {
            hrsTextView.append(s + '\n');
        }

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
