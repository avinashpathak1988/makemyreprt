package com.luminousinfoways.makemyreportandroid.util;

import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.inputmethod.InputMethodManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Util {
	
	private static final String TAG = "Util";
	
	public static boolean isConnected(Context context){
		
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
		for (NetworkInfo networkInfo : networkInfos) {
			if(networkInfo.isConnected()){
				return true;
			}
		}
		
		return false;
	}
	
	public static int getCurrentYear(){
		
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		
		return year;
	}
	
	public static int getCurrentMonth(){
		
		Calendar calendar = Calendar.getInstance();
		int month = calendar.get(Calendar.MONTH);
		
		return month;
	}

	public static int geCurrentDay(){
	
		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		
		return day;
	}

	public static void hideKeyboard(Activity activity)
	{
		if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null)
		{
			InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
		}
	}

	public static boolean isJSONValid(String test) {
		try {
			new JSONObject(test);
		} catch (JSONException ex) {
			try {
				new JSONArray(test);
			} catch (JSONException ex1) {
				return false;
			}
		}
		return true;
	}
}
