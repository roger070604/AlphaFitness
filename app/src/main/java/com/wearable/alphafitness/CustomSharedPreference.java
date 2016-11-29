package com.wearable.alphafitness;

import android.app.Application;
import android.content.Context;
import java.lang.String;

import java.util.ArrayList;

/**
 * Created by lingouyang on 11/11/16.
 */
public class CustomSharedPreference extends Application {
    public boolean serviceState=true;

    public long startWorkTime=0;

    public long endWorkTime=0;

    public double totalDurationTime=0;

    public double maxSpeed=0;

    public double avgSpeed=0;

    public double minSpeed=0;

    public int currentStepCount=0;

    public double totalDistance=0;

    public int totalStepCounter=0;

    public int totalWorkoutTimes=0;

    private double fSteplength=0.67;
    private double mSteplength=0.762;

    private double calunit=0.00025;  // 0.00025 cal per step per lbs

    public double allWorkOutTime=0;
    public double allWorkoutDistance=0;


    public String profileName="Ling";

    public String profileGender="Male";

    public double profileWeight=150;

    ArrayList<Integer> totalWorkoutStepSingle= new ArrayList<Integer>();

    ArrayList<Double> totalWorkoutDistance = new ArrayList<Double>();


    private static Context context;

    public void onCreate() {
        super.onCreate();
        CustomSharedPreference.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return CustomSharedPreference.context;
    }

    public CustomSharedPreference(){

        serviceState=true;
    }

    public void setServiceState(boolean s)
    {
        serviceState=s;
    }

    public boolean getServiceState()
    {
        return serviceState;
    }



    public double getfSteplength()
    {
        return fSteplength;
    }

    public void setfSteplength(double fst)
    {
        fSteplength=fst;
    }

    public double getmSteplength()
    {
        return mSteplength;
    }

    public void setmSteplength(double mst)
    {
        mSteplength=mst;
    }

    public int getCurrentStepCount()
    {
        return currentStepCount;
    }

    public void setCurrentStepCount(int nStepCount)
    {
        currentStepCount=nStepCount;}

    public void setProfileName(String pname)
    {
        profileName=pname;
    }
    public String getProfileName()
    {
        return profileName;
    }

    public void setProfileGender(String ge)
    {
        profileGender=ge;
    }

    public String getProfileGender()
    {
        return profileGender;
    }

    public void setProfileWeight(double fw)
    {
        profileWeight=fw;
    }

    public double getProfileWeight()
    {
        return profileWeight;
    }

    public void setStartWorkTime(long et)
    {
        startWorkTime=et;
    }

    public long getStartWorkTime()
    {
        return startWorkTime;
    }

    public void setEndWorkTime(long et)
    {
        endWorkTime=et;
    }

    public long getendWorkTime()
    {
        return endWorkTime;
    }

    public void setTotalDistance(double tD)
    {
        totalDistance=tD;
    }

    public double getTotalDistance()
    {
        return totalDistance;
    }


    public void setTotalDurationTime(double tt)
    {
        totalDurationTime=tt;
    }

    public double getTotalDurationTime()
    {
        return totalDurationTime;
    }

    public void setMinSpeed(double mins)
    {
        minSpeed=mins;
    }

    public double getMinSpeed()
    {
        return minSpeed;
    }
    public void setMaxSpeed(double ms)
    {
        maxSpeed=ms;
    }

    public double getMaxSpeed()
    {
        return maxSpeed;
    }

    public void setAvgSpeed(double as)
    {
        avgSpeed=as;
    }

    public double getAvgSpeed()
    {
        return avgSpeed;
    }


    public void addTotalCounter(int stepCount)
    {
        totalStepCounter=totalStepCounter+stepCount;
    }

    public int getTotalCounter()
    {
        return totalStepCounter;
    }

    public void setTotalWorkoutTimes(int Times)
    {
        totalWorkoutTimes=Times;
    }

    public float getTotalWorkoutTimes()
    {
        return totalWorkoutTimes;
    }

    public void addWorkoutStepCountSingle(int stepCount) {totalWorkoutStepSingle.add(stepCount);}

    public void resetWorkoutStepCountSingle() {totalWorkoutStepSingle.clear();}

    public ArrayList<Integer>  getWorkoutStepCountSingle() {return totalWorkoutStepSingle;}

    public void addWorkoutTime(double workoutTime) {allWorkOutTime=allWorkOutTime+workoutTime;}

    public void addWorkoutDistance(Double Distance) {allWorkoutDistance=allWorkoutDistance+Distance;}

    public double getAllWorkoutTime() {return allWorkOutTime;}

    public double getAllWorkoutDistance() {return allWorkoutDistance;}


}
