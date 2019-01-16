package com.luminousinfoways.makemyreport.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.PercentFormatter;
import com.luminousinfoways.makemyreport.R;
import com.luminousinfoways.makemyreport.util.Constants;
import com.luminousinfoways.makemyreport.util.MMRSingleTone;
import com.luminousinfoways.makemyreport.util.Util;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class FragmentDashboard extends SherlockFragment implements AnimationListener, OnChartValueSelectedListener, View.OnClickListener {
			
	LinearLayout layoutProgress;
	SmoothProgressBar mPocketBar;

	Animation animation;
	RelativeLayout layout;
	boolean moveUp = false;
	boolean moveDown = false;
	LinearLayout layoutTransparent;

	private Menu optionsMenu;
	private boolean is_refreshing = false;

	PieChart mPieChart;
	Typeface tf;
	protected String[] mParties = new String[] {
			"Report Submitted",
			"Report Pending",
			"Report Rejected",
			"Report Expired"
	};

	TextView tvTotalAssignedReport;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		MMRSingleTone.getInstance().currentFragment = FragmentDashboard.this;
		
		View rootView = inflater.inflate(R.layout.fragment_dashboard, container,
				false);
		
		layoutTransparent = (LinearLayout) rootView.findViewById(R.id.layoutTransparent);
		
		layoutProgress = (LinearLayout) rootView.findViewById(R.id.layoutProgress);
		mPocketBar = (SmoothProgressBar) rootView.findViewById(R.id.google_now);

		mPieChart = (PieChart) rootView.findViewById(R.id.chartPie);
		tvTotalAssignedReport = (TextView) rootView.findViewById(R.id.tvTotalAssigned);
		 		
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

			DashboardASyncTask reportList = new DashboardASyncTask(getView());
			reportList.execute();

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
			
		if(Util.isConnected(getActivity())){
			DashboardASyncTask reportList = new DashboardASyncTask(getView());
			reportList.execute();
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
		
		super.onResume();		
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
				animation.setAnimationListener(FragmentDashboard.this);
				moveUp = false;
				moveDown = true;
				layout.startAnimation(animation);
			}
		});
	}
	
	private class DashboardASyncTask extends AsyncTask<Void, Void, String>{
		
		View context;
		String message = "";
		
		public DashboardASyncTask(View context){
			this.context = context;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mPocketBar.progressiveStop();
			mPocketBar.progressiveStart();
			layoutProgress.setVisibility(View.VISIBLE);
			setRefreshActionButtonState(true);
		}

		@Override
		protected String doInBackground(Void... params) {
			
			String orgId = context.getContext().getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 1).getString(Constants.SP_ORG_ID, null);
			String apikey = context.getContext().getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 1).getString(Constants.SP_API_KEY, null);
			if(orgId == null)
				return "Org id not found.";
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

				HttpPost httpPost = new HttpPost(Constants.URL + Constants.DASHBOARD_REQUEST_URL+orgId);
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
					
					/*
					 * {
						    "Response": {
						        "noOfReportAssign": 26,
						        "noOfReportSubmitted": 10,
						        "noOfReportExpired": 14,
						        "noOfReportPending": 0,
						        "noOfReportRejected": 2
						    }
						}				
					 * */

				if(Util.isJSONValid(response) == false){
					message = "Server side error occur.";
					return message;
				}

				JSONObject jsonObject = new JSONObject(response);
	            JSONObject object = jsonObject.getJSONObject("Response");

				if(object.isNull("noOfReportAssign") == true){
					message = "Your password is changed.\nPlease logout and login again to get your app work.";
					return message;
				}

	            int noOfReportAssign = object.getInt("noOfReportAssign");
	            int noOfReportSubmitted = object.getInt("noOfReportSubmitted");
	            int noOfReportExpired = object.getInt("noOfReportExpired");
	            int noOfReportPending = object.getInt("noOfReportPending");
	            int noOfReportRejected = object.getInt("noOfReportRejected");
	            
	            MMRSingleTone.getInstance().noOfReportAssign = noOfReportAssign;
	            MMRSingleTone.getInstance().noOfReportSubmitted = noOfReportSubmitted;
	            MMRSingleTone.getInstance().noOfReportExpired = noOfReportExpired;
	            MMRSingleTone.getInstance().noOfReportPending = noOfReportPending;
	            MMRSingleTone.getInstance().noOfReportRejected = noOfReportRejected;
	            
	            message = "success";
	            
			} catch(ConnectTimeoutException e){
				message = "Connection time out.\nPlease reset internet connection.";
				return message;
			} catch(Exception e){
				Log.e("ReportList", "Exception", e);
				message = "Some error found. Please reset internet connection and try again.";
				return message;
			} finally {
				if(httpClient != null && httpClient.getConnectionManager() != null) {
					httpClient.getConnectionManager().shutdown();
				}
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			try {
				
				mPocketBar.progressiveStop();
				layoutProgress.setVisibility(View.GONE);

				if(!message.equalsIgnoreCase("success")){
					layout = (RelativeLayout) getView().findViewById(R.id.layoutBg);
					animation = AnimationUtils.loadAnimation(context.getContext(), R.anim.move_up);
					animation.setAnimationListener(FragmentDashboard.this);
					moveUp = true;
					moveDown = false;
					layout.setDrawingCacheEnabled(true);
					layout.startAnimation(animation);
					
					customAlert(message, 0);
				}
				if(message != null && message.equalsIgnoreCase("success")) {
					setmPieChart();
				}
			} catch(Exception e){
				
			}
			finally {
				setRefreshActionButtonState(false);
			}
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
				animation.setAnimationListener(FragmentDashboard.this);
				moveUp = false;
				moveDown = true;
				layout.startAnimation(animation);
			}
		});
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

	private void setmPieChart(){
		mPieChart.setUsePercentValues(true);

		// change the color of the center-hole
		// mPieChart.setHoleColor(Color.rgb(235, 235, 235));
		mPieChart.setHoleColorTransparent(true);

		tf = Typeface.createFromAsset(getActivity().getAssets(), Constants.ROBOTO_LIGHT);

		mPieChart.setCenterTextTypeface(tf);

		mPieChart.setHoleRadius(60f);

		mPieChart.setDescription("");

		mPieChart.setDrawCenterText(true);

		mPieChart.setDrawHoleEnabled(true);

		mPieChart.setRotationAngle(0);
		// enable rotation of the chart by touch
		mPieChart.setRotationEnabled(true);

		// mPieChart.setUnit(" €");
		// mPieChart.setDrawUnitsInChart(true);

		// add a selection listener
		mPieChart.setOnChartValueSelectedListener(this);
		// mPieChart.setTouchEnabled(false);

		mPieChart.setCenterText("Dashboard\nReport");

		setPieChartData(100);

		mPieChart.animateXY(1500, 1500);
		// mPieChart.spin(2000, 0, 360);

		Legend l = mPieChart.getLegend();
		l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
		l.setXEntrySpace(7f);
		l.setYEntrySpace(5f);
	}

	private void setPieChartData(float range) {

		float mult = range;

		ArrayList<Entry> yVals1 = new ArrayList<Entry>();

		// IMPORTANT: In a PieChart, no values (Entry) should have the same
		// xIndex (even if from different DataSets), since no values can be
		// drawn above each other.

		float total = MMRSingleTone.getInstance().noOfReportAssign;
		tvTotalAssignedReport.setVisibility(View.VISIBLE);
		tvTotalAssignedReport.setText("Total Report Assigned : "+(int)total);

		float noOfSubmittedReports = MMRSingleTone.getInstance().noOfReportSubmitted;
		float noOfPendingReports = MMRSingleTone.getInstance().noOfReportPending;
		float noOfRejectedReports = MMRSingleTone.getInstance().noOfReportRejected;
		float noOfExpiredReports = MMRSingleTone.getInstance().noOfReportExpired;

		int tvendSubmittedDegree = (int) ((noOfSubmittedReports / total) * 100f);
		int tvendPendingDegree = (int) ((noOfPendingReports / total) * 100f);
		int tvendRejectDegree = (int) ((noOfRejectedReports / total) * 100f);
		int tvendExpireDegree = (int) ((noOfExpiredReports / total) * 100f);

		yVals1.add(new Entry(tvendSubmittedDegree, 0));
		yVals1.add(new Entry(tvendPendingDegree, 1));
		yVals1.add(new Entry(tvendRejectDegree, 2));
		yVals1.add(new Entry(tvendExpireDegree, 3));

		ArrayList<String> xVals = new ArrayList<String>();

		mParties = new String[] {
				(int)noOfSubmittedReports+" Report Submitted  ",
				(int)noOfPendingReports+" Report Pending  ",
				(int)noOfRejectedReports+" Report Rejected  ",
				(int)noOfExpiredReports+" Report Expired  "
		};

		for (int i = 0; i < mParties.length; i++)
			xVals.add(mParties[i]);

		PieDataSet dataSet = new PieDataSet(yVals1, "");
		dataSet.setSliceSpace(3f);

		// add a lot of colors

		ArrayList<Integer> colors = new ArrayList<Integer>();

		int color1 = Color.rgb(149, 117, 205);
		int color2 = Color.rgb(121, 134, 203);
		int color3 = Color.rgb(100, 181, 246);
		int color4 = Color.rgb(77, 208, 225);

		colors.add(color1);
		colors.add(color2);
		colors.add(color3);
		colors.add(color4);

		colors.add(ColorTemplate.getHoloBlue());

		dataSet.setColors(colors);

		PieData data = new PieData(xVals, dataSet);
		data.setValueFormatter(new PercentFormatter());
		data.setValueTextSize(11f);
		data.setValueTextColor(Color.BLACK);
		data.setValueTypeface(tf);
		mPieChart.setData(data);

		// undo all highlights
		mPieChart.highlightValues(null);

		mPieChart.invalidate();
	}

	@SuppressLint("NewApi")
	@Override
	public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

		if (e == null)
			return;

	}

	@Override
	public void onNothingSelected() {
		Log.i("PieChart", "nothing selected");
	}

	@Override
	public void onClick(View view) {

	}
}
