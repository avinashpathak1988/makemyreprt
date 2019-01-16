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

public class FragmentExpired extends Fragment{
	
	UserReportsAdapter adapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		MMRSingleTone.getInstance().currentReportCategoryShowing = 3;
		
		View rootView = inflater.inflate(R.layout.fragment_tab_report_pending, container,
				false);
		
		ListView listView1 = (ListView) rootView.findViewById(R.id.listView1);
		List<Report> reports = MMRSingleTone.getInstance().expierdReport;
		adapter = new UserReportsAdapter(getActivity(), 
				MMRSingleTone.getInstance().expierdReport, 2);
		adapter.notifyDataSetChanged();
		listView1.setAdapter(adapter);
		
		return rootView;
	}

	public void refreshUI() {
		adapter.notifyDataSetChanged();
	}
}
