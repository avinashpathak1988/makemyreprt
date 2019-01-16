package com.luminousinfoways.makemyreport.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.luminousinfoways.makemyreport.R;

public class CustomNotificationActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.custom_notification_layout);
		
		RelativeLayout layoutBg = (RelativeLayout) findViewById(R.id.layoutBg);
		TextView tvMsg = (TextView) findViewById(R.id.tvMsg);
		TextView tvOk = (TextView) findViewById(R.id.tvOk);
		ImageView img = (ImageView) findViewById(R.id.img);
		String message = getIntent().getExtras().getString("AlertDialogMessage");
		tvMsg.setText(message);		
		
		int status = getIntent().getExtras().getInt("status");
		if(status == 0){
			img.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_remove));
			layoutBg.setBackgroundColor(getResources().getColor(R.color.red));
		} else if(status == 1){
			img.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_accept));
			layoutBg.setBackgroundColor(getResources().getColor(R.color.login_btn_color_pressed));
		}
		
		tvOk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}
