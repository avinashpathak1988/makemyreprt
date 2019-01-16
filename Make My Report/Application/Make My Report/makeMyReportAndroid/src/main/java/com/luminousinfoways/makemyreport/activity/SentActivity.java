package com.luminousinfoways.makemyreport.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.luminousinfoways.makemyreport.R;
import com.luminousinfoways.makemyreport.util.MMRSingleTone;

public class SentActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_tab_report_submited);
		
		ListView listView1 = (ListView) findViewById(R.id.listView1);
		UserReportsAdapter adapter = new UserReportsAdapter(this, 
				MMRSingleTone.getInstance().submitedReport, 1);
		listView1.setAdapter(adapter);
	}
}
