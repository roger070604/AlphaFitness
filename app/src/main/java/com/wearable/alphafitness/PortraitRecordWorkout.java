package com.wearable.alphafitness;

import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.images.ImageManager;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.CameraPosition;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class PortraitRecordWorkout extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks

{
    //private static final String TAG = por.class.getSimpleName();
    MapView mMapView;
    GoogleMap mMap;
    private LocationManager locationManager;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG =PortraitRecordWorkout.class.getSimpleName();
    //private DatabaseQuery mQuery;
    private RouteBroadCastReceiver routeReceiver;
    private double latitudeValue = 0.0;
    private double longitudeValue = 0.0;
    private long timeValue=0L;
    public ArrayList<LocationObject> startToPresentLocations;
    private Button b1;
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
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;


// TODO: Create public field for each column in your table.

    public PortraitRecordWorkout(){

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_portrait_record_workout, container, false);
        getActivity().setTitle("Record Workout Map View");

        b1 = (Button)view.findViewById(R.id.start_tracking);
        b1.setOnClickListener(mStartListener);
        distanceValue = (TextView)view.findViewById(R.id.distance_value);
        durationValue = (TextView)view.findViewById(R.id.duration_value);
        //MaxSpeedValue = (TextView)view.findViewById(R.id.maxSpd_value);
       // AvgSpeedValue = (TextView)view.findViewById(R.id.avgSpd_value);
        customSharedPreference = (CustomSharedPreference)getActivity().getApplication();


        ImageButton buttonProfileImage = (ImageButton) view.findViewById(R.id.Profile_Button);
        buttonProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getContext(), Profile.class);
                startActivity(intent);}});

        mMapView = (MapView) view.findViewById(R.id.map);
        mMapView .onCreate(savedInstanceState);
        mMapView .onResume();
        mMapView .getMapAsync(this);//when you already implement OnMapReadyCallback in your fragment
        mLocationRequest= createLocationRequest();
        mLocationRequest= createLocationRequest();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this).addApi(LocationServices.API).build();
        }


        mLocationRequest = createLocationRequest();
        routeReceiver = new RouteBroadCastReceiver();


        return view;


    }
    @Override


    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));





        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                markLocationOnMap(mMap,point,BitmapDescriptorFactory.HUE_RED);
                ContentValues contentValues=new ContentValues();
                contentValues.put(DataBaseHelper.FIELD_LAT,point.latitude);
                contentValues.put(DataBaseHelper.FIELD_LNG,point.longitude);
                LocationInsertTask insertTask=new LocationInsertTask();
                insertTask.execute(contentValues);
                //Toast.makeText(customSharedPreference.getApplicationContext(), "Marker is added to the Map", Toast.LENGTH_SHORT).show();

            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mMap.clear();
                LocationDeleteTask deleteTask=new LocationDeleteTask();

                deleteTask.execute();
            }
        });




    }






    @Override
