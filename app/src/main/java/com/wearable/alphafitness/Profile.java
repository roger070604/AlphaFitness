package com.wearable.alphafitness;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.wearable.alphafitness.database.DataBaseHelper;
import com.wearable.alphafitness.database.DatabaseQuery;

import java.util.ArrayList;
import java.util.List;

public class Profile extends Activity {

    private EditText edProfileName;
    private EditText edProfileGender;
    private EditText edProfileWeight;

    private TextView WeeklyAvgDistance;
    private TextView WeeklyAvgDuration;
    private TextView WeeklyAvgTimes;
    private TextView WeeklyAvgCalories;
    private TextView AllDistance;
    private TextView AllDuration;
    private TextView AllTimes;
    private TextView AllCalories;

    private static final String TAG = Profile.class.getSimpleName();

    private RouteBroadCastReceiver routeReceiver;


    public CustomSharedPreference customSharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        /*ImageButton buttonProfileImage = (ImageButton)findViewById(R.id.Profile_Button_BackWorkout);
        buttonProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getBaseContext(), PortraitRecordWorkout.class);
                startActivity(intent);}});*/
        customSharedPreference = (CustomSharedPreference) this.getApplication();
        edProfileName = (EditText) findViewById(R.id.editProfileName);
        edProfileGender = (EditText) findViewById(R.id.editGender);
        edProfileWeight = (EditText) findViewById(R.id.editWeight);
        WeeklyAvgTimes = (TextView) findViewById(R.id.workout_times_value);
        WeeklyAvgCalories = (TextView) findViewById(R.id.workout_calories_value);
        WeeklyAvgDuration = (TextView) findViewById(R.id.workout_time_value);
        WeeklyAvgDistance = (TextView) findViewById(R.id.distance_weekly_value);
        AllTimes = (TextView) findViewById(R.id.workout_times_all_value);
        AllCalories = (TextView) findViewById(R.id.workout_calories_all_values);
        AllDuration = (TextView) findViewById(R.id.workout_time_all_value);
        AllDistance = (TextView) findViewById(R.id.distance_all_value);

        double weeklyDistance = customSharedPreference.getAllWorkoutDistance();
        double allDistance = customSharedPreference.getAllWorkoutDistance();
        double stepLength = customSharedPreference.getmSteplength();
        int weeklyTimes = customSharedPreference.getTotalCounter();
        int totalTimes = customSharedPreference.getTotalCounter();
        double weeklyDuration = customSharedPreference.getAllWorkoutTime();
        double totalDuration = customSharedPreference.getAllWorkoutTime();
        if (customSharedPreference.getProfileGender() == "female")
            stepLength = customSharedPreference.getfSteplength();

        else
            stepLength = customSharedPreference.getmSteplength();
        double weeklyCalories = weeklyDistance / stepLength * 25 / 100* customSharedPreference.getProfileWeight();
        double totalCalories = allDistance / stepLength * 25 / 100 * customSharedPreference.getProfileWeight();


        WeeklyAvgDistance.setText(String.format( "%.3f",weeklyDistance));
        AllDistance.setText(String.format( "%.3f",allDistance));
        WeeklyAvgDuration.setText(String.format( "%.3f",weeklyDuration));
        AllDuration.setText(String.format( "%.3f",totalDuration));
        WeeklyAvgTimes.setText(Integer.toString(weeklyTimes));
        AllTimes.setText(Integer.toString(totalTimes));
        WeeklyAvgCalories.setText(String.format("%.3f", weeklyCalories));


        edProfileWeight.setText(Double.toString(customSharedPreference.getProfileWeight()));
        edProfileGender.setText(customSharedPreference.getProfileGender());
        edProfileName.setText(customSharedPreference.getProfileName());


        String newProfileName = edProfileName.getText().toString();
        String newProfileGender = edProfileGender.getText().toString();
        String newProfileWeight = edProfileWeight.getText().toString();
        customSharedPreference.setProfileName(newProfileName);
        customSharedPreference.setProfileGender(newProfileGender);
        customSharedPreference.setProfileWeight(Double.parseDouble(newProfileWeight));

        routeReceiver = new RouteBroadCastReceiver();

    }

    private class RouteBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String local = intent.getExtras().getString("RESULT_CODE");

            if (local.equals("LOCAL")) {
                double weeklyDistance = customSharedPreference.getAllWorkoutDistance();
                double allDistance = customSharedPreference.getAllWorkoutDistance();
                double stepLength = customSharedPreference.getmSteplength();
                int weeklyTimes = customSharedPreference.getTotalCounter();
                int totalTimes = customSharedPreference.getTotalCounter();
                double weeklyDuration = customSharedPreference.getAllWorkoutTime();
                double totalDuration = customSharedPreference.getAllWorkoutTime();
                if (customSharedPreference.getProfileGender() == "female")
                    stepLength = customSharedPreference.getfSteplength();

                else
                    stepLength = customSharedPreference.getmSteplength();
                double weeklyCalories = weeklyDistance / stepLength * 25 / 100* customSharedPreference.getProfileWeight();
                double totalCalories = allDistance / stepLength * 25 / 100 * customSharedPreference.getProfileWeight();


                WeeklyAvgDistance.setText(String.format( "%.3f",weeklyDistance));
                AllDistance.setText(String.format( "%.3f",allDistance));
                WeeklyAvgDuration.setText(String.format( "%.3f",weeklyDuration));
                AllDuration.setText(String.format( "%.3f",totalDuration));
                WeeklyAvgTimes.setText(Integer.toString(weeklyTimes));
                AllTimes.setText(Integer.toString(totalTimes));
                WeeklyAvgCalories.setText(String.format("%.3f", weeklyCalories));
                AllCalories.setText(String.format("%.3f",totalCalories));
                String newProfileName = edProfileName.getText().toString();
                String newProfileGender = edProfileGender.getText().toString();
                String newProfileWeight = edProfileWeight.getText().toString();
                customSharedPreference.setProfileName(newProfileName);
                customSharedPreference.setProfileGender(newProfileGender);
                customSharedPreference.setProfileWeight(Double.parseDouble(newProfileWeight));

            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
//        mMapView.onResume();
        IntentFilter filter = new IntentFilter(RouteService.ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(routeReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if (routeReceiver == null) {
                Log.i(TAG, "Do not unregister receiver as it was never registered");
            } else {
                Log.d(TAG, "Unregister receiver");
                this.unregisterReceiver(routeReceiver);
                routeReceiver = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}




