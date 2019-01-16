package com.luminousinfoways.makemyreportandroid.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.luminousinfoways.makemyreportandroid.R;
import com.luminousinfoways.makemyreportandroid.pojo.Report;

import java.util.List;

public class UserReportsAdapter extends BaseAdapter {
	
	private List<Report> userReports;
	private Activity context;
	int tabNumber;
	
	static final int MIN_DISTANCE = 100;// TODO change this runtime based on screen resolution. for 1920x1080 is to small the 100 distance
    private float downX, downY, upX, upY;
    
	public UserReportsAdapter(Activity context, List<Report> userReports, int number){
		this.context = context;
		this.userReports = userReports;
		this.tabNumber = number;
	}

	@Override
	public int getCount() {
		return userReports.size();
	}

	@Override
	public Object getItem(int position) {
		return userReports.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.user_report_item_layout, null);
		}
		
		LinearLayout layoutParent = (LinearLayout) convertView.findViewById(R.id.layoutParent);
		layoutParent.setTag(position);
		
		TextView tvReportTitle = (TextView) convertView.findViewById(R.id.tvReportTitle);
		tvReportTitle.setTag(position);
		tvReportTitle.setText(userReports.get(position).getReportTitle());
		
		TextView tvReportCreatedBy = (TextView) convertView.findViewById(R.id.tvReportCreatedBy);
		tvReportCreatedBy.setTag(position);
		tvReportCreatedBy.setText("Assigned By : "+userReports.get(position).getReportCreatedBy());
		
		TextView tvReportCreatedOn = (TextView) convertView.findViewById(R.id.tvReportCreatedOn);
		tvReportCreatedOn.setTag(position);
		tvReportCreatedOn.setText(userReports.get(position).getLastDateofSubmission());
				
		TextView submitsts = (TextView) convertView.findViewById(R.id.submitsts);
		submitsts.setTag(position);
		
		TextView tvReportType = (TextView) convertView.findViewById(R.id.tvReportType);
		tvReportType.setText("Report Type : "+userReports.get(position).getReportType());
		
		//TextView btnSubmit = (TextView) convertView.findViewById(R.id.btnSubmit);
		ImageView imgNext = (ImageView) convertView.findViewById(R.id.imgNext);
				
		if(tabNumber == 0){
			
			submitsts.setTextColor(context.getResources().getColor(R.color.red));
			
			if(userReports.get(position).getReportType().equalsIgnoreCase("instant")) {
				submitsts.setVisibility(View.VISIBLE);
				String total = userReports.get(position).getNoOfTimesReportSubmission()+"";
				String pending = userReports.get(position).getNoOfTimesReportToBeSubmitted()+"";
				submitsts.setText(pending+"/"+total+" left");
			} else{
				//Weekly, Daily, Monthly
				submitsts.setVisibility(View.INVISIBLE);
			}
			
			if(userReports.get(position).getShowSubmitButton().equalsIgnoreCase("y")){
				imgNext.setVisibility(View.VISIBLE);
			} else{
				imgNext.setVisibility(View.INVISIBLE);
			}
			
			layoutParent.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					int position = (Integer) (v.getTag());
					Report report = userReports.get(position);
					
					if(report.getShowSubmitButton().equalsIgnoreCase("y")){
					
						String fb_form_id = report.getReportID()+"";

                       /* ReportFromDetailsActivity.mReportForm = null;
						Intent intent = new Intent(context, ReportFromDetailsActivity.class);
						intent.putExtra("fb_form_id", fb_form_id);
						intent.putExtra("no_of_times_report_submited", report.getNoOfTimesReportSubmitted());
						intent.putExtra("fb_assign_user_id", report.getFbAssignUserID());
						intent.putExtra("report_title", report.getReportTitle());
						context.startActivityForResult(intent, 1234);*/
					}
				}
			});	
			
		} else if(tabNumber == 1){

			imgNext.setVisibility(View.INVISIBLE);
			
			String total = userReports.get(position).getNoOfTimesReportSubmission()+"";
			String submitted = userReports.get(position).getNoOfTimesReportSubmitted()+"";
			submitsts.setText(submitted+"/"+total+" submitted");
		} else{
			submitsts.setVisibility(View.INVISIBLE);
			imgNext.setVisibility(View.INVISIBLE);
		}
		
		return convertView;
	}
	
}
