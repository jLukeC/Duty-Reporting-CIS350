package com.example.swords.dutyreporting;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;


public class LoggedInActivity extends ActionBarActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private String username;
    private GoogleApiClient mGoogleApiClient;
    private boolean succesfulConnection;
    private ArrayList<Geofence> mGeofenceList = new ArrayList<Geofence>();
    private PendingIntent mGeofencePendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);
        Intent intent = getIntent();
        username = intent.getStringExtra("USERNAME");
        succesfulConnection = false;
        buildGoogleApiClient();

        setupGeofences();
    }

    protected void setupGeofences() {
        Resources res = getResources();
        String[] resLocations = res.getStringArray(R.array.location_array);
        String radius = res.getString(R.string.radius);

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
//        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
//        return PendingIntent.getService(this, 0, intent, PendingIntent.
//                FLAG_UPDATE_CURRENT);
        return null;
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

            System.out.println("Distance = " + results[0]);


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

            System.out.println("Distance = " + results[0]);


            if (results[0] < 1000) {
                text = " Your location has been verified, thank you for checking out!";
            } else {
                text = "Your location does not appear to be near the hospital...";
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
