package com.luminousinfoways.makemyreport.util;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.luminousinfoways.makemyreport.asynctask.ReportListAsync;

public class MMRService extends Service {
	
	private IBinder mBinder = new LocalBinder();
	
	public class LocalBinder extends Binder{
		public MMRService getService() {
			return MMRService.this;
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		if(!MMRSingleTone.getInstance().isRunningReportAsynncTask){
			ReportListAsync async = new ReportListAsync(getApplicationContext());
			async.execute();	
		}
	 
		return START_STICKY;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
