package com.luminousinfoways.makemyreport.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.Toast;

import com.luminousinfoways.makemyreport.R;
import com.luminousinfoways.makemyreport.activity.MainActivity;

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

				NotificationCompat.Builder mBuilder =
						new NotificationCompat.Builder(context.getApplicationContext())
								.setSmallIcon(R.drawable.ic_launcher)
								//.setSound(uri)
								.setContentTitle(context.getApplicationContext().getResources().getString(R.string.app_name))
								.setContentText("Update your latest assigned reports.");

				// Vibrate the mobile phone
				Vibrator vibrator = (Vibrator) context.getApplicationContext()
						.getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(2000);

				Intent resultIntent = new Intent(context.getApplicationContext(), MainActivity.class);
				resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

				TaskStackBuilder stackBuilder = TaskStackBuilder.create(context.getApplicationContext());
				stackBuilder.addParentStack(MainActivity.class);
				stackBuilder.addNextIntent(resultIntent);
				PendingIntent resultPendingIntent =
						stackBuilder.getPendingIntent(
								0,
								PendingIntent.FLAG_ONE_SHOT
						);
				mBuilder.setContentIntent(resultPendingIntent);
				NotificationManager mNotificationManager =
						(NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
				Notification notif = mBuilder.build();
				notif.flags |= Notification.FLAG_AUTO_CANCEL;
				mNotificationManager.notify(Constants.NOTIFY_ME_ID, notif);
				
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
