package com.luminousinfoways.makemyreport.activity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.luminousinfoways.makemyreport.R;
import com.luminousinfoways.makemyreport.database.MMRDatabaseHelper;
import com.luminousinfoways.makemyreport.util.Constants;
import com.luminousinfoways.makemyreport.util.MMRSingleTone;
import com.luminousinfoways.makemyreport.util.Util;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class LoginActivity1 extends Activity implements AnimationListener {
	
	String orgId = null;
	String orgName = null;
	int counter = 0;
	SmoothProgressBar mPocketBar;
	
	Animation animation;
	RelativeLayout layout;
	boolean moveUp = false;
	boolean moveDown = false;
	LinearLayout layoutTransparent;
	
	EditText etUserID;
	EditText etPassword;
	Button btnLogIn;
	//TextView textView1;
	EditText etCorIDCor;
	Button btnSetupCor;
	
	RelativeLayout layoutLogin;
	RelativeLayout corporateLayout;
	
	TextView tvCorName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_login);
		
		layoutLogin = (RelativeLayout) findViewById(R.id.layoutNamePassword);
		corporateLayout = (RelativeLayout) findViewById(R.id.corporateLayout);
				
		etUserID = (EditText) findViewById(R.id.etUserID);
		etPassword = (EditText) findViewById(R.id.etPassword);
		btnLogIn = (Button) findViewById(R.id.btnLogIn);
		etCorIDCor = (EditText) findViewById(R.id.etCorIDCor);
		//textView1 = (TextView) findViewById(R.id.textView1);
		btnSetupCor = (Button) findViewById(R.id.btnSetupCor);
		
		tvCorName = (TextView) findViewById(R.id.tvCorName);
		
		layoutTransparent = (LinearLayout) findViewById(R.id.layoutTransparent);
		
		mPocketBar = (SmoothProgressBar) findViewById(R.id.google_now);
		mPocketBar.setVisibility(View.INVISIBLE);
	    mPocketBar.progressiveStop();
	    
	    orgId = getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 1).getString(Constants.SP_ORG_ID, null);
	    orgName = getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 1).getString(Constants.SP_ORG_NAME, null);
	    
	    
	    if(orgId == null){
	    	//CorID
	    	corporateLayout.setVisibility(View.VISIBLE);
	    	layoutLogin.setVisibility(View.INVISIBLE);
	    } else{
	    	//Login
	    	layoutLogin.setVisibility(View.VISIBLE);
	    	corporateLayout.setVisibility(View.INVISIBLE);
	    }
	    
	    if(orgName == null){
	    	tvCorName.setVisibility(View.INVISIBLE);
	    } else{
	    	tvCorName.setText(orgName);
	    	tvCorName.setVisibility(View.VISIBLE);
	    }
		
	    btnSetupCor.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String corID = etCorIDCor.getText().toString().trim();
				
				if(corID == null || corID.length() <= 0){
					Toast.makeText(LoginActivity1.this, "Enter Corporate ID", Toast.LENGTH_SHORT).show();
					return;
				}
				
				if(Util.isConnected(LoginActivity1.this)){
					CorSetUpAsyncTask asyncTask = new CorSetUpAsyncTask();
					asyncTask.execute(corID);
				} else{
					
					layout = (RelativeLayout) findViewById(R.id.layoutBg);
					animation = AnimationUtils.loadAnimation(LoginActivity1.this, R.anim.move_up);
					animation.setAnimationListener(LoginActivity1.this);
					moveUp = true;
					moveDown = false;
					layout.setDrawingCacheEnabled(true);
					layout.startAnimation(animation);
					
					customAlert("No Internet Connection.", 0);
				}
			}
		});
	    
		btnLogIn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String userid = etUserID.getText().toString().trim();
				
				if(userid == null || userid.length() <= 0){
					Toast.makeText(LoginActivity1.this, "Enter User ID", Toast.LENGTH_SHORT).show();
					return;
				}
				
				String pass = etPassword.getText().toString().trim();
				
				if(pass == null || pass.length() <= 0){
					Toast.makeText(LoginActivity1.this, "Enter password", Toast.LENGTH_SHORT).show();
					return;
				}
				
				if(Util.isConnected(LoginActivity1.this)){
					LoginAsyncTask asyncTask = new LoginAsyncTask();
					asyncTask.execute(userid, pass, orgId);
				} else{
					
					layout = (RelativeLayout) findViewById(R.id.layoutBg);
					animation = AnimationUtils.loadAnimation(LoginActivity1.this, R.anim.move_up);
					animation.setAnimationListener(LoginActivity1.this);
					moveUp = true;
					moveDown = false;
					layout.setDrawingCacheEnabled(true);
					layout.startAnimation(animation);
					
					customAlert("No Internet Connection.", 0);
				}
			}
		});
       
	}
	
	private void customAlert(String message, int status){
		TextView tvMsg = (TextView) findViewById(R.id.tvMsg);
		TextView tvOk = (TextView) findViewById(R.id.tvOk);
		ImageView img = (ImageView) findViewById(R.id.img);
		tvMsg.setText(message);		
		
		if(status == 0){
			img.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_remove));
			layout.setBackgroundColor(getResources().getColor(R.color.red));
		} else if(status == 1){
			img.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_accept));
			layout.setBackgroundColor(getResources().getColor(R.color.login_btn_color_pressed));
		}
		
		tvOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				layout = (RelativeLayout) findViewById(R.id.layoutBg);
				animation = AnimationUtils.loadAnimation(LoginActivity1.this, R.anim.move_down);
				animation.setAnimationListener(LoginActivity1.this);
				moveUp = false;
				moveDown = true;
				layout.startAnimation(animation);
			}
		});
	}
	
	
	private class LoginAsyncTask extends AsyncTask<String, Integer, Void>{
		
		String TAG = "LoginAsync";
		int status = 0;
		String message = "";
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			setDisableInputElements();
			
			mPocketBar.setVisibility(View.VISIBLE);
		    mPocketBar.progressiveStop();
			mPocketBar.progressiveStart();
		}

		@Override
		protected Void doInBackground(String... params) {
			
			String username = params[0];
			String password = params[1];
			String orgID = params[2];
			
			List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
			list.add(new BasicNameValuePair("username", username)); 
			list.add(new BasicNameValuePair("password", password));
			
			HttpParams httpParameters = new BasicHttpParams();
			// Set the timeout in milliseconds until a connection is established.
			// The default value is zero, that means the timeout is not used. 
			int timeoutConnection = 3000;
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
			// Set the default socket timeout (SO_TIMEOUT) 
			// in milliseconds which is the timeout for waiting for data.
			//int timeoutSocket = 5000;
			//HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			
			HttpResponse httpResponse = null;
			HttpClient httpClient = new DefaultHttpClient(httpParameters);
			HttpPost httpPost = new HttpPost(Constants.URL + Constants.LOGIN_URL_REQUEST+orgID);
			try{
				httpPost.setEntity(new UrlEncodedFormEntity(list));
				httpResponse = httpClient.execute(httpPost);
				InputStream inputStream = httpResponse.getEntity().getContent();				
				
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
				String line;
				String response = "";
				while((line = bufferedReader.readLine()) != null){
					publishProgress(10);
					response = response + line;
				}
				Log.i(TAG, "Response : ["+response+"]");
				
				JSONObject jsonObject = new JSONObject(response);
				JSONArray responseArray = jsonObject.getJSONArray("Response");
				for (int i = 0; i < responseArray.length(); i++) {
					
					status = responseArray.getJSONObject(i).getInt("loginStatus");
					
					if(status == 1){
						String full_name = responseArray.getJSONObject(i).getString("userFullName");
						String api_key = responseArray.getJSONObject(i).getString("apiKey");
						
						getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 2).edit().putString(Constants.SP_FULL_NAME, full_name).commit();
						getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 2).edit().putString(Constants.SP_API_KEY, api_key).commit();
						
						String org_id = getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 1).getString(Constants.SP_ORG_ID, orgId);
						String org_name = getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 1).getString(Constants.SP_ORG_NAME, orgName);
						
						MMRDatabaseHelper databaseHelper = new MMRDatabaseHelper(LoginActivity1.this);
						databaseHelper.insertUser(api_key, full_name, 1, org_id, org_name);
						databaseHelper.close();
					} else{
						message = "Incorrect Password";
					}
				}
				
			
			} catch(ConnectTimeoutException e){
				message = "Connection time out.\nPlease reset internet connection.";
			}catch(UnknownHostException exception){
				message = "Please reset internet connection.";
			} catch(Exception e){
				message = "Login failed.";
				Log.e(TAG, "Exception", e);
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			
			mPocketBar.progressiveStop();
			setEnableInputElements();
			
			if(status == 1){
				
				MMRSingleTone.getInstance().isAlreadyRunReportAsyncBefore = false;
				MMRSingleTone.getInstance().isRunningReportAsynncTask = false;
				
				Intent intent = new Intent(LoginActivity1.this, MainActivity.class);
				startActivity(intent);
				finish();
			} else{
			
				layout = (RelativeLayout) findViewById(R.id.layoutBg);
				animation = AnimationUtils.loadAnimation(LoginActivity1.this, R.anim.move_up);
				animation.setAnimationListener(LoginActivity1.this);
				moveUp = true;
				moveDown = false;
				layout.setDrawingCacheEnabled(true);
				layout.startAnimation(animation);
				
				customAlert(message, 0);
			}	
		}
	}
	
	private class CorSetUpAsyncTask extends AsyncTask<String, Integer, Void>{
		
		String TAG = "CorSetUpAsyncTask";
		int status = 0;
		String message = "";
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			setDisableInputElements();
			
			mPocketBar.setVisibility(View.VISIBLE);
		    mPocketBar.progressiveStop();
			mPocketBar.progressiveStart();
		}

		@Override
		protected Void doInBackground(String... params) {
			
			String corName = params[0];
			
			List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
			list.add(new BasicNameValuePair("corName", corName)); 
			
			HttpResponse httpResponse = null;
			
			HttpParams httpParameters = new BasicHttpParams();
			// Set the timeout in milliseconds until a connection is established.
			// The default value is zero, that means the timeout is not used. 
			int timeoutConnection = 3000;
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
			// Set the default socket timeout (SO_TIMEOUT) 
			// in milliseconds which is the timeout for waiting for data.
			//int timeoutSocket = 5000;
			//HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			
			HttpClient httpClient = new DefaultHttpClient(httpParameters);
			HttpPost httpPost = new HttpPost(Constants.URL + Constants.ORG_REQUEST_URL);
			try{
				httpPost.setEntity(new UrlEncodedFormEntity(list));
				httpResponse = httpClient.execute(httpPost);
				InputStream inputStream = httpResponse.getEntity().getContent();				
				
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
				String line;
				String response = "";
				while((line = bufferedReader.readLine()) != null){
					publishProgress(10);
					response = response + line;
				}
				Log.i(TAG, "Response : ["+response+"]");
				
				
				/*
				 * {
					    "Response": {
					        "message": "Success",
					        "url": "http://makemyreport.com/MobileApps/",
					        "cor": "DEMO",
					        "headerMsg": "DEMO",
					        "subheadMsg": "DEMO"
					    }
					}

				 * */
				
				JSONObject jsonObject = new JSONObject(response);
				JSONObject responseObj = jsonObject.getJSONObject("Response");
				message = responseObj.getString("message");
				String url = responseObj.getString("url");
				orgId = responseObj.getString("cor");
				orgName = responseObj.getString("headerMsg");
				String subheadMsg = responseObj.getString("subheadMsg");
	
				if(message.equalsIgnoreCase("success")){
					status = 1;
					getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 2).edit().putString(Constants.SP_ORG_ID, orgId).commit();
					getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 2).edit().putString(Constants.SP_ORG_NAME, orgName).commit();
					
					tvCorName.setText(orgName);
					tvCorName.setVisibility(View.VISIBLE);
				} else{
					status = 0;
					getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 2).edit().putString(Constants.SP_ORG_ID, null).commit();
					getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 2).edit().putString(Constants.SP_ORG_NAME, null).commit();
				}
			
			} catch(ConnectTimeoutException e){ 
				message = "Connection time out.\nPlease reset internet connection.";
			} catch(UnknownHostException exception){
				message = "Please reset internet connection.";
			} catch(Exception e){
				Log.e(TAG, "Exception", e);
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			mPocketBar.setVisibility(View.INVISIBLE);
			mPocketBar.progressiveStop();
			setEnableInputElements();
			
			if(status == 1){
				//Animate flip for login
				
				corporateLayout.setVisibility(View.INVISIBLE);
				layoutLogin.setVisibility(View.VISIBLE);
				
			} else{
			
				layout = (RelativeLayout) findViewById(R.id.layoutBg);
				animation = AnimationUtils.loadAnimation(LoginActivity1.this, R.anim.move_up);
				animation.setAnimationListener(LoginActivity1.this);
				moveUp = true;
				moveDown = false;
				layout.setDrawingCacheEnabled(true);
				layout.startAnimation(animation);
				
				customAlert(message, 0);
			}	
		}
	}

	@Override
	public void onAnimationStart(Animation animation) {
		animation = new TranslateAnimation(0.0f, 0.0f, 0.0f, 0.0f);
        animation.setDuration(1);
        layoutTransparent.startAnimation(animation);
		if(moveUp)
			layout.setVisibility(View.VISIBLE);
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		if(moveDown){
			moveDown = false;
			moveUp = false;
			layout.setVisibility(View.GONE);
			layout.setDrawingCacheEnabled(false);
		} 
		
		animation = new TranslateAnimation(0.0f, 0.0f, 0.0f, 0.0f);
        animation.setDuration(1);
        layoutTransparent.startAnimation(animation);
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		
	}
	
	private void setDisableInputElements(){
		etUserID.setEnabled(false);
		etPassword.setEnabled(false);
		btnLogIn.setEnabled(false);
		//textView1.setEnabled(false);
	}
	
	private void setEnableInputElements(){
		etUserID.setEnabled(true);
		etPassword.setEnabled(true);
		btnLogIn.setEnabled(true);
		//textView1.setEnabled(true);
	}
	
}