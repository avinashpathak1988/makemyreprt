package com.luminousinfoways.makemyreport.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetworkStateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		ConnectivityManager cm =
		        (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		 
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if(activeNetwork == null)
			return;
		
		boolean isConnected = activeNetwork != null &&
		                      activeNetwork.isConnectedOrConnecting();
		
		boolean isNetworkAvailable = activeNetwork.isAvailable();
		
		if(isNetworkAvailable){
			if(isConnected){
				/*
				 * Return the type of the network, for example "WIFI" or "MOBILE".
				 * */
				String networkType = activeNetwork.getTypeName();
				Toast.makeText(context, networkType+" Connected.", Toast.LENGTH_SHORT).show();
				
				Intent intentService = new Intent(context, MMRService.class);
		        context.startService(intentService);
				
			} else{
				/*
				 * Return the type of the network, for example "WIFI" or "MOBILE".
				 * */
				String networkType = activeNetwork.getTypeName();
				/*
				 * the reason for failure, or null if not available
				 * */
				String reason = activeNetwork.getReason();
				Toast.makeText(context, networkType+" DisConnected. Reason : "+reason, Toast.LENGTH_SHORT).show();				
			}
		}		
	}

}
