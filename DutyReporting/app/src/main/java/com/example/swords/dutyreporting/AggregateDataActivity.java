package com.example.swords.dutyreporting;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.github.mikephil.charting.buffer.BarBuffer;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.renderer.BarChartRenderer;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class AggregateDataActivity extends ActionBarActivity {
    private BarChart barChart;
    private ArrayList<BarEntry> hours;
    private ArrayList<String> users;
    private BarDataSet dataSet;
    private BarData data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aggregate_data);
        Intent intent = getIntent();

        String username = intent.getStringExtra("PD_USERNAME");
        ParseHandler handler = new ParseHandler(username);
        Set<String> residents = handler.getResidents();

        hours = new ArrayList<BarEntry>();
        users = new ArrayList<String>();

        addBarChartData(residents);

        barChart = (BarChart) findViewById(R.id.bar_chart);
        barChart.setDragEnabled(true);

        dataSet = new BarDataSet(hours, "Average # of Hours Worked");
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        data = new BarData(users,dataSet);
        barChart.setData(data);
        barChart.setDescription("# of Hours Worked per User");
    }

    private void addBarChartData(Set<String> residents) {
        ParseHandler handler;
        int i = 0;
        for (String name : residents) {
            users.add(i,name);
            handler = new ParseHandler(name);
            double avgHours = handler.getAverageShiftLength();
            hours.add(new BarEntry((float)avgHours,i));
            i++;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_aggregate_data, menu);
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
