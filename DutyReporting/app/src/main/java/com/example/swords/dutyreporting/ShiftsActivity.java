package com.example.swords.dutyreporting;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ShiftsActivity extends ActionBarActivity {

    private String username;
    private ListView shiftList;
    private ArrayList<RowData> shiftsWorked;
    private ParseHandler handler;
    private ShiftArrayAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shifts);
        Intent intent = getIntent();

        username = intent.getStringExtra("USERNAME");
        shiftList = (ListView) findViewById(R.id.list_of_shifts);
        shiftsWorked = new ArrayList<RowData>();

        //create row entries from parse database
        handler = new ParseHandler(username);
        ArrayList<String> hrsWorked = handler.getHoursWorkedPerWeek();
        for (String s: hrsWorked){
            String text = s + " hour shift";
            shiftsWorked.add(new RowData(text));
        }
        adapter = new ShiftArrayAdapter(this,shiftsWorked);
        shiftList.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shifts, menu);
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

    //take to manual entry screen
    public void onAddShiftButtonClick(View view) {
        Intent intent = new Intent(this, ManualEntryActivity.class);
        intent.putExtra("USERNAME", username);
        startActivity(intent);
    }

    //remove selected shifts
    public void onDeleteShiftButtonClick(View view) {
        ArrayList<RowData> toDelete = new ArrayList<RowData>();
        Pattern p = Pattern.compile("(\\d+(?:\\.\\d+))");
        for (RowData shift : shiftsWorked){
            if (shift.isSelected()){
                String[] hoursAndDate = shift.getText().split(" ");
                Matcher m = p.matcher(hoursAndDate[1]);
                double d = 0;
                if(m.find()) {
                    d = Double.parseDouble(m.group(1));
                }
                boolean success = handler.deleteShift(hoursAndDate[0],d);
                if(success){toDelete.add(shift);}
                else{
                    Toast.makeText(getApplicationContext(), "Could not delete an entry", Toast.LENGTH_SHORT).show();
                }
            }
        }
        for (RowData shift : toDelete){
            shiftsWorked.remove(shift);
        }
        if (toDelete.size() >0){
            Toast.makeText(getApplicationContext(), "Successfully Deleted Entries", Toast.LENGTH_SHORT).show();
        }
        adapter.notifyDataSetChanged();
    }

    //private ArrayAdapter to display hours worked
    private class ShiftArrayAdapter extends ArrayAdapter<RowData>{
        private final Context context;
        private ArrayList<RowData> values;
        public ShiftArrayAdapter(Context context, ArrayList<RowData> vals){
            super(context,R.layout.shift_list_item_layout,vals);
            this.context = context;
            this.values = vals;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int location = position;
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.shift_list_item_layout, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.shift_text);
            textView.setText((values.get(values.size() - location - 1).getText()));
            CheckBox checkBox = (CheckBox) rowView.findViewById(R.id.delete_box);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {
                    RowData element = (RowData) values.get(values.size() - location - 1);
                    element.setSelected(buttonView.isChecked());
                }
            });
            return rowView;
        }
    }

    //private class to store info for each row
    private class RowData{
        private String shiftText;
        private boolean selected;

        public RowData(String text){
            this.shiftText = text;
            this.selected = false;
        }
        public String getText(){
            return  shiftText;
        }
        public void setText(String text){
            this.shiftText = text;
        }
        public boolean isSelected(){
            return  selected;
        }
        public void setSelected(boolean selected){
            this.selected = selected;
        }
        @Override
        public boolean equals(Object other) {
            if (other == null){return false;}
            RowData h = (RowData) other;
            return this.getText().equalsIgnoreCase(h.getText());
        }
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + shiftText.length();
            return result;
        }
    }
}
