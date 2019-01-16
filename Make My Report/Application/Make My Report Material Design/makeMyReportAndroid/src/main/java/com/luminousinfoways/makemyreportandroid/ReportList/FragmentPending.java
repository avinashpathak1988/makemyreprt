package com.luminousinfoways.makemyreportandroid.ReportList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.luminousinfoways.makemyreportandroid.R;
import com.luminousinfoways.makemyreportandroid.adapter.UserReportsAdapter;
import com.luminousinfoways.makemyreportandroid.pojo.Report;
import com.luminousinfoways.makemyreportandroid.util.MMRSingleTone;

import java.util.List;

public class FragmentPending extends Fragment {
	
	UserReportsAdapter adapter;

	public static FragmentPending newInstance() {
		FragmentPending f = new FragmentPending();
		return f;
	}
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		MMRSingleTone.getInstance().currentReportCategoryShowing = 1;
		
		View rootView = inflater.inflate(R.layout.fragment_tab_report_pending, container,
				false);
		
		ListView listView1 = (ListView) rootView.findViewById(R.id.listView1);
		List<Report> reports = MMRSingleTone.getInstance().pendingReport;
		adapter = new UserReportsAdapter(getActivity(), 
				reports, 0);
		adapter.notifyDataSetChanged();
		listView1.setAdapter(adapter);
		return rootView;
	}

	public void refreshUI() {
		adapter.notifyDataSetChanged();
	}
}
