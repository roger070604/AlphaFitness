package com.wearable.alphafitness;

/**
 * Created by lingouyang on 10/28/16.
 */
public class LocationObject {

    private double latitudeValue = 0.0;
    private double longitudeValue = 0.0;
    private double timeValue=0;

    public LocationObject(double init_latitudeValue,double init_longitudeValue,double init_timeVale)
    {
        latitudeValue = init_latitudeValue;
        longitudeValue = init_longitudeValue;
        timeValue=init_timeVale;
    }


    public double getLatitude() {
        return latitudeValue;
    }

    public double getLongitude() {
        return longitudeValue;
    }

    public  double getTime() {return timeValue;}

    public void setLatitude(double latitudeValue) {
        this.latitudeValue = latitudeValue;
    }

    public void setLongitude(double longitudeValue) {
        this.longitudeValue = longitudeValue;
    }

    public void setTimeValue(long timeValue) {
        this.timeValue=timeValue;
    }

}
