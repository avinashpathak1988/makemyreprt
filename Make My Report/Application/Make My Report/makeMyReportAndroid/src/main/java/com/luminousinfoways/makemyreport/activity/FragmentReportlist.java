package com.luminousinfoways.makemyreport.activity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

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

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
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

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.luminousinfoways.makemyreport.R;
import com.luminousinfoways.makemyreport.database.MMRDatabaseHelper;
import com.luminousinfoways.makemyreport.pojo.Report;
import com.luminousinfoways.makemyreport.util.Constants;
import com.luminousinfoways.makemyreport.util.MMRSingleTone;
import com.luminousinfoways.makemyreport.util.Util;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class FragmentReportlist extends SherlockFragment implements AnimationListener {
	
	SmoothProgressBar mPocketBar;

	Animation animation;
	RelativeLayout layout;
	boolean moveUp = false;
	boolean moveDown = false;
	LinearLayout layoutTransparent;

	//ImageView imgPending, imgSubmitted, imgExpired;
	TextView tvPending, tvSubmitted, tvExpired;
	
	RelativeLayout layoutPending, layoutSubmitted, layoutExpired;
	
	FragmentTransaction fragmentTransaction;

	private Menu optionsMenu;
	private boolean is_refreshing = false;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_reportlist, container, false);
		
		MMRSingleTone.getInstance().currentFragment = FragmentReportlist.this;
		
		layoutTransparent = (LinearLayout) view.findViewById(R.id.layoutTransparent);
		
		 mPocketBar = (SmoothProgressBar) view.findViewById(R.id.google_now);
		 mPocketBar.setVisibility(View.GONE);

		   /* imgPending = (ImageView) view.findViewById(R.id.imgPending);
			imgSubmitted = (ImageView) view.findViewById(R.id.imgSubmitted);
			imgExpired = (ImageView) view.findViewById(R.id.imgExpired);*/
			
			tvPending = (TextView) view.findViewById(R.id.tvPending);
			tvSubmitted = (TextView) view.findViewById(R.id.tvSubmitted);
			tvExpired = (TextView) view.findViewById(R.id.tvExpired);
			
			layoutPending = (RelativeLayout) view.findViewById(R.id.layoutPending);
			layoutSubmitted = (RelativeLayout) view.findViewById(R.id.layoutSubmitted);
			layoutExpired = (RelativeLayout) view.findViewById(R.id.layoutExpired);	
		    
		
		return view;
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

			layout = (RelativeLayout) getActivity().findViewById(R.id.layoutBg);
			animation = AnimationUtils.loadAnimation(getActivity(), R.anim.move_up);
			animation.setAnimationListener(this);
			moveUp = true;
			moveDown = false;
			layout.setDrawingCacheEnabled(true);
			layout.startAnimation(animation);

			customAlert(getActivity(),"No Internet Connection.", 0);
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
		
		setOnclickListener();
		
		/*
		 * TODO Pull down to refresh
		 */
		
		if(MMRSingleTone.getInstance().isBackFromSubmit){
			MMRSingleTone.getInstance().isBackFromSubmit = false;
			if(Util.isConnected(getActivity())){
				
				LocalReportListAsync async = new LocalReportListAsync(getView());
				async.execute();
			
			} else{
			
				layout = (RelativeLayout) getActivity().findViewById(R.id.layoutBg);
				animation = AnimationUtils.loadAnimation(getActivity(), R.anim.move_up);
				animation.setAnimationListener(this);
				moveUp = true;
				moveDown = false;
				layout.setDrawingCacheEnabled(true);
				layout.startAnimation(animation);
				
				customAlert(getActivity(),"No Internet Connection.", 0);
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
				
					layout = (RelativeLayout) getActivity().findViewById(R.id.layoutBg);
					animation = AnimationUtils.loadAnimation(getActivity(), R.anim.move_up);
					animation.setAnimationListener(this);
					moveUp = true;
					moveDown = false;
					layout.setDrawingCacheEnabled(true);
					layout.startAnimation(animation);
					
					customAlert(getActivity(),"No Internet Connection.", 0);
				}
			}
		}
		
	}
	
	private void customAlert(final Activity context, String message, int status){
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
				layout = (RelativeLayout) context.findViewById(R.id.layoutBg);
				animation = AnimationUtils.loadAnimation(context, R.anim.move_down);
				animation.setAnimationListener(FragmentReportlist.this);
				moveUp = false;
				moveDown = true;
				layout.startAnimation(animation);
			}
		});
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
	
	public void setOnclickListener(){
		layoutPending.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setForPending();
			}
		});
		
		layoutSubmitted.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setForSubmitted();
			}
		});
		
		layoutExpired.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setForExpiered();
			}
		});
		
		if(MMRSingleTone.getInstance().currentReportCategoryShowing == 1){
			setForPending();
		} else if(MMRSingleTone.getInstance().currentReportCategoryShowing == 2){
			setForSubmitted();
		} else if(MMRSingleTone.getInstance().currentReportCategoryShowing == 3){
			setForExpiered();
		} else{
			setForPending();
		}
	}
	
	private void setForSubmitted() {
		tvPending.setTextColor(Color.WHITE);
		layoutPending.setBackgroundColor(getResources().getColor(R.color.red));
		
		tvSubmitted.setTextColor(getResources().getColor(R.color.login_btn_color_pressed));
		layoutSubmitted.setBackgroundColor(Color.WHITE);
		
		tvExpired.setTextColor(Color.WHITE);
		layoutExpired.setBackgroundColor(getResources().getColor(R.color.indigo));
		
		fragmentTransaction = getFragmentManager().beginTransaction();
		FragmentSubmitted fr = new FragmentSubmitted();
		fragmentTransaction.replace(R.id.fragment_place, fr);
		fragmentTransaction.commit();
	}
	
	private void setForPending() {
		tvPending.setTextColor(getResources().getColor(R.color.red));
		layoutPending.setBackgroundColor(Color.WHITE);
		
		tvSubmitted.setTextColor(Color.WHITE);
		layoutSubmitted.setBackgroundColor(getResources().getColor(R.color.login_btn_color_pressed));
		
		tvExpired.setTextColor(Color.WHITE);
		layoutExpired.setBackgroundColor(getResources().getColor(R.color.indigo));
		
		fragmentTransaction = getFragmentManager().beginTransaction();
		FragmentPending fr = new FragmentPending();
		fragmentTransaction.replace(R.id.fragment_place, fr);
		fragmentTransaction.commit();
	}
	
	private void setForExpiered() {
		tvPending.setTextColor(Color.WHITE);
		layoutPending.setBackgroundColor(getResources().getColor(R.color.red));
		
		tvSubmitted.setTextColor(Color.WHITE);
		layoutSubmitted.setBackgroundColor(getResources().getColor(R.color.login_btn_color_pressed));
		
		tvExpired.setTextColor(getResources().getColor(R.color.indigo));
		layoutExpired.setBackgroundColor(Color.WHITE);
		
		fragmentTransaction = getFragmentManager().beginTransaction();
		FragmentExpired fr = new FragmentExpired();
		fragmentTransaction.replace(R.id.fragment_place, fr);
		fragmentTransaction.commit();
	}
	
	private class LocalReportListAsync extends AsyncTask<Void, Void, String>{
		
		View context;
		String message;
		
		public LocalReportListAsync(View context){
			this.context = context;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mPocketBar.progressiveStop();
			mPocketBar.progressiveStart();
			mPocketBar.setVisibility(View.VISIBLE);
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
			try{

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
				message = "Server error found. Please reset internet connection and try again.";
				return message;

			} finally {
				if(httpClient != null && httpClient.getConnectionManager() != null) {
					httpClient.getConnectionManager().shutdown();
				}
			}
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
							}
							
							if(MMRSingleTone.getInstance().currentFragment instanceof FragmentPending){
								((FragmentPending) MMRSingleTone.getInstance().currentFragment).refreshUI();
							}
							
							if(MMRSingleTone.getInstance().currentFragment instanceof FragmentExpired){
								((FragmentExpired) MMRSingleTone.getInstance().currentFragment).refreshUI();
							}
						}
					} else{
						layout = (RelativeLayout) context.findViewById(R.id.layoutBg);
						animation = AnimationUtils.loadAnimation(context.getContext(), R.anim.move_up);
						animation.setAnimationListener(FragmentReportlist.this);
						moveUp = true;
						moveDown = false;
						layout.setDrawingCacheEnabled(true);
						layout.startAnimation(animation);
						
						customAlert(message, 0);
					}
					
					MMRSingleTone.getInstance().isRunningReportAsynncTask = false;
					
					setOnclickListener();
				
				} catch(Exception e){
					
				}
			
				mPocketBar.progressiveStop();
				mPocketBar.setVisibility(View.GONE);
			setRefreshActionButtonState(false);
			super.onPostExecute(result);
		}
	}

	private void customAlert(String message, int status){
		TextView tvMsg = (TextView) getView().findViewById(R.id.tvMsg);
		TextView tvOk = (TextView) getView().findViewById(R.id.tvOk);
		ImageView img = (ImageView) getView().findViewById(R.id.img);
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
				layout = (RelativeLayout) getView().findViewById(R.id.layoutBg);
				animation = AnimationUtils.loadAnimation(getActivity(), R.anim.move_down);
				animation.setAnimationListener(FragmentReportlist.this);
				moveUp = false;
				moveDown = true;
				layout.startAnimation(animation);
			}
		});
	}
}
