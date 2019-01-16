package com.luminousinfoways.makemyreport.util;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.luminousinfoways.makemyreport.activity.CameraCaptureActivity;
import com.luminousinfoways.makemyreport.activity.ReportFromDetailsActivity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GPSTracker extends Service implements LocationListener {

	private Context mContext = MMRSingleTone.getInstance().context;

	// flag for GPS status
	boolean isGPSEnabled = false;

	// flag for network status
	boolean isNetworkEnabled = false;

	// flag for GPS status
	boolean canGetLocation = false;

	Location location; // location
	double latitude; // latitude
	double longitude; // longitude

    private static final String TAG = "GPSTracker";

	// The minimum distance to change Updates in meters
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;//10; // 10 meters

	// The minimum time between updates in milliseconds
	private static final long MIN_TIME_BW_UPDATES = 0;//1000 * 60 * 1; // 1 minute

	// Declaring a Location Manager
	protected LocationManager locationManager;

    String address;
    String postalCode;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		getLocation();
		return START_STICKY;
	}

	public Location getLocation() {

		try {
			locationManager = (LocationManager) mContext
					.getSystemService(LOCATION_SERVICE);

			// getting GPS status
			isGPSEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// getting network status
			isNetworkEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnabled && !isNetworkEnabled) {
				// no network provider is enabled
				//showSettingsAlert();
				if(mContext instanceof ReportFromDetailsActivity){
					((CameraCaptureActivity)mContext).showAlert();
				}
			} else {
				this.canGetLocation = true;
				if (isNetworkEnabled) {
					locationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER,
							MIN_TIME_BW_UPDATES,
							MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
					Log.d("Network", "Network");
					if (locationManager != null) {
						location = locationManager
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (location != null) {
							latitude = location.getLatitude();
							longitude = location.getLongitude();
                            Bundle bundle = getAddressFromLocation(latitude, longitude, mContext);
                            String address = bundle.getString("address", "0.0");
                            String postalCode = bundle.getString("postal_code", "0.0");
							//((CameraCaptureActivity)mContext).availableLocation(latitude, longitude);
							if(mContext instanceof CameraCaptureActivity){
								//((CameraCaptureActivity)mContext).availableLocation(address, postalCode, latitude, longitude);
							}
							/*if(mContext instanceof ReportFromDetailsActivity){
								((ReportFromDetailsActivity)mContext).availableLocation(address, postalCode, latitude, longitude);
							}*/

						}
					}
				}
				// if GPS Enabled get lat/long using GPS Services
				if (isGPSEnabled) {
					if (location == null) {
						locationManager.requestLocationUpdates(
								LocationManager.GPS_PROVIDER,
								MIN_TIME_BW_UPDATES,
								MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
						Log.d("GPS Enabled", "GPS Enabled");
						if (locationManager != null) {
							location = locationManager
									.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							if (location != null) {
								latitude = location.getLatitude();
								longitude = location.getLongitude();
                                Bundle bundle = getAddressFromLocation(latitude, longitude, mContext);
                                String address = bundle.getString("address", "0.0");
                                String postalCode = bundle.getString("postal_code", "0.0");
//                                ((CameraCaptureActivity)mContext).availableLocation(latitude, longitude);
								if(mContext instanceof CameraCaptureActivity) {
									//((CameraCaptureActivity) mContext).availableLocation(address, postalCode, latitude, longitude);
								}
								/*if(mContext instanceof ReportFromDetailsActivity){
									((ReportFromDetailsActivity) mContext).availableLocation(address, postalCode, latitude, longitude);
								}*/
							}
						}
					}
				} else{
					showSettingsAlert();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return location;
	}

    public static Bundle getAddressFromLocation(final double latitude, final double longitude, final Context context) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String result = null;
        String postalCode = null;
        try {
            List<Address> addressList = geocoder.getFromLocation(
                    latitude, longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    sb.append(address.getAddressLine(i)).append("\n");
                }
                sb.append(address.getLocality()).append("\n");
                sb.append(address.getPostalCode()).append("\n");
                sb.append(address.getCountryName());
                result = sb.toString();
                postalCode = address.getPostalCode();
            }
        } catch (IOException e) {
            Log.e(TAG, "Unable connect to Geocoder", e);
        } finally {
            if (result != null) {
                Bundle bundle = new Bundle();
                result = "Latitude: " + latitude + " Longitude: " + longitude +
                        "\n\nAddress:\n" + result;
                bundle.putString("address", result);
                bundle.putString("postal_code", postalCode);
                return bundle;
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("address", "0.0");
                bundle.putString("postal_code", "0.0");
                return bundle;
            }
        }
    }


	/**
	 * Stop using GPS listener
	 * Calling this function will stop using GPS in your app
	 * */
	public void stopUsingGPS(){
		if(locationManager != null){
			locationManager.removeUpdates(GPSTracker.this);
		}		
	}
	
	/**
	 * Function to get latitude
	 * */
	public double getLatitude(){
		if(location != null){
			latitude = location.getLatitude();
		}
		
		// return latitude
		return latitude;
	}
	
	/**
	 * Function to get longitude
	 * */
	public double getLongitude(){
		if(location != null){
			longitude = location.getLongitude();
		}
		
		// return longitude
		return longitude;
	}
	
	/**
	 * Function to check GPS/wifi enabled
	 * @return boolean
	 * */
	public boolean canGetLocation() {
		return this.canGetLocation;
	}
	
	/**
	 * Function to show settings alert dialog
	 * On pressing Settings button will lauch Settings Options
	 * */
	public void showSettingsAlert(){

		if(mContext instanceof CameraCaptureActivity) {
			((CameraCaptureActivity) mContext).showSettingsAlert();
		}

		/*if(mContext instanceof ReportFromDetailsActivity){
			((ReportFromDetailsActivity)mContext).showAlert();
		}*/

	}

	@Override
	public void onLocationChanged(Location location) {
		latitude = location.getLatitude();
		longitude = location.getLongitude();
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
		getLocation();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}
