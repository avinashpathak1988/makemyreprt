package com.luminousinfoways.makemyreportandroid.ReportList;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.luminousinfoways.makemyreportandroid.R;
import com.luminousinfoways.makemyreportandroid.util.Constants;
import com.luminousinfoways.makemyreportandroid.util.MMRSingleTone;
import com.luminousinfoways.makemyreportandroid.util.Util;

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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FragmentProfile extends Fragment {
	
	EditText etFullName;
	EditText editMob;
	TextView etDob;
	
	String name, mob, dob;

	ImageView imgDate;

	public static FragmentProfile newInstance() {
		FragmentProfile f = new FragmentProfile();
		return f;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_profile, container,
				false);
		MMRSingleTone.getInstance().currentFragment = FragmentProfile.this;
		
		imgDate = (ImageView) rootView.findViewById(R.id.imgDate);

		etFullName = (EditText) rootView.findViewById(R.id.editText1);
		editMob = (EditText) rootView.findViewById(R.id.editMob);
		etDob = (TextView) rootView.findViewById(R.id.etDob);
		
		imgDate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new OnDateSetListener() {
					
					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear,
							int dayOfMonth) {
						int month = monthOfYear + 1;
						etDob.setText(dayOfMonth+"/"+month+"/"+year);
					}
				}, Util.getCurrentYear(), Util.getCurrentMonth(), Util.geCurrentDay());
				datePickerDialog.show();
			}
		});
		
		TextView tvSave = (TextView) rootView.findViewById(R.id.tvSave);
		tvSave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(etFullName.getText().toString().trim().length() <= 0){
					Toast.makeText(getActivity(), "Enter Name.", Toast.LENGTH_SHORT).show();
					return;
				}
				
				if(editMob.getText().toString().trim().length() <= 0){
					Toast.makeText(getActivity(), "Enter Contact Number.", Toast.LENGTH_SHORT).show();
					return;
				}
				
				if(editMob.getText().toString().trim().length() > 10){
					Toast.makeText(getActivity(), "Enter 10 digits only.", Toast.LENGTH_SHORT).show();
					return;
				}
				
				if(etDob.getText().toString().trim().length() <= 0){
					Toast.makeText(getActivity(), "Enter Date Of Birth.", Toast.LENGTH_SHORT).show();
					return;
				}
				
				name = etFullName.getText().toString().trim();
				mob = editMob.getText().toString().trim();
				dob = etDob.getText().toString().trim();
				
				UserProfileUpdateAsyncTask asynctask = new UserProfileUpdateAsyncTask(getView());
				asynctask.execute(name, mob, dob);
			}
		});
		
		TextView tvReset = (TextView) rootView.findViewById(R.id.tvReset);
		tvReset.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				etFullName.setText("");
				editMob.setText("");
				etDob.setText("");
				name = mob = dob = "";
			}
		});
				
		return rootView;
	}
	
	@Override
	public void onResume() {
		super.onResume();
			
		if(Util.isConnected(getActivity())){
			UserProfileAsyncTask reportList = new UserProfileAsyncTask(getView());
			reportList.execute();
		} else{
			
			//TODO show alert no internet connection
		}
	}
	
	private class UserProfileAsyncTask extends AsyncTask<Void, Void, String> {
		
		View context;
		String fullName;
        String mobNo;
        String dob;
        String message = "";
        
		
		public UserProfileAsyncTask(View context){
			this.context = context;
		}

		@Override
		protected String doInBackground(Void... params) {
			
			String orgId = context.getContext().getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 1).getString(Constants.SP_ORG_ID, null);
			String apikey = context.getContext().getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 1).getString(Constants.SP_API_KEY, null);
			if(orgId == null)
				return "Org id not found.";
			
			try{
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
				HttpPost httpPost = new HttpPost(Constants.URL + Constants.PROFILE_REQUEST_URL+orgId);
					httpPost.setHeader("Authorization", apikey);
					httpResponse = httpClient.execute(httpPost);
					InputStream inputStream = httpResponse.getEntity().getContent();
					
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
					String line;
					String response = "";
					while((line = bufferedReader.readLine()) != null){
						response = response + line;
					}
					Log.i("ReportList", "Response : [" + response + "]");
					
					/*{
					    "fullName": "BIKASH",
					    "contactNo": "8999999999",
					    "userDob": "15/10/2014"
					}
					*/

				if(Util.isJSONValid(response) == false){
					message = "Server side error occur.";
					return message;
				}

				JSONObject jsonObject = new JSONObject(response);

				if(jsonObject.isNull("Response") == false){
					message = "Your password is changed.\nPlease logout and login again to get your app work.";
					return message;
				}

	            fullName = jsonObject.getString("fullName");
	            mobNo = jsonObject.getString("contactNo");
	            if(jsonObject.getString("userDob").contains("null")
	            		&& (jsonObject.getString("userDob").length() == 4) ){
	            	dob = "";
	            } else{
	            	dob = jsonObject.getString("userDob");
	            }
	           message = "success";
	            
			} catch(ConnectTimeoutException e){
				message = "Connection time out.\nPlease reset internet connection.";
				return message;
			} catch(Exception e){
				Log.e("ReportList", "Exception", e);
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			try {
				
				etFullName.setText(fullName);
				editMob.setText(mobNo);
				etDob.setText(dob);
			
			} catch(Exception e){
				
			}
		}
	}
	
	private class UserProfileUpdateAsyncTask extends AsyncTask<String, Void, String> {
		
		View context;
        
        String submitStatus = null;
    	String message = null;
		
		public UserProfileUpdateAsyncTask(View context){
			this.context = context;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			
			String orgId = context.getContext().getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 1).getString(Constants.SP_ORG_ID, null);
			String apikey = context.getContext().getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 1).getString(Constants.SP_API_KEY, null);
			if(orgId == null)
				return "Org id not found.";
			
			try{
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
				HttpPost httpPost = new HttpPost(Constants.URL + Constants.PROFILE_UPDATE_REQUEST_URL+orgId);
				
				String name = params[0];
				String mobno = params[1];
				String dob = params[2];
				
				List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
				list.add(new BasicNameValuePair("full_name", name));
				list.add(new BasicNameValuePair("user_dob", dob));
				list.add(new BasicNameValuePair("contact_no", mobno));
				
					httpPost.setHeader("Authorization", apikey);
					httpPost.setEntity(new UrlEncodedFormEntity(list));
					httpResponse = httpClient.execute(httpPost);
					InputStream inputStream = httpResponse.getEntity().getContent();
					
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
					String line;
					String response = "";
					while((line = bufferedReader.readLine()) != null){
						response = response + line;
					}
					Log.i("ReportList", "Response : [" + response + "]");
					
					/*{
					    "Response": [
					        {
					            "Response": {
					                "submitStatus": "Success",
					                "Message": "Profile updated Successfully"
					            }
					        }
					    ]
					}
					*/
	            
	            JSONObject jsonObject = new JSONObject(response);
	            JSONArray array = jsonObject.getJSONArray("Response");
	            for (int i = 0; i < array.length(); i++) {
	            	JSONObject jobject = array.getJSONObject(i).getJSONObject("Response");
	            	submitStatus = jobject.getString("submitStatus");
	            	message = jobject.getString("Message");
				}
	            
			} catch(ConnectTimeoutException e){
				message = "Connection time out.\nPlease reset internet connection.";
				return message;
			} catch(Exception e){
				Log.e("ReportList", "Exception", e);
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
		}
	}
}
