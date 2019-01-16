package com.luminousinfoways.makemyreportandroid.ReportList;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.luminousinfoways.makemyreportandroid.R;
import com.luminousinfoways.makemyreportandroid.database.MMRDatabaseHelper;
import com.luminousinfoways.makemyreportandroid.pojo.Report;
import com.luminousinfoways.makemyreportandroid.util.Constants;
import com.luminousinfoways.makemyreportandroid.util.MMRSingleTone;
import com.luminousinfoways.makemyreportandroid.util.Util;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

public class FragmentReportlist extends Fragment {

	ViewPager pager;
	private String titles[] = new String[]{"Pending", "Submitted", "Expired"};

	SlidingTabLayout slidingTabLayout;

	public static FragmentReportlist newInstance() {
		FragmentReportlist f = new FragmentReportlist();
		return f;
	}
	
	FragmentTransaction fragmentTransaction;

	private Menu optionsMenu;
	private boolean is_refreshing = false;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_reportlist_tab_layout, container, false);

		pager = (ViewPager) rootView.findViewById(R.id.viewpager);
		slidingTabLayout = (SlidingTabLayout) rootView.findViewById(R.id.sliding_tabs);
		pager.setAdapter(new ViewPagerAdapter(getActivity().getSupportFragmentManager(), titles));

		slidingTabLayout.setViewPager(pager);
		slidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
			@Override
			public int getIndicatorColor(int position) {
				return Color.WHITE;
			}
		});

		MMRSingleTone.getInstance().currentFragment = FragmentReportlist.this;

		return rootView;

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		this.optionsMenu = menu;
		inflater.inflate(R.menu.main, menu);
		setRefreshActionButtonState(is_refreshing);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if(id == R.id.action_refresh){
			if(!is_refreshing){
				refreshList();
			}
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setRetainInstance(true);
		setHasOptionsMenu(true);
	}

	private void refreshList(){
		if(Util.isConnected(getActivity())){

			LocalReportListAsync async = new LocalReportListAsync(getView());
			async.execute();

		} else{
			// TODO show alert no internet connection
		}
	}

	public void setRefreshActionButtonState(final boolean refreshing) {
		is_refreshing = refreshing;
		if (optionsMenu != null) {
			final MenuItem refreshItem = optionsMenu
					.findItem(R.id.action_refresh);
			if (refreshItem != null) {
				if (refreshing) {
					refreshItem.setActionView(R.layout.actionbar_indeterminate_progress);
				} else {
					refreshItem.setActionView(null);
				}
			}
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();		

		/*
		 * TODO Pull down to refresh
		 */
		
		if(MMRSingleTone.getInstance().isBackFromSubmit){
			MMRSingleTone.getInstance().isBackFromSubmit = false;
			if(Util.isConnected(getActivity())){
				
				LocalReportListAsync async = new LocalReportListAsync(getView());
				async.execute();
			
			} else{
				//TODO show alert no internet connection
			}
		} else{
			if( (!MMRSingleTone.getInstance().isAlreadyRunReportAsyncBefore)
					&&  (!MMRSingleTone.getInstance().isRunningReportAsynncTask)){

				MMRDatabaseHelper databaseHelper = new MMRDatabaseHelper(getActivity());
				databaseHelper.deleteAllReports();
				databaseHelper.close();
				
				MMRSingleTone.getInstance().submitedReport.clear();
				MMRSingleTone.getInstance().pendingReport.clear();
				MMRSingleTone.getInstance().expierdReport.clear();
				
				if(Util.isConnected(getActivity())){
				
					LocalReportListAsync async = new LocalReportListAsync(getView());
					async.execute();
				
				} else{

					//TODO show alert no internet connection
				}
			}
		}
		
	}

	@Override
	public void onDetach() {
		super.onDetach();
		try {
			Field childFragmentManager = Fragment.class
					.getDeclaredField("mChildFragmentManager");
			childFragmentManager.setAccessible(true);
			childFragmentManager.set(this, null);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	private class LocalReportListAsync extends AsyncTask<Void, Void, String> {
		
		View context;
		String message;
		
		public LocalReportListAsync(View context){
			this.context = context;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			setRefreshActionButtonState(true);
		}

		@Override
		protected String doInBackground(Void... params) {
			
			String orgId = context.getContext().getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 1).getString(Constants.SP_ORG_ID, null);
			String apikey = context.getContext().getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 1).getString(Constants.SP_API_KEY, null);
			if(orgId == null){
				message =  "Org id not found.";
				return message;
			}
			if(apikey == null){
				message =  "API key not found.";
				return message;
			}
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
					Log.i("ReportList", "Response : [" + response + "]");
					/*
						{
							"Reports": [
							{
							"reportID": "512",
							"reportTitle": "test report 22jun level1",
							"reportType": "Instant",
							"reportShortDesc": "test report 22jun level1",
							"reportCreatedBy": "SUPERUSER",
							"reportCreatedOn": "22\/06\/2015",
							"LastDateofSubmission": "30\/06\/2015",
							"LastResponseDate": "22\/06\/2015",
							"color": "green",
							"FbAssignUserID": "981",
							"RecentSubmission": "Y",
							"ShowSubmitButton": "N",
							"ShowRejectButton": "N",
							"ShowRejectMessage": "",
							"ShowViewButton": "Y",
							"reportStatus": "S",
							"noOfTimesReportSubmission": 1,
							"noOfTimesReportSubmitted": 1,
							"noOfTimesReportToBeSubmitted": 0,
							"canSubmitReportAfterDeadLine": "Y"
							}]}
					 * */


				/*
				* {
					"Response": [
					{
					"Apikey": "Api key does not exit"
					}
					]
					}
				*
				* */

				if(Util.isJSONValid(response) == false){
					message = "Server side error occur.";
					return message;
				}
	            
	            JSONObject jsonObject = new JSONObject(response);

				if(jsonObject.isNull("Response") == false){
					message = "Your password is changed.\nPlease logout and login again to get your app work.";
					return message;
				}

				JSONArray array = jsonObject.getJSONArray("Reports");

	            MMRDatabaseHelper databaseHelper = new MMRDatabaseHelper(context.getContext());

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
	            
	            message = "success";
	            return message;
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
				try {				
					
					if(result != null && result.equalsIgnoreCase("success")){
						MMRDatabaseHelper databaseHelper = new MMRDatabaseHelper(context.getContext());
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
							if(MMRSingleTone.getInstance().currentFragment instanceof FragmentSubmitted){
								((FragmentSubmitted) MMRSingleTone.getInstance().currentFragment).refreshUI();
							} else if(MMRSingleTone.getInstance().currentFragment instanceof FragmentPending){
								((FragmentPending) MMRSingleTone.getInstance().currentFragment).refreshUI();
							} else if(MMRSingleTone.getInstance().currentFragment instanceof FragmentExpired){
								((FragmentExpired) MMRSingleTone.getInstance().currentFragment).refreshUI();
							} else{
								((FragmentPending) MMRSingleTone.getInstance().currentFragment).refreshUI();
							}
						}
					} else{
						//TODO show alert no internet connection
					}
					
					MMRSingleTone.getInstance().isRunningReportAsynncTask = false;

				} catch(Exception e){

				}
			setRefreshActionButtonState(false);
			super.onPostExecute(result);
		}
	}
}
