package com.wearable.alphafitness;

import android.app.Application;
import android.content.Context;

/**
 * Created by lingouyang on 11/11/16.
 */
public class CustomSharedPreference extends Application {
    public boolean serviceState=true;

    public long startWorkTime=0;

    public long endWorkTime=0;

    public long totalDurationTime=0;

    public float totalDistance=0;

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

    public void setStartWorkTime(long st)
    {
        startWorkTime=st;
    }

    public long getStartWorkTIme()
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

    public void setTotalDistance(float tD)
    {
        totalDistance=tD;
    }

    public float getTotalDistance()
    {
        return totalDistance;
    }


    public void setTotalDurationTime(long tt)
    {
        totalDistance=tt;
    }

    public long getTotalDurationTime()
    {
        return totalDurationTime;
    }
}
