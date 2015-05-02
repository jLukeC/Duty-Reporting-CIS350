package com.example.swords.dutyreporting;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class ListOfResidentsActivity extends ActionBarActivity {
    String pd_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_residents);
        Intent i = getIntent();
        pd_user = i.getStringExtra("PD_USERNAME");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_of_residents, menu);
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

    public void onUserGraphButtonClicked(View view){
        Intent intent = new Intent(this, SpecificUserDataActivity.class);
        //pass username to LoggedInActivity
        intent.putExtra("USERNAME", "testuser");
        startActivity(intent);
    }

    public void onUserStatButtonClicked(View view){
        Intent intent = new Intent(this, StatisticsActivity.class);
        //pass username to LoggedInActivity
        intent.putExtra("USERNAME", "testuser");
        startActivity(intent);
    }
}
