package com.luminousinfoways.makemyreport.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.luminousinfoways.makemyreport.R;
import com.luminousinfoways.makemyreport.util.MMRSingleTone;

public class MenuListAdapter extends BaseAdapter {

	// Declare Variables
	Context context;
	String[] mTitle;
	String[] mSubTitle;
	int[] mIcon;
	LayoutInflater inflater;

	public MenuListAdapter(Context context, String[] title, String[] subtitle,
			int[] icon) {
		this.context = context;
		this.mTitle = title;
		this.mSubTitle = subtitle;
		this.mIcon = icon;
	}

	@Override
	public int getCount() {
		return mTitle.length;
	}

	@Override
	public Object getItem(int position) {
		return mTitle[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		// Declare Variables
		TextView txtTitle;

		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View itemView = inflater.inflate(R.layout.drawer_list_item, parent,
				false);
		
		LinearLayout layoutDivider = (LinearLayout) itemView.findViewById(R.id.layoutDivider);
		if(position == 3 || position == 4 || position == 5){
			layoutDivider.setVisibility(View.INVISIBLE);
		} else{
			layoutDivider.setVisibility(View.VISIBLE);
		}
		
		LinearLayout selectedLayout = (LinearLayout) itemView.findViewById(R.id.selectedLayout);
		
		if(position == MMRSingleTone.getInstance().drawerMenuItemSelectedNo){
			selectedLayout.setVisibility(View.VISIBLE);
		} else{
			selectedLayout.setVisibility(View.INVISIBLE);
		}

		// Locate the TextViews in drawer_list_item.xml
		txtTitle = (TextView) itemView.findViewById(R.id.title);

		// Set the results into TextViews
		txtTitle.setText(mTitle[position]);
		
		if(position == 5){
			itemView.setBackgroundColor(context.getResources().getColor(R.color.red));
		}

		return itemView;
	}

}
