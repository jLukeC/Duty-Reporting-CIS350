package com.example.swords.dutyreporting;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.net.wifi.WifiConfiguration;
import android.os.AsyncTask;
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
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LoggedInActivity extends ActionBarActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private String username;
    static private GoogleApiClient mGoogleApiClient;
    private boolean succesfulConnection;
    private static final String TAG = "LoggedInActivity";
    private boolean connectedToGoogleApi;
    private boolean isCheckedIn;
    private Handler handler;

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

        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        CharSequence text = "Unable to verify location.";
        ;

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
        }
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        isCheckedIn = true;
    }

    //record check out
    public void onCheckOutButtonClick(View view) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        CharSequence text = "Unable to verify location.";

        if (connectedToGoogleApi) {
            Location lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);

            if (lastKnownLocation != null) {

                float[] results = new float[1];
                Location.distanceBetween(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(),
                        39.95013175, -75.1937449, results);

                if (results[0] < 500) {
                    text = " Your location has been verified, thank you for checking out!";
                } else {
                    text = "Your location does not appear to be near the hospital...";
                }
            }
        }
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        isCheckedIn = false;
    }

    //take to manual entry screen
    public void onManualEntryButtonClick(View view) {
        Intent intent = new Intent(this, ManualEntryActivity.class);
        intent.putExtra("USERMAME", username);
        startActivity(intent);
    }

    //take to shifts view
    public void onShiftsButtonClick(View view) {
        Intent intent = new Intent(this, StatisticsActivity.class);
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
            } else if (results[0] > 500 && isCheckedIn) {
                text = "You have been checked out automatically!";
                isCheckedIn = false;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
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
        mGoogleApiClient.disconnect();
        handler.removeCallbacks(CheckLocation);
    }
}