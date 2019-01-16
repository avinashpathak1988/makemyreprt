package com.luminousinfoways.makemyreport.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.luminousinfoways.makemyreport.R;
import com.luminousinfoways.makemyreport.database.MMRDatabaseHelper;
import com.luminousinfoways.makemyreport.pojo.User;
import com.luminousinfoways.makemyreport.util.MMRService;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class SplashActivity extends Activity {

	SmoothProgressBar mPocketBar;
	
	Handler mHandler = new Handler();
    Thread downloadThread;
    boolean isRunning = true;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.splash_screen);
        
        mPocketBar = (SmoothProgressBar) findViewById(R.id.google_now);
        
        mPocketBar.progressiveStop();
        mPocketBar.progressiveStart();
        
	    Handler handler = new Handler();
	    handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				mPocketBar.progressiveStop();
				
				MMRDatabaseHelper databaseHelper = new MMRDatabaseHelper(SplashActivity.this);
				User loggedInUser = databaseHelper.getLoggedInUser();
				databaseHelper.close();
				
				//int login_sts = getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 2).getInt(Constants.SP_USER_LOGIN_STS, 0);
				Intent intent = null;
				if(loggedInUser != null){
					intent = new Intent(SplashActivity.this, MainActivity.class);
				} else{
					intent = new Intent(SplashActivity.this, LoginActivity.class);
				}
				startActivity(intent);
				finish();
			}
		}, 2000);
	    
	    downloadThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning) {
                    try {
                        Thread.sleep(600000); // run at every 10 minutes
                        mHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                // Write your code here to update the UI.
                            	Intent intentService = new Intent(getApplicationContext(), MMRService.class);
                            	getApplicationContext().startService(intentService);
                            }
                        });
                    } catch (Exception e) {
                    }
                }
            }
        }); 

       downloadThread.start();
        
    }   
}
