package com.luminousinfoways.makemyreport.util;

import android.app.Application;
import android.content.Intent;
import android.os.Parcel;

import com.luminousinfoways.makemyreport.pojo.CurrentLocation;

/**
 * Created by luminousinfoways on 14/10/15.
 */
public class MyApplication extends Application {

    private static MyApplication mInstance;
    public CurrentLocation currentLocationList;
    private UpdateLocationListener mUpdateLocationListener;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        // register with parse
        currentLocationList = new CurrentLocation(Parcel.obtain());
        startService(new Intent(this, GPSService.class));
    }

    public void setUpdateLocationListener(UpdateLocationListener updateLocationListener){
        mUpdateLocationListener = updateLocationListener;
    }

    public void addLocationToList(CurrentLocation currentLocation){
        currentLocationList = currentLocation;
        if(mUpdateLocationListener != null)
            mUpdateLocationListener.updateLocation(currentLocationList);
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public interface UpdateLocationListener{
        public void updateLocation(CurrentLocation currentLocationList);
    }
}