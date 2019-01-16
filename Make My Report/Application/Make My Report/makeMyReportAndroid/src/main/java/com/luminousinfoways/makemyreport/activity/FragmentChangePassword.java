package com.luminousinfoways.makemyreport.activity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.luminousinfoways.makemyreport.R;
import com.luminousinfoways.makemyreport.util.Constants;
import com.luminousinfoways.makemyreport.util.MMRSingleTone;
import com.luminousinfoways.makemyreport.util.Util;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class FragmentChangePassword extends Fragment implements AnimationListener {
	
	EditText oldPass;
	EditText newPass;
	EditText confirmPass;
	
	LinearLayout lProLayout;
	SmoothProgressBar mPocketBar;
	
	Animation animation;
	RelativeLayout layout;
	boolean moveUp = false;
	boolean moveDown = false;
	LinearLayout layoutTransparent;
	
	View rootView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_changepassword, container,
				false);
		MMRSingleTone.getInstance().currentFragment = FragmentChangePassword.this;
		
		layoutTransparent = (LinearLayout) rootView.findViewById(R.id.layoutTransparent);
		
		lProLayout = (LinearLayout) rootView.findViewById(R.id.layoutProgress);
		mPocketBar = (SmoothProgressBar) rootView.findViewById(R.id.google_now);
	    		
		oldPass = (EditText) rootView.findViewById(R.id.editText1);
		newPass = (EditText) rootView.findViewById(R.id.editTextNewPass);
		confirmPass = (EditText) rootView.findViewById(R.id.editTextConfirmPass);
		
		TextView btnSave = (TextView) rootView.findViewById(R.id.btnSave);
		btnSave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(oldPass.getText().toString().trim().length() <= 0 ){
					Toast.makeText(getActivity(), "Enter Old Password", Toast.LENGTH_SHORT).show();
					return;
				}				
				
				if(newPass.getText().toString().trim().length() <= 0 ){
					Toast.makeText(getActivity(), "Enter New Password", Toast.LENGTH_SHORT).show();
					return;
				}	
				
				if(newPass.getText().toString().trim().equalsIgnoreCase(oldPass.getText().toString().trim())){
					Toast.makeText(getActivity(), "Enter Different For New Password", Toast.LENGTH_SHORT).show();
					return;
				}
				
				if(confirmPass.getText().toString().trim().length() <= 0 ){
					Toast.makeText(getActivity(), "Enter Confirm Password", Toast.LENGTH_SHORT).show();
					return;
				}	
				
				if(!confirmPass.getText().toString().trim().equalsIgnoreCase(newPass.getText().toString().trim())){
					Toast.makeText(getActivity(), "Enter Same New in Confirm Password", Toast.LENGTH_SHORT).show();
					return;
				}	
				
				if(Util.isConnected(v.getContext())){
					String tvOldPass = oldPass.getText().toString();
					String tvNewPass = newPass.getText().toString();
								
					ChangePasswordAsync asyncTask = new ChangePasswordAsync();
					asyncTask.execute(tvOldPass, tvNewPass);
				} else{
					
					layout = (RelativeLayout) rootView.findViewById(R.id.layoutBg);
					animation = AnimationUtils.loadAnimation(getActivity(), R.anim.move_up);
					animation.setAnimationListener(FragmentChangePassword.this);
					moveUp = true;
					moveDown = false;
					layout.setDrawingCacheEnabled(true);
					layout.startAnimation(animation);
					
					customAlert(getActivity(), "No Internet Connection.", 0);
				}
			}
		});
		
		TextView btnReset = (TextView) rootView.findViewById(R.id.btnReset);
		btnReset.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				oldPass.setText("");
				newPass.setText("");
				confirmPass.setText("");
			}
		});
			
		return rootView;
	}
	
	private void customAlert(Activity context, String message, int status){
		TextView tvMsg = (TextView) context.findViewById(R.id.tvMsg);
		TextView tvOk = (TextView) context.findViewById(R.id.tvOk);
		ImageView img = (ImageView) context.findViewById(R.id.img);
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
				layout = (RelativeLayout) rootView.findViewById(R.id.layoutBg);
				animation = AnimationUtils.loadAnimation(getActivity(), R.anim.move_down);
				animation.setAnimationListener(FragmentChangePassword.this);
				moveUp = false;
				moveDown = true;
				layout.startAnimation(animation);
			}
		});
	}
		
	private class ChangePasswordAsync extends AsyncTask<String, Integer, Void>{
		
		String TAG = "LoginAsync";
		int status = 0;
		String message = "";
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			lProLayout.setVisibility(View.VISIBLE);
						
			mPocketBar.progressiveStop();
			mPocketBar.progressiveStart();
		}

		@Override
		protected Void doInBackground(String... params) {
			
			String orgID = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 1).getString(Constants.SP_ORG_ID, null);
			if(orgID == null)
				return null;
			
			String apikey = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 1).getString(Constants.SP_API_KEY, null);
			if(apikey == null)
				return null;
			
			String oldPass = params[0];
			String newPass = params[1];
			
			List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
			list.add(new BasicNameValuePair("oldPwd", oldPass)); 
			list.add(new BasicNameValuePair("newPwd", newPass));
			
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
			HttpPost httpPost = new HttpPost(Constants.URL + Constants.CHANGE_PASSWORD_REQUEST_URL+orgID);
			try{
				httpPost.setEntity(new UrlEncodedFormEntity(list));
				httpPost.setHeader("Authorization", apikey);
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
				 * {"Response":{"submitStatus":"Failed","Message":"Sorry Old Password mismatch.","apiKey":""}}
				 * */

				if(Util.isJSONValid(response) == false){
					message = "Server side error occur.";
					return null;
				}

				JSONObject jsonObject = new JSONObject(response);
					
				JSONObject obj = jsonObject.getJSONObject("Response");
				
				String sts = obj.getString("submitStatus");
				message = obj.getString("Message");
				if(sts.equalsIgnoreCase("Success"))
					status = 1;
				else
					status = 0;
				
				if(status == 1){
					String api_key = obj.getString("apiKey");
					getActivity().getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 2).edit().putString(Constants.SP_API_KEY, api_key).commit();
				} else{
					if(message.equalsIgnoreCase("Api key does not exit")){
						message = "Please logout and login again to get your app work.";
					}
				}
			
			} catch(ConnectTimeoutException e){
				message = "Connection time out.\nPlease reset internet connection.";
			} catch(Exception e){
				Log.e(TAG, "Exception", e);
			} finally {
				if(httpClient != null && httpClient.getConnectionManager() != null) {
					httpClient.getConnectionManager().shutdown();
				}
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			
			lProLayout.setVisibility(View.INVISIBLE);
			
			mPocketBar.progressiveStop();
			
			if(status == 1){
				layout = (RelativeLayout) getActivity().findViewById(R.id.layoutBg);
				animation = AnimationUtils.loadAnimation(getActivity(), R.anim.move_up);
				animation.setAnimationListener(FragmentChangePassword.this);
				moveUp = true;
				moveDown = false;
				layout.setDrawingCacheEnabled(true);
				layout.startAnimation(animation);
				
				if(message.length() <= 0)
					message = "Password hasbeen changed successfully.";
				
				customAlert(getActivity(), message, 1);
				
			} else{
			
				layout = (RelativeLayout) getActivity().findViewById(R.id.layoutBg);
				animation = AnimationUtils.loadAnimation(getActivity(), R.anim.move_up);
				animation.setAnimationListener(FragmentChangePassword.this);
				moveUp = true;
				moveDown = false;
				layout.setDrawingCacheEnabled(true);
				layout.startAnimation(animation);
				
				if(message.length() <= 0)
					message = "Sorry Password could not be changed. Please try again.";

				
				customAlert(getActivity(), message, 0);
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
}
