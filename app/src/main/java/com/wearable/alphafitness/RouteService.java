package com.wearable.alphafitness;
import android.Manifest;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.wearable.alphafitness.database.DataBaseHelper;
import com.wearable.alphafitness.database.DatabaseQuery;
//import com.wearable.alphafitness.helpers.CustomSharedPreference;
//import com.wearable.alphafitness.database.DatabaseQuery;




public class RouteService extends Service implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,LocationListener
{
    public RouteService() {
    }

    private static final String TAG = RouteService.class.getSimpleName();
    public static final String ACTION ="com.wearable.alphafitness.RouteService";
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private double latitudeValue = 0.0;
    private double longitudeValue = 0.0;
    private long locationTime = 0;
    private CustomSharedPreference customSharedPreference;
    private DatabaseQuery query;
    private long startTimeInMilliSeconds = 0L;
    public long iAccelTimestamp;
    private boolean isServiceRunning = false;





    @Override
    public void onCreate() {
        super.onCreate();
        customSharedPreference = (CustomSharedPreference)getApplication();
        //customSharedPreference = new CustomSharedPreference(getApplicationContext());
        if(isRouteTrackingOn()){
            startTimeInMilliSeconds = System.currentTimeMillis();
            //startTimeInMilliSeconds = 10000;
            Log.d(TAG, "Current time " + startTimeInMilliSeconds);
            Log.d(TAG, "Service is running");
            //Toast.makeText(getBaseContext(), "Current time:"+ Double.toString(startTimeInMilliSeconds), Toast.LENGTH_SHORT).show();
        }
        //query = new DatabaseQuery(getApplicationContext());
        mLocationRequest = createLocationRequest();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }




    }






    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "Connection method has been called");
        Toast.makeText(getBaseContext(),"connection method has been called",Toast.LENGTH_SHORT).show();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                            if (mLastLocation != null) {
                                latitudeValue = mLastLocation.getLatitude();
                                longitudeValue = mLastLocation.getLongitude();
                                locationTime= mLastLocation.getTime();
                                // Creating an instance of ContentValues
                                ContentValues contentValues = new ContentValues();

                                // Setting latitude in ContentValues
                                contentValues.put(DataBaseHelper.FIELD_LAT, latitudeValue );

                                // Setting longitude in ContentValues
                                contentValues.put(DataBaseHelper.FIELD_LNG, longitudeValue);


                                // setting timestamp in ContentValues
                               contentValues.put(DataBaseHelper.FIELD_TIME, locationTime);

                                // Creating an instance of LocationInsertTask
                                LocationInsertTask insertTask = new LocationInsertTask();

                                // Storing the latitude, longitude and zoom level to SQLite database
                                insertTask.execute(contentValues);

                               // Toast.makeText(getBaseContext(), "Start Location:"+ Double.toString(latitudeValue)+"," +Double.toString(longitudeValue)+"is added to the Map", Toast.LENGTH_SHORT).show();



                                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, RouteService.this);
                            }
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Latitude " + location.getLatitude() + " Longitude " + location.getLongitude());
        Log.d(TAG, "SERVICE RUNNING " + isServiceRunning);
        if(isRouteTrackingOn() && startTimeInMilliSeconds == 0){
            startTimeInMilliSeconds = System.currentTimeMillis();
        }
        if(isRouteTrackingOn() && startTimeInMilliSeconds > 0){
            latitudeValue = location.getLatitude();
            longitudeValue = location.getLongitude();
            locationTime=location.getTime();
            Log.d(TAG, "Latitude " + latitudeValue + " Longitude " + longitudeValue);
            // insert values to local sqlite database
           /*query.addNewLocationObject(System.currentTimeMillis(), latitudeValue, longitudeValue);*/
            // Creating an instance of ContentValues
            ContentValues contentValues = new ContentValues();

            // Setting latitude in ContentValues
            contentValues.put(DataBaseHelper.FIELD_LAT, latitudeValue );

            // Setting longitude in ContentValues
            contentValues.put(DataBaseHelper.FIELD_LNG, longitudeValue);

           // setting timestamp in ContentValues
            contentValues.put(DataBaseHelper.FIELD_TIME, locationTime);
            // Creating an instance of LocationInsertTask
            LocationInsertTask insertTask = new LocationInsertTask();

            // Storing the latitude, longitude and zoom level to SQLite database
            insertTask.execute(contentValues);

            //Toast.makeText(getBaseContext(), "New Location:"+ Double.toString(latitudeValue)+"," +Double.toString(longitudeValue)+"is added to the Map", Toast.LENGTH_SHORT).show();


            // send local broadcast receiver to application components
            Intent localBroadcastIntent = new Intent(ACTION);
            localBroadcastIntent.putExtra("RESULT_CODE", "LOCAL");
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(localBroadcastIntent);
            long timeoutTracking = 10 * 60 * 60 * 1000;
            long newTimeInMilliSecond=System.currentTimeMillis();
            if(newTimeInMilliSecond >= startTimeInMilliSeconds + timeoutTracking){
                //turn of the tracking
                customSharedPreference.setServiceState(false);
                Toast.makeText(this, "woop, SERVICE HAS BEEN STOPPED", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "SERVICE HAS BEEN STOPPED");
                this.stopSelf();
            }
        }
        if(!isRouteTrackingOn()){
            Log.d(TAG, "SERVICE HAS BEEN STOPPED 1");
            isServiceRunning = false;
            Log.d(TAG, "SERVICE STOPPED " + isServiceRunning);
            Intent dialogIntent = new Intent(this, RecordWorkoutActivity.class);
            //Intent dialogIntent = new Intent(this, PortraitRecordWorkout.class);
           // Intent dialogIntent = new Intent(this, LandScapeRecordWorkout.class);
            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(dialogIntent);
            this.stopSelf();
        }
    }

    private class LocationInsertTask extends AsyncTask<ContentValues, Void, Void>{
        @Override
        protected Void doInBackground(ContentValues... contentValues) {

            /** Setting up values to insert the clicked location into SQLite database */
            getContentResolver().insert(DatabaseQuery.CONTENT_URI, contentValues[0]);
            return null;
        }
    }

    private class LocationDeleteTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {

            /** Deleting all the locations stored in SQLite database */
            getContentResolver().delete(DatabaseQuery.CONTENT_URI, null, null);
            return null;
        }
    }


    private boolean isRouteTrackingOn(){
        Log.d(TAG, "SERVICE STATE " + customSharedPreference.getServiceState());
        //Log.d(TAG, "SERVICE STATE " + true);
        return customSharedPreference.getServiceState();
    }

    @Override
    public void onDestroy() {
        mGoogleApiClient.disconnect();
        this.stopSelf();
        super.onDestroy();
        // Tell the user we stopped.
        Toast.makeText(this, "woop, local service is stopped", Toast.LENGTH_SHORT).show();
    }


}
