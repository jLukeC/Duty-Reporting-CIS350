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
import java.util.List;


public class LoggedInActivity extends ActionBarActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private String username;
    private GoogleApiClient mGoogleApiClient;
    private boolean succesfulConnection;
    private static final String TAG = "LoggedInActivity";
    private ArrayList<Geofence> mGeofenceList;
    private boolean connectedToGoogleApi;
    private PendingIntent mGeofenceRequestIntent;
    private PendingIntent mGeofencePendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);
        Intent intent = getIntent();
        username = intent.getStringExtra("USERNAME");
        connectedToGoogleApi = false;

        //Checks to make sure that app can connect to google services
        if (isGooglePlayServicesAvailable()) {

            //Builds and connects
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();

            mGeofenceList = new ArrayList<Geofence>();
            setupGeofences();
        }
    }

    /**
     * This function creates and adds geofences to the list from the constants files
     */
    protected void setupGeofences() {
        Resources res = getResources();
        String[] resLocations = new String[res.getStringArray(R.array.location_array).length];
        resLocations = res.getStringArray(R.array.location_array);

        List<FenceLocation> locations = new ArrayList<FenceLocation>();

        for (int i = 0; i < resLocations.length; i++) {
            locations.add(new FenceLocation(resLocations[i]));
        }

        for (FenceLocation loc : locations) {
            mGeofenceList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(loc.getName())

                    .setCircularRegion(
                            loc.getLatitude(),
                            loc.getLongitude(),
                            1000
                    )
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());
        }
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
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
        CharSequence text = "Unable to verify location.";

        if (connectedToGoogleApi) {
            Location lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);

            if (lastKnownLocation != null) {
                float[] results = new float[1];
                Location.distanceBetween(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(),
                        39.95013175, -75.1937449, results);

                Log.v(TAG, "Distance = " + results[0]);


                if (results[0] < 1000) {
                    text = " Your location has been verified, thank you for checking in!";
                } else {
                    text = "Your location does not appear to be near the hospital...";
                }
            }
        }
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    //record check out
    public void onCheckOutButtonClick(View view){
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

                Log.v(TAG, "Distance = " + results[0]);


                if (results[0] < 1000) {
                    text = " Your location has been verified, thank you for checking out!";
                } else {
                    text = "Your location does not appear to be near the hospital...";
                }
            }
        }
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    //take to manual entry screen
    public void onManualEntryButtonClick(View view){
        Intent intent = new Intent(this, ManualEntryActivity.class);
        intent.putExtra("USERMAME",username);
        startActivity(intent);
    }

    //take to statistics view
    public void onStatButtonClick(View view){
        Intent intent = new Intent(this, StatisticsActivity.class);
        //pass username to StatisticsActivity
        intent.putExtra("USERNAME",username);
        startActivity(intent);
    }


    /**
     * This callback is executed when the app has connected to googleplayServices
     * @param connectionHint
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        connectedToGoogleApi = true;
        // Get the PendingIntent for the geofence monitoring request.
        // Send a request to add the current geofences.
        LocationServices.GeofencingApi.addGeofences(mGoogleApiClient,
                getGeofencingRequest(),
                getGeofencePendingIntent());

        Toast.makeText(this, "Starting geofence transition service", Toast.LENGTH_SHORT).show();
    }

    /**
     * Callback when google services connection suspends
     * @param cause
     */
    @Override
    public void onConnectionSuspended(int cause) {
        connectedToGoogleApi = false;
    }


    /**
     * Callback when google services connection fails
     * @param result
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
    }


    /**
     * Checks that a connection can be made to google services
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
            Log.e("DutyReporting" , "Google Play services is unavailable.");
            return false;
        }
    }


    /**
     * Called when activity is ending, disconnects from google play services
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getApplicationContext(), "received", Toast.LENGTH_SHORT);
        }
    };

//    public void onResult (Status status) {
//        Toast.makeText(this, "callback after adding geofences", Toast.LENGTH_SHORT).show();
//    }

}
