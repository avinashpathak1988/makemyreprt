package com.luminousinfoways.makemyreport.activity;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.luminousinfoways.makemyreport.R;
import com.luminousinfoways.makemyreport.pojo.Report;
import com.luminousinfoways.makemyreport.util.MMRSingleTone;

public class InboxActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_tab_report_pending);
		
		ListView listView1 = (ListView) findViewById(R.id.listView1);
		List<Report> reports = MMRSingleTone.getInstance().pendingReport;
		UserReportsAdapter adapter = new UserReportsAdapter(this, 
				reports, 0);
		listView1.setAdapter(adapter);
	}
}
