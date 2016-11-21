package com.wearable.alphafitness;

import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.maps.SupportMapFragment;
import com.wearable.alphafitness.database.*;


import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;
import java.util.List;




/**
 * A simple {@link Fragment} subclass.
 */
public class LandScapeRecordWorkout extends Fragment implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks

{

    MapView mMapView;
    GoogleMap mMap;
    private LocationManager locationManager;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG =LandScapeRecordWorkout.class.getSimpleName();
    //private DatabaseQuery mQuery;
    private RouteBroadCastReceiver routeReceiver;
    private double latitudeValue = 0.0;
    private double longitudeValue = 0.0;
    private long timeValue=0L;
    public ArrayList<LocationObject> startToPresentLocations;

    boolean isWorkingout=false;
    // These should be descriptive.
    public static final String KEY_LATITUDE_COLUMN ="lat";
    public static final String KEY_LONGITUDE_COLUMN ="lng";
    public static final String KEY_TIME="loc";
    public static final String KEY_ID="_id";

    private TextView MaxSpeedValue;
    private TextView AvgSpeedValue;
    private TextView distanceValue;
    private TextView durationValue;
    public CustomSharedPreference customSharedPreference;






    public LandScapeRecordWorkout(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_landscape_record_workout, container, false);
        getActivity().setTitle("LandScape WorkDeatils");
        MaxSpeedValue = (TextView)view.findViewById(R.id.maxSpd_value);
        AvgSpeedValue = (TextView)view.findViewById(R.id.avgSpd_value);
        customSharedPreference = (CustomSharedPreference)getActivity().getApplication();

        //b2 = (Button)findViewById(R.id.stop);
        //b2.setOnClickListener(mStopListener);


        mLocationRequest= createLocationRequest();

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this).addApi(LocationServices.API).build();
        }


        mLocationRequest = createLocationRequest();
        routeReceiver = new RouteBroadCastReceiver();
        if (customSharedPreference.getServiceState()==true) {
            Intent i = new Intent(getActivity(), RouteService.class);
            customSharedPreference.getApplicationContext().startService(i);
        }
        return view;

    }



    private class RouteBroadCastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String local = intent.getExtras().getString("RESULT_CODE");
            String[] result_columns=new String[] {KEY_ID,  KEY_LONGITUDE_COLUMN, KEY_LATITUDE_COLUMN, KEY_TIME};
            // String[] result_columns=null;
            String whereArgs[] = null;
            String where = null;
            String having = null;
            String order = null;
            int locationCount = 0;
            double lat=0;
            double lng=0;
            long et=0;
            long  preEt=0;
            long curEt=0;

            assert local != null;
            double prevlat=0;
            double prevlng=0;
            double currentlat=0;
            double currentlng=0;
            float[] distance = {0};
            float totalDistance=0;
            long totalDurationTime=0;
            float maxSpeed=0;
            float curSpeed=0;
            float avgSpeed=0;

            if(local.equals("LOCAL")){
                //get all data from database

                ArrayList<LocationObject> startToPresentLocations=new ArrayList<LocationObject>();
                // ArrayList<Location> startToPresentLocations=new ArrayList<Location>();
                Cursor allLocation=customSharedPreference.getApplicationContext().getContentResolver().query(DatabaseQuery.CONTENT_URI,result_columns,where,whereArgs,order);

                locationCount = allLocation.getCount();

                // Move the current record pointer to the first row of the table
                allLocation.moveToFirst();

                for(int i=0;i<locationCount;i++){

                    // Get the latitude
                    lat = allLocation.getDouble(allLocation.getColumnIndex(DataBaseHelper.FIELD_LAT));

                    // Get the longitude
                    lng = allLocation.getDouble(allLocation.getColumnIndex(DataBaseHelper.FIELD_LNG));

                    //Get the time
                    et=allLocation.getLong(allLocation.getColumnIndex(DataBaseHelper.FIELD_TIME));

                    curEt=et;
                    currentlat=lat;
                    currentlng=lng;

                    if (i==0)
                    {
                        preEt=curEt;
                        prevlat=currentlat;
                        prevlng=currentlng;
                    }

                    else
                    {
                        mLastLocation.distanceBetween(prevlat,prevlng,currentlat,currentlng,distance);
                        if (distance[0]>0)
                            curSpeed=(curEt-preEt)/distance[0]/60;
                        else
                            curSpeed=0;

                        if (curSpeed>maxSpeed)
                            maxSpeed=curSpeed;
                        totalDistance=totalDistance+distance[0];
                        totalDurationTime=totalDurationTime+curEt-preEt;
                        preEt=curEt;
                        prevlat=currentlat;
                        prevlng=currentlng;
                        Toast.makeText(customSharedPreference.getApplicationContext(), "curSpeed"+Float.toString(curSpeed)+",New Duration Time:"+ Long.toString((curEt-preEt))+"ms" , Toast.LENGTH_SHORT).show();
                    }



                    startToPresentLocations.add(new LocationObject(lat,lng,et));



                    // Traverse the pointer to the next row
                    allLocation.moveToNext();
                }

                    avgSpeed=totalDurationTime/totalDistance/60;
                    customSharedPreference.setMaxSpeed(maxSpeed);
                    customSharedPreference.setAvgSpeed(avgSpeed);
                    customSharedPreference.setTotalDistance(totalDistance);
                    customSharedPreference.setTotalDurationTime(totalDurationTime);
                    MaxSpeedValue.setText(Float.toString(maxSpeed));
                    AvgSpeedValue.setText(Float.toString(avgSpeed));
                    Toast.makeText(customSharedPreference.getApplicationContext(), "MaxSpeed:"+Float.toString(maxSpeed)+",AvgSpeed:"+ Float.toString(avgSpeed)+"ms" , Toast.LENGTH_SHORT).show();

                }
            }
        }


    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);//set the desired interval (e.g. 5s) for active location update.
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "Connection method has been called");
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override

            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                            if (mLastLocation != null) {
                                latitudeValue = mLastLocation.getLatitude();
                                longitudeValue = mLastLocation.getLongitude();
                                Log.d(TAG, "Latitude 4: " + latitudeValue + " Longitude 4: " + longitudeValue);

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
    public void onPause() {
        super.onPause();
        try {
            if (routeReceiver == null) {
                Log.i(TAG, "Do not unregister receiver as it was never registered");
            } else {
                Log.d(TAG, "Unregister receiver");
                customSharedPreference.getApplicationContext().unregisterReceiver(routeReceiver);
                routeReceiver = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (routeReceiver == null) {
                Log.i(TAG, "Do not unregister receiver as it was never registered");
            } else {
                Log.d(TAG, "Unregister receiver");
                customSharedPreference.getApplicationContext().unregisterReceiver(routeReceiver);
                routeReceiver = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        //mMapView.onSaveInstanceState(outState);
    }
    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
  //      mMapView.onLowMemory();
    }
    @Override
    public void onResume() {
        super.onResume();
//        mMapView.onResume();
        IntentFilter filter = new IntentFilter(RouteService.ACTION);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(routeReceiver, filter);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();


    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        {
            mMap = googleMap;

            // Add a marker in Sydney and move the camera
            LatLng sydney = new LatLng(-34, 151);
            mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }


    }
}
