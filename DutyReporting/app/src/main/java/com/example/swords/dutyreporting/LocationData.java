package com.example.swords.dutyreporting;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;


public class LocationData extends ActionBarActivity {

    BarChart barChart;
    ArrayList<BarEntry> y;
    ArrayList<String> x;
    BarDataSet dataSet;
    BarData data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_data);
        Intent intent = getIntent();

        int[] temp_arr = ParseHandler.getInLocation();

        barChart = (BarChart) findViewById(R.id.location_chart);
        barChart.setDragEnabled(true);
        y = new ArrayList<BarEntry>();
        for (int i = 0; i < 2; i++) {
           y.add(new BarEntry(temp_arr[i], i));
        }
        x = new ArrayList<String>();
        x.add("On-Location");
        x.add("Off-Location");
        dataSet = new BarDataSet(y, "");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        data = new BarData(x,dataSet);
        barChart.setData(data);
        barChart.setDescription("");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_location_data, menu);
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
