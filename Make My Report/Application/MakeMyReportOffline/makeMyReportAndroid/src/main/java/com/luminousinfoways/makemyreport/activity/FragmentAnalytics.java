package com.luminousinfoways.makemyreport.activity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.ArcShape;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.luminousinfoways.makemyreport.util.Constants;
import com.luminousinfoways.makemyreport.util.MMRSingleTone;
import com.luminousinfoways.makemyreport.util.Util;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class FragmentAnalytics extends SherlockFragment implements AnimationListener {
			
	LinearLayout layoutProgress;
	SmoothProgressBar mPocketBar;

	Animation animation;
	RelativeLayout layout;
	boolean moveUp = false;
	boolean moveDown = false;
	LinearLayout layoutTransparent;
	
	FragmentTransaction fragmentTransaction;

	private Menu optionsMenu;
	private boolean is_refreshing = false;
	
	ImageView imageViewPendingSquare, imageViewSubmittedSquare, 
	imageViewLateSubmissionSquare, imageViewRejectSquare, imageViewExpireSquare;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		MMRSingleTone.getInstance().currentFragment = FragmentAnalytics.this;
		
		View rootView = inflater.inflate(R.layout.fragment_analytics, container,
				false);
		
		layoutTransparent = (LinearLayout) rootView.findViewById(R.id.layoutTransparent);
		
		layoutProgress = (LinearLayout) rootView.findViewById(R.id.layoutProgress);
		 mPocketBar = (SmoothProgressBar) rootView.findViewById(R.id.google_now);
		 
		 imageViewPendingSquare = (ImageView) rootView.findViewById(R.id.imageViewPendingSquare);
		 imageViewSubmittedSquare = (ImageView) rootView.findViewById(R.id.imageViewSubmittedSquare);
		 imageViewRejectSquare = (ImageView) rootView.findViewById(R.id.imageViewRejectSquare);
		 imageViewLateSubmissionSquare = (ImageView) rootView.findViewById(R.id.imageViewLateSubmissionSquare);
		 imageViewExpireSquare = (ImageView) rootView.findViewById(R.id.imageViewExpireSquare);
		 
		 imageViewPendingSquare.setVisibility(View.INVISIBLE);
		 imageViewSubmittedSquare.setVisibility(View.INVISIBLE);
		 imageViewLateSubmissionSquare.setVisibility(View.INVISIBLE);	
		 imageViewRejectSquare.setVisibility(View.INVISIBLE);	
		 imageViewExpireSquare.setVisibility(View.INVISIBLE);	
		 		
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
				animation.setAnimationListener(FragmentAnalytics.this);
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
			
			imageViewPendingSquare.setVisibility(View.INVISIBLE);
			 imageViewSubmittedSquare.setVisibility(View.INVISIBLE);
			 imageViewLateSubmissionSquare.setVisibility(View.INVISIBLE);	
			 imageViewRejectSquare.setVisibility(View.INVISIBLE);	
			 imageViewExpireSquare.setVisibility(View.INVISIBLE);	
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
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			try {
				
				mPocketBar.progressiveStop();
				layoutProgress.setVisibility(View.GONE);
				
				imageViewPendingSquare.setVisibility(View.VISIBLE);
				imageViewSubmittedSquare.setVisibility(View.VISIBLE);
				imageViewLateSubmissionSquare.setVisibility(View.VISIBLE);	
				imageViewRejectSquare.setVisibility(View.VISIBLE);	
				imageViewExpireSquare.setVisibility(View.VISIBLE);	
				
				if(!message.equalsIgnoreCase("success")){
					layout = (RelativeLayout) getView().findViewById(R.id.layoutBg);
					animation = AnimationUtils.loadAnimation(context.getContext(), R.anim.move_up);
					animation.setAnimationListener(FragmentAnalytics.this);
					moveUp = true;
					moveDown = false;
					layout.setDrawingCacheEnabled(true);
					layout.startAnimation(animation);
					
					customAlert(message, 0);
				}
				
				setDashboard(context);
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
				animation.setAnimationListener(FragmentAnalytics.this);
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
	
	private void setDashboard(View rootView){
		int widthAndHeightOfCircle = (int) (getResources().getDisplayMetrics().widthPixels * 0.65f);		
		/*
		 float total = MMRSingleTone.getInstance().noOfTotalReports;
		 float noOfSubmittedReports = MMRSingleTone.getInstance().noOfSubmittedReports;
		float noOfPendingReports = MMRSingleTone.getInstance().noOfPendingReports;
		float noOfLateSubmissionReports = MMRSingleTone.getInstance().noOfLateSubmissionReports;*/
		//TODO remove below and uncomment above
		
		float total = MMRSingleTone.getInstance().noOfReportAssign;
		float noOfSubmittedReports = MMRSingleTone.getInstance().noOfReportSubmitted;
		float noOfPendingReports = MMRSingleTone.getInstance().noOfReportPending;
		float noOfLateSubmissionReports = 0;//TODO
		float noOfRejectedReports = MMRSingleTone.getInstance().noOfReportRejected;
		float noOfExpiredReports = MMRSingleTone.getInstance().noOfReportExpired;
		
		int startSubmittedDegree = -90;
		int endSubmittedDegree = (int) ((noOfSubmittedReports / total) * 360f);
		
		ArcShape arcShapeSubmitted = new ArcShape(startSubmittedDegree, endSubmittedDegree);
        ShapeDrawable submitted = new ShapeDrawable(arcShapeSubmitted);
        submitted.setIntrinsicHeight(widthAndHeightOfCircle);
        submitted.setIntrinsicWidth(widthAndHeightOfCircle);
        submitted.getPaint().setColor(getResources().getColor(R.color.login_btn_color_pressed));
        ImageView iViewSubmitted = (ImageView) rootView.findViewById(R.id.imageViewSubmitted);
        iViewSubmitted.setImageDrawable(submitted);
		
		int startPendingDegree = -90 + endSubmittedDegree;
		int endPendingDegree = (int) ((noOfPendingReports / total) * 360f);	
		
		ArcShape arcShapePending = new ArcShape(startPendingDegree, endPendingDegree);
        ShapeDrawable pending = new ShapeDrawable(arcShapePending);
        pending.setIntrinsicHeight(widthAndHeightOfCircle);
        pending.setIntrinsicWidth(widthAndHeightOfCircle);
        pending.getPaint().setColor(getResources().getColor(R.color.indigo));
        ImageView iViewPending = (ImageView) rootView.findViewById(R.id.imageViewPending);
        iViewPending.setImageDrawable(pending);
		
		int startLateSubmissionDegree = -90 + endSubmittedDegree + endPendingDegree;
		int endLateSubmissionDegree = (int) ((noOfLateSubmissionReports / total) * 360f);	
		
		ArcShape arcShapeLateSubmission = new ArcShape(startLateSubmissionDegree, endLateSubmissionDegree);
        ShapeDrawable lateSubmission = new ShapeDrawable(arcShapeLateSubmission);
        lateSubmission.setIntrinsicHeight(widthAndHeightOfCircle);
        lateSubmission.setIntrinsicWidth(widthAndHeightOfCircle);
        lateSubmission.getPaint().setColor(Color.RED);
        ImageView imageViewLateSubmission = (ImageView) rootView.findViewById(R.id.imageViewLateSubmission);
        imageViewLateSubmission.setImageDrawable(lateSubmission);
        
        int startRejectDegree = -90 + endSubmittedDegree + endPendingDegree + endLateSubmissionDegree;
		int endRejectDegree = (int) ((noOfRejectedReports / total) * 360f);	
		
		ArcShape arcShapeReject = new ArcShape(startRejectDegree, endRejectDegree);
        ShapeDrawable reject = new ShapeDrawable(arcShapeReject);
        reject.setIntrinsicHeight(widthAndHeightOfCircle);
        reject.setIntrinsicWidth(widthAndHeightOfCircle);
        reject.getPaint().setColor(Color.BLACK);
        ImageView imageViewReject = (ImageView) rootView.findViewById(R.id.imageViewReject);
        imageViewReject.setImageDrawable(reject);
        
        int startExpireDegree = -90 + endSubmittedDegree + endPendingDegree + endLateSubmissionDegree + endRejectDegree;
		int endExpireDegree = (int) ((noOfExpiredReports / total) * 360f);	
		
		ArcShape arcShapeExpire = new ArcShape(startExpireDegree, endExpireDegree);
        ShapeDrawable expire = new ShapeDrawable(arcShapeExpire);
        expire.setIntrinsicHeight(widthAndHeightOfCircle);
        expire.setIntrinsicWidth(widthAndHeightOfCircle);
        expire.getPaint().setColor(Color.WHITE);
        ImageView imageViewExpire = (ImageView) rootView.findViewById(R.id.imageViewExpired);
        imageViewExpire.setImageDrawable(expire);
        
        int tvendSubmittedDegree = (int) ((noOfSubmittedReports / total) * 100f);
        int tvendPendingDegree = (int) ((noOfPendingReports / total) * 100f);	
        int tvendLateSubmissionDegree = (int) ((noOfLateSubmissionReports / total) * 100f);	
        int tvendRejectDegree = (int) ((noOfRejectedReports / total) * 100f);	
        int tvendExpireDegree = (int) ((noOfExpiredReports / total) * 100f);	
        
        TextView tvSubmittedSquare = (TextView) rootView.findViewById(R.id.tvSubmittedSquare);
        tvSubmittedSquare.setText(tvendSubmittedDegree+"% Report Submitted");
        
        TextView tvPendingSquare = (TextView) rootView.findViewById(R.id.tvPendingSquare);
        tvPendingSquare.setText(tvendPendingDegree+"% Report Pending");
        
        TextView tvLateSubmissionSquare = (TextView) rootView.findViewById(R.id.tvLateSubmissionSquare);
        tvLateSubmissionSquare.setText(tvendLateSubmissionDegree+"% Report Late Submitted");
        
        TextView tvReject = (TextView) rootView.findViewById(R.id.tvReject);
        tvReject.setText(tvendRejectDegree+"% Report Rejected");
        
        TextView tvExpire = (TextView) rootView.findViewById(R.id.tvExpire);
        tvExpire.setText(tvendExpireDegree+"% Report Expired");
	}

}
