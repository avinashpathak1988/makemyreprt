package com.luminousinfoways.makemyreport.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ViewPagerReportListAdapter extends FragmentPagerAdapter {

	// Declare the number of ViewPager pages
	final int PAGE_COUNT = 2;
	//private String titles[] = new String[] { "SUBMITTED", "PENDING", "REJECTED", "EXPIRED" };
	private String titles[] = new String[] { "Inbox", "Sent" };

	public ViewPagerReportListAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		switch (position) {

			// Open FragmentTab1.java
		case 0:
			FragmentSubmitted fragmenttab1 = new FragmentSubmitted();
			return fragmenttab1;

			// Open FragmentTab2.java
		case 1:
			FragmentPending fragmenttab = new FragmentPending();
			return fragmenttab;
		}
		return null;
	}

	public CharSequence getPageTitle(int position) {
		return titles[position];
	}

	@Override
	public int getCount() {
		return PAGE_COUNT;
	}

}