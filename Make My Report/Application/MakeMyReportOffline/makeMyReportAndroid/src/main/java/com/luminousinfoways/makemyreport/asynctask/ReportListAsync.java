package com.luminousinfoways.makemyreport.asynctask;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.luminousinfoways.makemyreport.activity.FragmentReportlist;
import com.luminousinfoways.makemyreport.database.MMRDatabaseHelper;
import com.luminousinfoways.makemyreport.pojo.Report;
import com.luminousinfoways.makemyreport.util.Constants;
import com.luminousinfoways.makemyreport.util.MMRSingleTone;

public class ReportListAsync extends AsyncTask<Void, Void, String>{
	
	Context context;
	
	public ReportListAsync(Context context){
		this.context = context;
	}

	@Override
	protected String doInBackground(Void... params) {
		
		String orgId = context.getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 1).getString(Constants.SP_ORG_ID, null);
		String apikey = context.getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 1).getString(Constants.SP_API_KEY, null);
		if(orgId == null)
			return "Org id not found.";
		if(apikey == null)
			return "API key not found.";
		try{
			HttpResponse httpResponse = null;
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(Constants.URL + Constants.REPORTIST_URL_REQUEST+orgId);
				httpPost.setHeader("Authorization", apikey);
				MMRSingleTone.getInstance().isRunningReportAsynncTask = true;
				MMRSingleTone.getInstance().isAlreadyRunReportAsyncBefore = true;
				httpResponse = httpClient.execute(httpPost);
				InputStream inputStream = httpResponse.getEntity().getContent();				
				
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
				String line;
				String response = "";
				while((line = bufferedReader.readLine()) != null){
					response = response + line;
				}
				Log.i("ReportList", "Response : ["+response+"]");

			/*
			*
			* {"Reports":
			* 	[{"reportID":"1",
			* 	"reportTitle":"ghgfhgfhgfh",
			* 	"reportType":"Instant",
			* 	"reportShortDesc":"fghfghgfhgf",
			* 	"reportCreatedBy":"avinash pathak",
			* 	"reportCreatedOn":"19\/08\/2015",
			* 	"LastDateofSubmission":"20\/08\/2015",
			* 	"LastResponseDate":"19\/08\/2015",
			* 	"color":"green",
			* 	"FbAssignUserID":"1",
			* 	"RecentSubmission":"Y",
			* 	"ShowSubmitButton":"N",
			* 	"ShowRejectButton":"N",
			* 	"ShowRejectMessage":"",
			* 	"ShowViewButton":"Y",
			* 	"reportStatus":"S",
			* 	"noOfTimesReportSubmission":4,
			* 	"noOfTimesReportSubmitted":2,
			* 	"noOfTimesReportToBeSubmitted":2,
			* 	"canSubmitReportAfterDeadLine":"N"
			*  }]
			* }
			*
			* */
            
            JSONObject jsonObject = new JSONObject(response);
            JSONArray array = jsonObject.getJSONArray("Reports");
            
            MMRDatabaseHelper databaseHelper = new MMRDatabaseHelper(context);
                        
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
            	report.setNoOfTimesReportSubmission(array.getJSONObject(i).getInt("noOfTimesReportSubmission"));
            	report.setNoOfTimesReportToBeSubmitted(array.getJSONObject(i).getInt("noOfTimesReportToBeSubmitted"));
            	report.setCanSubmitReportAfterDeadLine(array.getJSONObject(i).getString("canSubmitReportAfterDeadLine"));

            	databaseHelper.insertUpdateReport(report);
			}
            
            databaseHelper.close();
            
            return "success";
		} catch(Exception e){
			Log.e("ReportList", "Exception", e);
		}
		
		return null;
	}
	
	@Override
	protected void onPostExecute(String result) {
		
		if(result != null && result.equalsIgnoreCase("success")){
			MMRDatabaseHelper databaseHelper = new MMRDatabaseHelper(context);
			MMRSingleTone.getInstance().submitedReport.clear();
			MMRSingleTone.getInstance().submitedReport = databaseHelper.getSubmittedReports();
			Collections.reverse(MMRSingleTone.getInstance().submitedReport);
			MMRSingleTone.getInstance().pendingReport.clear();
			MMRSingleTone.getInstance().pendingReport = databaseHelper.getPendingReports();
			Collections.reverse(MMRSingleTone.getInstance().pendingReport);
			MMRSingleTone.getInstance().expierdReport.clear();
			MMRSingleTone.getInstance().expierdReport = databaseHelper.getExpieredReports();
			Collections.reverse(MMRSingleTone.getInstance().expierdReport);
			databaseHelper.close();
			
			List<Report> submittedList = MMRSingleTone.getInstance().submitedReport;
			List<Report> pendingList = MMRSingleTone.getInstance().pendingReport;
			List<Report> expieredList = MMRSingleTone.getInstance().expierdReport;
			
			
			if(MMRSingleTone.getInstance().currentFragment != null){
				if(MMRSingleTone.getInstance().currentFragment instanceof FragmentReportlist){
					((FragmentReportlist) MMRSingleTone.getInstance().currentFragment).setOnclickListener();
				}
			}
		}
		
		MMRSingleTone.getInstance().isRunningReportAsynncTask = false;
		
		super.onPostExecute(result);
	}
}
