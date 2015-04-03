package com.example.swords.dutyreporting;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationServices;
import android.util.Log;


import com.google.android.gms.common.api.GoogleApiClient;


public class LoggedInActivity extends ActionBarActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private String username;
    private GoogleApiClient mGoogleApiClient;
    private boolean succesfulConnection;
    private static final String TAG = "LoggedInActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);
        Intent intent = getIntent();
        username = intent.getStringExtra("USERNAME");
        succesfulConnection = false;
        buildGoogleApiClient();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_logged_in, menu);
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

    //record check in
    public void onCheckInButtonClick(View view){

        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        CharSequence text = null;

        if (!succesfulConnection) {
            text = "Unable to verify location.";
        } else {
            Location lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);

            float testLat = (float) 39.950017;
            float testLong = (float) -75.196867;

            float[] results = new float[1];
            Location.distanceBetween(testLat, testLong,
                    39.95013175, -75.1937449, results);

            Log.v(TAG, "Distance = " + results[0]);


            if (results[0] < 1000) {
                text = " Your location has been verified, thank you for checking in!";
            } else {
                text = "Your location does not appear to be near the hospital...";
            }
        }
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    //record check out
    public void onCheckOutButtonClick(View view){
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        CharSequence text = null;

        if (!succesfulConnection) {
            text = "Unable to verify location.";
        } else {
            Location lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);

            float testLat = (float) 39.950017;
            float testLong = (float) -75.196867;

            float[] results = new float[1];
            Location.distanceBetween(testLat, testLong,
                    39.95013175, -75.1937449, results);

            Log.v(TAG, "Distance = " + results[0]);


            if (results[0] < 1000) {
                text = " Your location has been verified, thank you for checking out!";
            } else {
                text = "You should be checking out as you leave the hospital.";
            }
        }
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    //take to manual entry screen
    public void onManualEntryButtonClick(View view){
        Intent intent = new Intent(this, ManualEntryActivity.class);
        //pass username to LoggedInActivity
        startActivity(intent);
    }

    //take to statistics view
    public void onStatButtonClick(View view){
        Intent intent = new Intent(this, StatisticsActivity.class);
        //pass username to StatisticsActivity
        intent.putExtra("USERNAME",username);
        startActivity(intent);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        succesfulConnection = true;
    }

    @Override
    public void onConnectionSuspended(int cause) {
        succesfulConnection = false;
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

}
