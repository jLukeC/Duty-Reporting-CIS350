package com.example.swords.dutyreporting;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SpecificUserDataActivity extends ActionBarActivity {
    BarChart barChart;
    ArrayList<BarEntry> hours;
    ArrayList<String> dates;
    BarDataSet dataSet;
    BarData data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_user_data);
        Intent intent = getIntent();

        String username = intent.getStringExtra("USERNAME");

        ParseHandler handler = new ParseHandler(username);
        ArrayList<String> hrsWorked = handler.getHoursWorkedPerWeek();

        barChart = (BarChart) findViewById(R.id.hour_chart);
        barChart.setDragEnabled(true);
        hours = new ArrayList<BarEntry>();
        dates = new ArrayList<String>();
        addToBarChart(hrsWorked);
        dataSet = new BarDataSet(hours, "# of Hours Worked");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        data = new BarData(dates,dataSet);
        barChart.setData(data);
        barChart.setDescription("# of Hours Worked Each Day");
    }

    private void addToBarChart(ArrayList<String> worked){
        int i = 0;
        Pattern p = Pattern.compile("(\\d+(?:\\.\\d+))");

        for (String w : worked){
            String[] hoursAndDate = w.split(" ");
            Matcher m = p.matcher(hoursAndDate[1]);
            double d = 0;
            if(m.find()) {
                d = Double.parseDouble(m.group(1));
            }
            Float hour = (float) d;
            dates.add(i, hoursAndDate[0]);
            hours.add(new BarEntry(hour, i));
            i++;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_specific_user_data, menu);
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
