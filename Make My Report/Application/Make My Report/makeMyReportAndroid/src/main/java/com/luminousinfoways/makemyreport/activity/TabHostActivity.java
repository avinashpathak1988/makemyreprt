package com.luminousinfoways.makemyreport.activity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TabHost;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.luminousinfoways.makemyreport.R;
import com.luminousinfoways.makemyreport.pojo.Report;
import com.luminousinfoways.makemyreport.util.Constants;
import com.luminousinfoways.makemyreport.util.MMRSingleTone;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class TabHostActivity extends SherlockFragmentActivity {
	/** Called when the activity is first created. */
	
	LinearLayout layoutProgress;
	SmoothProgressBar mPocketBar;
	
	private FragmentTabHost mTabHost;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tab_host);
		
		layoutProgress = (LinearLayout) findViewById(R.id.layoutProgress);
		 mPocketBar = (SmoothProgressBar) findViewById(R.id.google_now);

	}
	
private class ReportList extends AsyncTask<Void, Void, String>{
		
		View context;
		
		public ReportList(View context){
			this.context = context;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mPocketBar.progressiveStop();
			mPocketBar.progressiveStart();
			layoutProgress.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(Void... params) {
			
			String orgId = context.getContext().getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 1).getString(Constants.SP_ORG_ID, null);
			String apikey = context.getContext().getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 1).getString(Constants.SP_API_KEY, null);
			if(orgId == null)
				return "Org id not found.";
			
			try{
				HttpResponse httpResponse = null;
				HttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(Constants.URL + Constants.REPORTIST_URL_REQUEST+orgId);
					httpPost.setHeader("Authorization", apikey);
					httpResponse = httpClient.execute(httpPost);
					InputStream inputStream = httpResponse.getEntity().getContent();				
					
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
					String line;
					String response = "";
					while((line = bufferedReader.readLine()) != null){
						response = response + line;
					}
					Log.i("ReportList", "Response : ["+response+"]");
	            
	            JSONObject jsonObject = new JSONObject(response);
	            JSONArray array = jsonObject.getJSONArray("Reports");
	            MMRSingleTone.getInstance().expierdReport.clear();
	            MMRSingleTone.getInstance().rejectedReport.clear();
	            MMRSingleTone.getInstance().submitedReport.clear();
	            MMRSingleTone.getInstance().pendingReport.clear();
	            
	            for (int i = 0; i < array.length(); i++) {
	            	Report report = new Report();
	            	report.setReportID(array.getJSONObject(i).getInt("reportID"));
	            	report.setReportTitle(array.getJSONObject(i).getString("reportTitle"));
	            	report.setReportType(array.getJSONObject(i).getString("reportType"));
	            	report.setReportCreatedBy(array.getJSONObject(i).getString("reportCreatedBy"));
	            	report.setReportCreatedOn(array.getJSONObject(i).getString("reportCreatedOn"));
	            	report.setLastDateofSubmission(array.getJSONObject(i).getString("LastDateofSubmission"));
	            	report.setFbAssignUserID(array.getJSONObject(i).getString("FbAssignUserID"));
	            	report.setRecentSubmission(array.getJSONObject(i).getString("RecentSubmission"));
	            	report.setShowSubmitButton(array.getJSONObject(i).getString("ShowSubmitButton"));
	            	report.setShowRejectButton(array.getJSONObject(i).getString("ShowRejectButton"));
	            	report.setShowRejectMessage(array.getJSONObject(i).getString("ShowRejectMessage"));
	            	report.setShowViewButton(array.getJSONObject(i).getString("ShowViewButton"));
	            	report.setReportStatus(array.getJSONObject(i).getString("reportStatus"));
	            	report.setNoOfTimesReportSubmitted(array.getJSONObject(i).getInt("noOfTimesReportSubmitted"));
	            	if(report.getReportStatus().equalsIgnoreCase("R")){
	            		//Reject List
	            		MMRSingleTone.getInstance().rejectedReport.add(report);
	            	} else if(report.getReportStatus().equalsIgnoreCase("S")){
	            		if(report.getShowSubmitButton().equalsIgnoreCase("Y")){
	            			//Pending List
	            			MMRSingleTone.getInstance().pendingReport.add(report);
	            		} 
	            	} else if(report.getReportStatus().equalsIgnoreCase("E")){
	            		//Expired List
	            		MMRSingleTone.getInstance().expierdReport.add(report);
	            	}
	            	
	            	if(report.getNoOfTimesReportSubmitted() > 0){
            			//Submitted list
            			MMRSingleTone.getInstance().submitedReport.add(report);
            		}
	            	
				}
			} catch(Exception e){
				Log.e("ReportList", "Exception", e);
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			try{
				
			mPocketBar.progressiveStop();
			layoutProgress.setVisibility(View.GONE);
			Resources res = getResources(); // Resource object to get Drawables
			TabHost tabHost = mTabHost; // The activity TabHost
			TabHost.TabSpec spec; // Reusable TabSpec for each tab
			Intent intent; // Reusable Intent for each tab

			// Create an Intent to launch an Activity for the tab (to be reused)
			intent = new Intent().setClass(TabHostActivity.this, InboxActivity.class);
			spec = tabHost.newTabSpec("home")
					.setIndicator("HOME", res.getDrawable(R.drawable.ic_action_camera))
					.setContent(intent);
			tabHost.addTab(spec);

			// Do the same for the other tabs

			intent = new Intent().setClass(TabHostActivity.this, SentActivity.class);
			spec = tabHost.newTabSpec("inbox")
					.setIndicator("INBOX", res.getDrawable(R.drawable.ic_action_accept))
					.setContent(intent);
			tabHost.addTab(spec);
			
			//set tab which one you want open first time 0 or 1 or 2
			tabHost.setCurrentTab(0);
			
			
			}catch(Exception e){
				
			}
		}
	}
	
}