package com.luminousinfoways.makemyreport.activity;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.luminousinfoways.makemyreport.R;

public class DropDownListAdapter extends BaseAdapter {
	
	private Context context;
	private List<String> options;
	
	public DropDownListAdapter(Context context, List<String> objects) {
		this.context = context;
		this.options = objects;
	}
	
	@Override
	public int getCount() {
		return options.size();
	}

	@Override
	public Object getItem(int position) {
		return options.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.dropdownlist_item_layout, null);
		}
		
		TextView tvOption = (TextView) convertView.findViewById(R.id.tvDropDownOption);
		tvOption.setText(options.get(position));
		
		Log.i("", "");
		
		return convertView;
	}

}
