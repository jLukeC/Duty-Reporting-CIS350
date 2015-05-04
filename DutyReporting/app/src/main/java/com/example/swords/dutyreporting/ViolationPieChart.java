package com.example.swords.dutyreporting;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.PercentFormatter;

import java.util.ArrayList;
import java.util.Set;


public class ViolationPieChart extends ActionBarActivity implements WarningDisplay {

    private int[] total_v_count;
    private int resident_count;
    private int total_residents;
    private PieChart pchart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_violation_pie_chart);

        total_v_count = new int[4];
        resident_count = 0;
        Set<String> residents = ParseHandler.getResidents();
        total_residents = residents.size();
        for (String s : residents) {
            ParseHandler handler = new ParseHandler(s);
            handler.getWarnings(this, s);
        }


    }

    //add warnings to a resident
    public void addWarnings (Set<String> warnings, String resident) {
        resident_count++;
        int[] v_count = new int[4];
        for (String s: warnings) {
            if (s.contains("average")) {
                v_count[0]++;
            } else if (s.contains("Too")) {
                v_count[1]++;
            } else if (s.contains("8hr")) {
                v_count[2]++;
            } else if (s.contains("24+4")) {
                v_count[3]++;
            }
        }
        for (int i = 0; i < 4; i++) {
            total_v_count[i] += v_count[i];
        }
        if (resident_count == total_residents) {
            drawPieChart();
        }
    }

    public void drawPieChart() {
        pchart = (PieChart) findViewById(R.id.pie_chart);
        pchart.setDescription("");

        ArrayList<Entry> y = new ArrayList<Entry>();
        for (int i = 0; i < 4; i++) {
            y.add(new Entry((float) total_v_count[i], i));
        }

        ArrayList<String> x = new ArrayList<String>();
        x.add("80 hrs/wk");
        x.add("Days in a row");
        x.add("No 8 hr break");
        x.add("More than 24+4");

        PieDataSet dataSet = new PieDataSet(y, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : ColorTemplate.COLORFUL_COLORS) {
            colors.add(c);
        }
        dataSet.setColors(colors);

        PieData data = new PieData(x, dataSet);
        data.setValueTextSize(0);
        pchart.setData(data);
        pchart.invalidate();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_violation_pie_chart, menu);
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
