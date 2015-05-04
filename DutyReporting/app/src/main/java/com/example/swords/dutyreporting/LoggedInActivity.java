package com.example.swords.dutyreporting;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.Calendar;

public class LoggedInActivity extends ActionBarActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private String username;
    static private GoogleApiClient mGoogleApiClient;
    private boolean succesfulConnection;
    private static final String TAG = "LoggedInActivity";
    private boolean connectedToGoogleApi;
    private boolean isCheckedIn;
    private Handler handler;
    private Calendar checkInTime;
    private Calendar checkOutTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);
        Intent intent = getIntent();
        username = intent.getStringExtra("USERNAME");
        connectedToGoogleApi = false;
        isCheckedIn = false;

        //Checks to make sure that app can connect to google services
        if (isGooglePlayServicesAvailable()) {

            //Builds and connects
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();

            handler = new Handler();
        } else {
            Toast toast = Toast.makeText(this, "Location Services Unavailable", Toast.LENGTH_SHORT);
        }
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
    public void onCheckInButtonClick(View view) {
        if (isCheckedIn) {
            Toast toast = Toast.makeText(this.getApplicationContext(),
                    "You are already checked in.", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        CharSequence text = "You have been checked in, but unable to verify location.";


        if (connectedToGoogleApi) {
            Location lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);

            if (lastKnownLocation != null) {

                float[] results = new float[1];
                Location.distanceBetween(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(),
                        39.95013175, -75.1937449, results);

                if (results[0] < 500) {
                    text = " Your location has been verified, thank you for checking in!";
                } else {
                    text = "Your location does not appear to be near the hospital...";
                }
            }
            // Start a thread to check if the user has left the hospital
            LocationCheckThread t = new LocationCheckThread(mGoogleApiClient,
                                    getApplicationContext());
            t.start();
        }
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        isCheckedIn = true;
        checkInTime = Calendar.getInstance();
    }

    //record check out
    public void onCheckOutButtonClick(View view) {
        boolean inLocation = false;
        if (!isCheckedIn) {
            Toast toast = Toast.makeText(this.getApplicationContext(),
                    "You haven't checked in yet.", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        CharSequence text = "You have been checked out, but unable to verify location.";

        if (connectedToGoogleApi) {
            Location lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);

            if (lastKnownLocation != null) {

                float[] results = new float[1];
                Location.distanceBetween(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(),
                        39.95013175, -75.1937449, results);

                if (results[0] < 500) {
                    inLocation = true;
                    text = " Your location has been verified, thank you for checking out!";
                } else {
                    inLocation = false;
                    text = "Your location does not appear to be near the hospital...";
                }
            }
        }
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        isCheckedIn = false;

        checkOutTime = Calendar.getInstance();
        ParseHandler handler = new ParseHandler(username);
        handler.setHoursWorked(checkInTime, checkOutTime, inLocation);
    }

    //take to manual entry screen
    public void onManualEntryButtonClick(View view) {
        Intent intent = new Intent(this, ManualEntryActivity.class);
        intent.putExtra("USERNAME", username);
        startActivity(intent);
    }

    //take to shifts view
    public void onShiftsButtonClick(View view) {
        Intent intent = new Intent(this, ShiftsActivity.class);
        //pass username to ShiftsActivity
        intent.putExtra("USERNAME", username);
        startActivity(intent);
    }

    //take to graphs view
    public void onGraphsButtonClick(View view) {
        Intent intent = new Intent(this,SpecificUserDataActivity.class);
        //pass username to StatisticsActivity
        intent.putExtra("USERNAME", username);
        startActivity(intent);
    }

    //take to stats averages view
    public void onStatsButtonClick(View view) {
        Intent intent = new Intent(this, AveragesActivity.class);
        //pass username to StatisticsActivity
        intent.putExtra("USERNAME", username);
        startActivity(intent);
    }


    /**
     * This callback is executed when the app has connected to googleplayServices
     *
     * @param connectionHint
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        connectedToGoogleApi = true;
        handler.postDelayed(CheckLocation, 100);
    }

    /**
     * Callback when google services connection suspends
     *
     * @param cause
     */
    @Override
    public void onConnectionSuspended(int cause) {
        connectedToGoogleApi = false;
        handler.removeCallbacks(CheckLocation);

    }

    /**
     * Callback when google services connection fails
     *
     * @param result
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
    }

    /**
     * Checks that a connection can be made to google services
     *
     * @return
     */
    private boolean isGooglePlayServicesAvailable() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == resultCode) {
            if (Log.isLoggable("DutyReporting", Log.DEBUG)) {
                Log.d("DutyReporting", "Google Play services is available.");
            }
            return true;
        } else {
            Log.e("DutyReporting", "Google Play services is unavailable.");
            return false;
        }
    }

    // Defines a callback to check location periodically to see if the user has entered or
    // left the hopsital
    private Runnable CheckLocation = new Runnable() {
        @Override
        public void run() {
            Location lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);

            if (lastKnownLocation == null) {
                handler.postDelayed(this, 10000);
                return;
            }

            float[] results = new float[1];
            Location.distanceBetween(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(),
                    39.95013175, -75.1937449, results);

            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;
            CharSequence text = null;

            if (results[0] < 500 && !isCheckedIn) {
                text = "You have been checked in automatically!";
                isCheckedIn = true;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                checkInTime = Calendar.getInstance();
            } else if (results[0] > 500 && isCheckedIn) {
                text = "You have been checked out automatically!";
                isCheckedIn = false;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

                checkOutTime = Calendar.getInstance();
                ParseHandler handler = new ParseHandler(username);
                handler.setHoursWorked(checkInTime, checkOutTime, true);
            }

            handler.postDelayed(this, 1000 * 60 * 5);
        }
    };


    /**
     * Called when activity is ending, disconnects from google play services
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
            handler.removeCallbacks(CheckLocation);
        }
    }
}


// Thread class that checks for location
class LocationCheckThread extends Thread {
    private GoogleApiClient mGoogleApiClient;
    Context context;
    public LocationCheckThread (GoogleApiClient mGoogleApiClient, Context context) {
        this.mGoogleApiClient = mGoogleApiClient;
        this.context = context;
    }
    public void run() {
        try {
            Thread.sleep(600000);
        }
        catch (InterruptedException e) {

        }
        finally {
            synchronized (mGoogleApiClient) {
                Location lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(
                        mGoogleApiClient);

                if (lastKnownLocation != null) {

                    float[] results = new float[1];
                    Location.distanceBetween(lastKnownLocation.getLatitude(),
                            lastKnownLocation.getLongitude(),
                            39.95013175, -75.1937449, results);

                    if (results[0] < 500) {
                        Toast toast = Toast.makeText(context, "Note it seems that you still" +
                                "haven't left the hospital after checking out", Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
            }
        }
    }
}
