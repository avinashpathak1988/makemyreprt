package com.luminousinfoways.makemyreport.activity;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.luminousinfoways.makemyreport.R;
import com.luminousinfoways.makemyreport.pojo.Report;
import com.luminousinfoways.makemyreport.util.MMRSingleTone;

public class FragmentSubmitted extends Fragment{
	
	UserReportsAdapter adapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		MMRSingleTone.getInstance().currentReportCategoryShowing = 2;
		
		View rootView = inflater.inflate(R.layout.fragment_tab_report_submited, container,
				false);		
		
		ListView listView1 = (ListView) rootView.findViewById(R.id.listView1);
		List<Report> reports = MMRSingleTone.getInstance().submitedReport;
		adapter = new UserReportsAdapter(getActivity(), 
				MMRSingleTone.getInstance().submitedReport, 1);
		adapter.notifyDataSetChanged();
		listView1.setAdapter(adapter);
		
		return rootView;
	}

	public void refreshUI() {
		adapter.notifyDataSetChanged();
	}
}
