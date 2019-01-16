package com.luminousinfoways.makemyreport.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.luminousinfoways.makemyreport.R;
import com.luminousinfoways.makemyreport.database.MMRDatabaseHelper;
import com.luminousinfoways.makemyreport.util.Constants;
import com.luminousinfoways.makemyreport.util.MMRSingleTone;
import com.luminousinfoways.makemyreport.util.Util;

public class MainActivity extends SherlockFragmentActivity {

	// Declare Variables
	DrawerLayout mDrawerLayout;
	ListView mDrawerList;
	ActionBarDrawerToggle mDrawerToggle;
	MenuListAdapter mMenuAdapter;
	String[] title;
	String[] subtitle;
	int[] icon;
	Fragment fragment0 = new FragmentProfile();
	Fragment fragment1 = new FragmentDashboard();
	//Fragment fragment1 = new FragmentAnalytics();
	Fragment fragment2 = new FragmentReportlist();
	Fragment fragment3 = new FragmentChangePassword();
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	
	private static int lastItemClicked = -1;

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Get the view from drawer_main.xml
		setContentView(R.layout.drawer_main);

		// Get the Title
		mTitle = mDrawerTitle = getTitle();

		String userName = getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 1).getString(Constants.SP_FULL_NAME, null);

		try{
			if(userName != null && userName.length() > 0) {
				userName = userName.trim();
				if (userName.contains(" ")) {
					String[] name = userName.split(" ");
					String usrName = "";
					if (name != null && name.length > 0) {
						for (int i = 0; i < name.length; i++) {
							String uName = name[i].replace(name[i].charAt(0), Character.toUpperCase(name[i].charAt(0)));
							usrName = usrName + uName + " ";
						}
						if (usrName != null && usrName.length() > 0) {
							userName = usrName;
						}
					}
				} else {
					userName = userName.replace(userName.charAt(0), Character.toUpperCase(userName.charAt(0)));
				}
			}
		} catch(Exception e){
			Log.e("MainActivity", "OnCreate()", e);
		}

		// Generate title
		if(userName != null)
			title = new String[] { userName ,"Dashboard", "Report List", "Change Password","", "Logout" };
		else
			title = new String[] {"" ,"Dashboard", "Report List", "Change Password","", "Logout" };
		// Generate subtitle
		if(userName != null)
			subtitle = new String[] { userName, "Dashboard", "Report List", "Change Password","", "Logout" };
		else
			subtitle = new String[] { "", "Dashboard", "Report List", "Change Password","", "Logout" };

		// Generate icon
		icon = new int[] { R.drawable.ic_launcher, R.drawable.ic_launcher,
				R.drawable.ic_launcher, R.drawable.ic_launcher,
				R.drawable.ic_launcher };

		// Locate DrawerLayout in drawer_main.xml
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		// Locate ListView in drawer_main.xml
		mDrawerList = (ListView) findViewById(R.id.listview_drawer);

		// Set a custom shadow that overlays the main content when the drawer
		// opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		// Pass string arrays to MenuListAdapter
		mMenuAdapter = new MenuListAdapter(MainActivity.this, title, subtitle,
				icon);

		// Set the MenuListAdapter to the ListView
		mDrawerList.setAdapter(mMenuAdapter);

		// Capture listview menu item click
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		// Enable ActionBar app icon to behave as action to toggle nav drawer
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.navigation_drawer_open,
				R.string.navigation_drawer_close) {

			public void onDrawerClosed(View view) {
				// TODO Auto-generated method stub
				super.onDrawerClosed(view);
			}

			public void onDrawerOpened(View drawerView) {
				// TODO Auto-generated method stub
				// Set the title on the action when drawer open
				getSupportActionBar().setTitle(mDrawerTitle);
				super.onDrawerOpened(drawerView);
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			lastItemClicked = 2;
			MMRSingleTone.getInstance().drawerMenuItemSelectedNo = 2;
			mMenuAdapter.notifyDataSetChanged();
			selectItem(2);
		}

		LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
		boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean statusOfInternet = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if(statusOfGPS == false && statusOfInternet == false) {
			showAlertDialog("Enable GPS to get current location.", "GPS Setting", "Setting", "Exit");
		}
	}

	/**
	 * To Show Material Alert Dialog
	 *
	 * @param message
	 * @param title
	 * */
	private void showAlertDialog(String message, String title, String positiveButtonText, String negativeButtonText){

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setCancelable(false);
		builder.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivity(intent);
			}
		});
		builder.setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
		builder.show();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev)
	{
		View v = getCurrentFocus();

		if (v != null &&
				(ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
				v instanceof EditText &&
				!v.getClass().getName().startsWith("android.webkit."))
		{
			int scrcoords[] = new int[2];
			v.getLocationOnScreen(scrcoords);
			float x = ev.getRawX() + v.getLeft() - scrcoords[0];
			float y = ev.getRawY() + v.getTop() - scrcoords[1];

			if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom()) {
				Util.hideKeyboard(this);
			}
		}
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == android.R.id.home) {

			if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
				mDrawerLayout.closeDrawer(mDrawerList);
			} else {
				mDrawerLayout.openDrawer(mDrawerList);
			}
		}

		return super.onOptionsItemSelected(item);
	}

	// ListView click listener in the navigation drawer
	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			/*if(position == 0){
				return;
			}*/
			
			if(position == 4){
				return;
			}
			
			if(lastItemClicked != position){
				lastItemClicked = position;
				MMRSingleTone.getInstance().drawerMenuItemSelectedNo = position;
				mMenuAdapter.notifyDataSetChanged();
				selectItem(lastItemClicked);
			} else{
				mDrawerList.setItemChecked(lastItemClicked, true);
				// Get the title followed by the position
				setTitle(title[position]);
				// Close drawer
				mDrawerLayout.closeDrawer(mDrawerList);
			}
		}
	}

	private void selectItem(int position) {

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		// Locate Position
		switch (position) {
			case 0:
				ft.replace(R.id.content_frame, fragment0);
				break;
			case 1:
				ft.replace(R.id.content_frame, fragment1);
				break;
			case 2:
				ft.replace(R.id.content_frame, fragment2);
				break;
			case 3:
				ft.replace(R.id.content_frame, fragment3);
				break;
			case 5:
				
				String api_key = getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 1).getString(Constants.SP_API_KEY, "");
				MMRDatabaseHelper databaseHelper = new MMRDatabaseHelper(MainActivity.this);
				databaseHelper.updateUserLogInStatusWhereApiKeyIS(api_key, 0);
				databaseHelper.close();
				
				MMRSingleTone.getInstance().isAlreadyRunReportAsyncBefore = false;
				MMRSingleTone.getInstance().isRunningReportAsynncTask = true;

				getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 2).edit().putString(Constants.SP_FULL_NAME, null).commit();
				getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 2).edit().putString(Constants.SP_API_KEY, null).commit();
				getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 2).edit().putString(Constants.SP_ORG_ID, null).commit();
				getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 2).edit().putString(Constants.SP_ORG_NAME, null).commit();

				Intent intent = new Intent(MainActivity.this, LoginActivity.class);
				startActivity(intent);
				finish();
				break;
		}
		ft.commit();
		mDrawerList.setItemChecked(position, true);
		// Get the title followed by the position
		setTitle(title[position]);
		// Close drawer
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggles
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		//getSupportActionBar().setTitle(mTitle);
	}

	@Override
	public void onBackPressed() {

		FragmentManager manager = getSupportFragmentManager();
		if (manager.getBackStackEntryCount() > 0) {
			// If there are back-stack entries, leave the FragmentActivity
			// implementation take care of them.
			manager.popBackStack();

		} else {
			// Otherwise, ask user if he wants to leave :)
			super.onBackPressed();
		}
	}
	
	@Override
	protected void onDestroy() {
		MMRSingleTone.getInstance().currentFragment = null;
		super.onDestroy();
	}
}
