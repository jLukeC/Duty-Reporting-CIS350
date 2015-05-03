package com.example.swords.dutyreporting;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.components.YAxis.YAxisLabelPosition;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.filter.Approximator;
import com.github.mikephil.charting.data.filter.Approximator.ApproximatorType;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.ValueFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;


public class ViolationFrequencyChart extends ActionBarActivity implements WarningDisplay {

    private int[] v_by_month;
    private int resident_count;
    private int total_residents;
    private int month;
    private int year;
    BarChart bChart;
    ArrayList<BarEntry> y;
    ArrayList<String> x;
    BarDataSet dataSet;
    BarData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_violation_frequency_chart);

        Calendar cal = Calendar.getInstance();
        month = cal.get(cal.MONTH);
        year = cal.get(cal.YEAR);

        v_by_month = new int[12];
        resident_count = 0;
        Set<String> residents = ParseHandler.getResidents();
        total_residents = residents.size();
        for (String s : residents) {
            ParseHandler handler = new ParseHandler(s);
            handler.getWarnings(this, s);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_violation_frequency_chart, menu);
        return true;
    }

    //add warnings to a resident
    public void addWarnings (Set<String> warnings, String resident) {
        resident_count++;
        for (String s: warnings) {
            if (s.contains("average")) {
                v_by_month[month - 1]++;
            } else if (s.contains("at ")) {
                String[] temp_arr = s.split("at ");
                String s_date = temp_arr[temp_arr.length - 1];
                String s_year = s_date.substring(0, 4);
                int i_year = Integer.parseInt(s_year);
                String s_month = s_date.substring(5, 7);
                int i_month = Integer.parseInt(s_month);
                if (year == i_year) {
                    v_by_month[i_month - 1]++;
                }
            }
        }
        System.out.println("MONTH: " + month);
        System.out.println("YEAR: " + year);
        if (resident_count == total_residents) {
            drawBarChart();
        }
    }

    public void drawBarChart() {

        for (int i = 0; i < 12; i++) {
            System.out.println("Month #" + i + " = " + v_by_month[i]);
        }

        TextView text_view = (TextView)findViewById(R.id.v_text);
        text_view.append("January:   " + v_by_month[0] + "\n");
        text_view.append("February:  " + v_by_month[1] + "\n");
        text_view.append("March:     " + v_by_month[2] + "\n");
        text_view.append("April:     " + v_by_month[3] + "\n");
        text_view.append("May:       " + v_by_month[4] + "\n");
        text_view.append("June:      " + v_by_month[5] + "\n");
        text_view.append("July:      " + v_by_month[6] + "\n");
        text_view.append("August:    " + v_by_month[7] + "\n");
        text_view.append("September: " + v_by_month[8] + "\n");
        text_view.append("October:   " + v_by_month[9] + "\n");
        text_view.append("November:  " + v_by_month[10] + "\n");
        text_view.append("December:  " + v_by_month[11] + "\n");

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
