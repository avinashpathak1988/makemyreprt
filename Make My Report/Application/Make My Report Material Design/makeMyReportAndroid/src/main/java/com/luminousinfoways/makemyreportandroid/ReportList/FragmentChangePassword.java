package com.luminousinfoways.makemyreportandroid.ReportList;

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
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FragmentChangePassword extends Fragment {
	
	EditText oldPass;
	EditText newPass;
	EditText confirmPass;

	View rootView;

	public static FragmentChangePassword newInstance() {
		FragmentChangePassword f = new FragmentChangePassword();
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_changepassword, container,
				false);
		MMRSingleTone.getInstance().currentFragment = FragmentChangePassword.this;

		oldPass = (EditText) rootView.findViewById(R.id.editText1);
		newPass = (EditText) rootView.findViewById(R.id.editTextNewPass);
		confirmPass = (EditText) rootView.findViewById(R.id.editTextConfirmPass);

		TextView btnSave = (TextView) rootView.findViewById(R.id.btnSave);
		btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (oldPass.getText().toString().trim().length() <= 0) {
					Toast.makeText(getActivity(), "Enter Old Password", Toast.LENGTH_SHORT).show();
					return;
				}

				if (newPass.getText().toString().trim().length() <= 0) {
					Toast.makeText(getActivity(), "Enter New Password", Toast.LENGTH_SHORT).show();
					return;
				}

				if (newPass.getText().toString().trim().equalsIgnoreCase(oldPass.getText().toString().trim())) {
					Toast.makeText(getActivity(), "Enter Different For New Password", Toast.LENGTH_SHORT).show();
					return;
				}

				if (confirmPass.getText().toString().trim().length() <= 0) {
					Toast.makeText(getActivity(), "Enter Confirm Password", Toast.LENGTH_SHORT).show();
					return;
				}

				if (!confirmPass.getText().toString().trim().equalsIgnoreCase(newPass.getText().toString().trim())) {
					Toast.makeText(getActivity(), "Enter Same New in Confirm Password", Toast.LENGTH_SHORT).show();
					return;
				}

				if (Util.isConnected(v.getContext())) {
					String tvOldPass = oldPass.getText().toString();
					String tvNewPass = newPass.getText().toString();

					ChangePasswordAsync asyncTask = new ChangePasswordAsync();
					asyncTask.execute(tvOldPass, tvNewPass);
				} else {

					//TODO show alert
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

	private class ChangePasswordAsync extends AsyncTask<String, Integer, Void> {

		String TAG = "ChangePasswordAsync";
		int status = 0;
		String message = "";


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
				Log.i(TAG, "Response : [" + response + "]");

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
						message = "Your password is changed.\nPlease logout and login again to get your app work.";
					}
				}

			} catch(ConnectTimeoutException e){
				message = "Connection time out.\nPlease reset internet connection.";
			} catch(Exception e){
				Log.e(TAG, "Exception", e);
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);


		}
	}
}
