package com.example.swords.dutyreporting;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class ChooseViolationStyle extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_violation_style);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_choose_violation_style, menu);
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

    public void onListClicked(View view){
        Intent intent = new Intent(this, AllViolationsActivity.class);
        startActivity(intent);
    }

    public void onFrequencyClicked(View view){
        Intent intent = new Intent(this, ViolationFrequencyChart.class);
        startActivity(intent);
    }

    public void onPieClicked(View view){
        Intent intent = new Intent(this, ViolationPieChart.class);
        startActivity(intent);
    }
}
