package com.example.swords.dutyreporting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Set;


public class AllViolationsActivity extends ActionBarActivity implements WarningDisplay {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_violations);
        Intent intent = getIntent();
        String username = intent.getStringExtra("PD_USERNAME");



        Set<String> residents = ParseHandler.getResidents();

        TextView residentsListView = (TextView)findViewById(R.id.residents_list);
        for (String res : residents) {
            ParseHandler handler = new ParseHandler(res);
            handler.getWarnings(this, res);
        }

    }

    public void addWarnings (Set<String> warnings, String resident) {
        TextView residentsListView = (TextView)findViewById(R.id.residents_list);
        residentsListView.append("--------");
        residentsListView.append(resident + '\n');
        for (String s: warnings) {
            residentsListView.append(s + '\n');
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_all_violations, menu);
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