public void onConnectionSuspended(int i) {

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
            double maxSpeed=0;
            double minSpeed=0;
            double curSpeed=0;
            double avgSpeed=0;
            int   avgStepCount_5=0;

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
                         curSpeed=(curEt-preEt)/(distance[0]/1000)/60000;
                        else
                         curSpeed=0;

                        if (curSpeed>maxSpeed)
                            maxSpeed=curSpeed;
                        if (curSpeed<minSpeed&&curSpeed>0)
                            minSpeed=curSpeed;
                        totalDistance=totalDistance+distance[0];
                        totalDurationTime=totalDurationTime+curEt-preEt;
                       /* customSharedPreference.addWorkoutTime((double)(curEt-preEt)/60000.0);
                        customSharedPreference.addWorkoutDistance((double)(distance[0])/1000.0);*/

                        preEt=curEt;
                        prevlat=currentlat;
                        prevlng=currentlng;
                        //Toast.makeText(customSharedPreference.getApplicationContext(), "New Distance added:"+Float.toString(distance[0])+",New Duration Time:"+ Long.toString((curEt-preEt))+"ms"+
                         //       ",CurSpeed:"+ Double.toString(curSpeed), Toast.LENGTH_SHORT).show();
                    }



                    startToPresentLocations.add(new LocationObject(lat,lng,et));



                    // Traverse the pointer to the next row
                   allLocation.moveToNext();
                }
                if (totalDistance>0) {
                    avgSpeed = ((double)(totalDurationTime) / 60000)/totalDistance;
                    if (customSharedPreference.getProfileGender()=="female")
                        avgStepCount_5= (int)((1/avgSpeed) /customSharedPreference.getfSteplength()*5);
                    else
                        avgStepCount_5= (int)((1/avgSpeed) /customSharedPreference.getmSteplength()*5);
                }
                else
                {     avgSpeed=0;
                    avgStepCount_5=0;
                }



                //maxSpeed=customSharedPreference.getMaxSpeed();
                //avgSpeed=customSharedPreference.getAvgSpeed();
               // MaxSpeedValue.setText(Float.toString(maxSpeed));
               // AvgSpeedValue.setText(Float.toString(avgSpeed));

                if(startToPresentLocations.size() > 0 && isWorkingout){
                    //prepare map drawing.
                    List<LatLng> locationPoints = getPoints(startToPresentLocations);
                    refreshMap(mMap);
                    int locSize=locationPoints.size();
                    //float[] distance = {0};
                   /* for (int j=0; j<locSize; j++)
                    {
                        markLocationOnMap(mMap, locationPoints.get(j));

                    }*/

                    markLocationOnMap(mMap,locationPoints.get(0),BitmapDescriptorFactory.HUE_RED);
                    markLocationOnMap(mMap,locationPoints.get(locSize-1),BitmapDescriptorFactory.HUE_BLUE);

                    //float durationTime=(customSharedPreference.startWorkTime-customSharedPreference.endWorkTime)/60000;
                    durationValue.setText(String.format("%.3f",totalDurationTime / 60000.0 )+"mins");
                    distanceValue.setText(String.format("%.3f",totalDistance / 1000.0 )+"km");
                    customSharedPreference.setMaxSpeed(maxSpeed);
                    customSharedPreference.setAvgSpeed(avgSpeed);
                    customSharedPreference.setMinSpeed(minSpeed);
                    double preTotalDistance=customSharedPreference.getTotalDistance();
                    double preTotalDuration=customSharedPreference.getTotalDurationTime();
                    customSharedPreference.addWorkoutDistance(totalDistance/1000.0-preTotalDistance);
                    customSharedPreference.addWorkoutTime(totalDurationTime/60000.0-preTotalDuration);
                    customSharedPreference.setTotalDistance(totalDistance/1000.0);

                    customSharedPreference.setTotalDurationTime((double)(totalDurationTime)/60000.0);

                    customSharedPreference.addWorkoutStepCountSingle(avgStepCount_5);
                    //Toast.makeText(customSharedPreference.getApplicationContext(), "Total Distance:"+Float.toString(totalDistance)+",New Duration Time:"+ Long.toString(totalDurationTime)+"ms", Toast.LENGTH_SHORT).show();

                    drawRouteOnMap(mMap, locationPoints);
                }
            }
        }
    }

    private List<LatLng> getPoints(List<LocationObject> mLocations){
        List<LatLng> points = new ArrayList<LatLng>();
        for(LocationObject mLocation : mLocations){
            points.add(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()));
        }
        return points;
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
                                refreshMap(mMap);
                                markLocationOnMap(mMap, new LatLng(latitudeValue, longitudeValue),BitmapDescriptorFactory.HUE_RED);
                                startPolyline(mMap, new LatLng(latitudeValue, longitudeValue));
                            }
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);//set the desired interval (e.g. 5s) for active location update.
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }


    private View.OnClickListener mStartListener = new View.OnClickListener() {
    public void onClick(View v) {
       if (isWorkingout==false)
       {   isWorkingout=true;
           distanceValue.setText("0 m");
           durationValue.setText("0 Minute");
           refreshMap(mMap);
           customSharedPreference.setStartWorkTime(System.currentTimeMillis());
           LocationDeleteTask deleteTask=new LocationDeleteTask();
           deleteTask.execute();
           Intent i = new Intent(getActivity(), RouteService.class);
           customSharedPreference.getApplicationContext().startService(i);
           customSharedPreference.setTotalDistance(0);
           customSharedPreference.setTotalDurationTime(0);
           customSharedPreference.resetWorkoutStepCountSingle();
           customSharedPreference.setAvgSpeed(0);
           customSharedPreference.setMaxSpeed(0);
           customSharedPreference.setMinSpeed(0);
           customSharedPreference.setServiceState(isWorkingout);
           b1.setText("Stop Workingout");
           customSharedPreference.setServiceState(isWorkingout);
           int currentWorkoutCounts=customSharedPreference.getTotalCounter();
           customSharedPreference.addTotalCounter(1);



       }

       else {
           isWorkingout = false;
           //refreshMap(mMap);
           customSharedPreference.setEndWorkTime(System.currentTimeMillis());
           Intent s = new Intent(getActivity(), RouteService.class);
           customSharedPreference.getApplicationContext().stopService(s);
           /*try {
               if (routeReceiver == null) {
                   Log.i(TAG, "Do not unregister receiver as it was never registered");
               } else {

                   Log.d(TAG, "Unregister receiver");
                   customSharedPreference.getApplicationContext().unregisterReceiver(routeReceiver);
                   routeReceiver = null;
               }
           } catch (Exception e) {
               e.printStackTrace();
           }*/


           //customSharedPreference.setTotalDistance();
           b1.setText("Start Workingout");



       }


    }
};

    private View.OnClickListener mStopListener = new View.OnClickListener() {
        public void onClick(View v) {
            // Cancel a previous call to startService().  Note that the
            // service will not actually stop at this point if there are
            // still bound clients.
            onPause();
        }
    };





    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }
    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    @Override
    public void onPause() {
        super.onPause();
       // senSensorManager.unregisterListener(this);
        mMapView.onPause();
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
          mMapView.onDestroy();
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
        super.onSaveInstanceState(outState); mMapView.onSaveInstanceState(outState);
    }
    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
       // senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        if(routeReceiver == null){
            routeReceiver = new RouteBroadCastReceiver();
        }
        IntentFilter filter = new IntentFilter(RouteService.ACTION);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(routeReceiver, filter);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }



    private class LocationInsertTask extends AsyncTask<ContentValues, Void, Void> {
        @Override
        protected Void doInBackground(ContentValues... contentValues) {

            /** Setting up values to insert the clicked location into SQLite database */
            customSharedPreference.getApplicationContext().getContentResolver().insert(DatabaseQuery.CONTENT_URI, contentValues[0]);
            return null;
        }
    }

    private class LocationDeleteTask extends AsyncTask<ContentValues, Void, Void> {
        @Override
        protected Void doInBackground(ContentValues... contentValues) {

            /** Setting up values to insert the clicked location into SQLite database */
            customSharedPreference.getApplicationContext().getContentResolver().delete(DatabaseQuery.CONTENT_URI, null,null);
            return null;
        }
    }


    private void markLocationOnMap(GoogleMap mapObject, LatLng location,Float color){
        mapObject.addMarker(new MarkerOptions().position(location).title("Current location").icon(BitmapDescriptorFactory
                .defaultMarker(color)));
        mapObject.moveCamera(CameraUpdateFactory.newLatLngZoom(location,16));

    }





    private void refreshMap(GoogleMap mapInstance){
        mapInstance.clear();
    }

    private void startPolyline(GoogleMap map, LatLng location){
        if(map == null){
            Log.d(TAG, "Map object is not null");
            return;
        }
        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        options.add(location);
        Polyline polyline = map.addPolyline(options);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(location)
                .zoom(mMap.getCameraPosition().zoom)
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
    private void drawRouteOnMap(GoogleMap map, List<LatLng> positions){
        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        options.addAll(positions);
        Polyline polyline = map.addPolyline(options);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(positions.get(0).latitude, positions.get(0).longitude))
                .zoom(mMap.getCameraPosition().zoom)
                .bearing(90)
                .tilt(40)
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }





}