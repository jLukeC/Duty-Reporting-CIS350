package com.example.swords.dutyreporting;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class UserSelectActivity extends ActionBarActivity {

    private Spinner spinner;

    //creates the activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_select);

        Spinner spinner = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinnerAdapter.add("Select a Resident");
        for (String resident : ParseHandler.getResidents()) {
            spinnerAdapter.add(resident);
        }
        spinnerAdapter.notifyDataSetChanged();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                if (parent.getItemAtPosition(pos).toString().equals("Select a Resident")) {
                    return;
                } else {
                    String res = parent.getItemAtPosition(pos).toString();
                    Intent intent = new Intent(UserSelectActivity.this, InAndOutActivity.class);
                    intent.putExtra("resident", res);
                    startActivity(intent);
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }

        });


        // Graph Spinner

        Spinner spinnerGraph = (Spinner)findViewById(R.id.spinner_graph);
        ArrayAdapter<String> spinnerGraphAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        spinnerGraphAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGraph.setAdapter(spinnerAdapter);
        spinnerGraphAdapter.add("Select a Resident --");
        for (String resident : ParseHandler.getResidents()) {
            spinnerGraphAdapter.add(resident);
        }
        spinnerGraphAdapter.notifyDataSetChanged();

        spinnerGraph.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                if (parent.getItemAtPosition(pos).toString().equals("Select a Resident")) {
                    return;
                } else {
                    String res = parent.getItemAtPosition(pos).toString();
                    Intent intent = new Intent(UserSelectActivity.this, SpecificUserDataActivity.class);
                    intent.putExtra("USERNAME", res);
                    startActivity(intent);
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }

        });



    }

    //creates menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_select, menu);
        return true;
    }

    //creates option responses
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
