package com.luminousinfoways.makemyreport.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
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


	//Decodes image and scales it to reduce memory consumption
	public static Bitmap decodeFile(File f, int requiredSize){

		try {

			//Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			FileInputStream stream1=new FileInputStream(f);
			BitmapFactory.decodeStream(stream1, null, o);
			stream1.close();

			//Find the correct scale value. It should be the power of 2.

			// Set width/height of recreated image
			final int REQUIRED_SIZE = requiredSize;

			int width_tmp=o.outWidth, height_tmp=o.outHeight;
			int scale=1;
			while(true){
				if(width_tmp/2 < REQUIRED_SIZE || height_tmp/2 < REQUIRED_SIZE)
					break;
				width_tmp/=2;
				height_tmp/=2;
				scale*=2;
			}

			//decode with current scale values
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize=scale;
			FileInputStream stream2=new FileInputStream(f);
			Bitmap bitmap= BitmapFactory.decodeStream(stream2, null, o2);
			bitmap = rotateBitmap(bitmap, f.getAbsolutePath());
			stream2.close();
			return bitmap;

		} catch (FileNotFoundException e) {
		}
		catch (IOException e) {
			e.printStackTrace();
		} catch (Throwable ex){
			ex.printStackTrace();
			if(ex instanceof OutOfMemoryError)
				Log.e("Util", "decodeFile()", ex);
			return null;
		}
		return null;
	}

	private static Bitmap rotateBitmap(Bitmap bm, String path){
		ExifInterface exif;
		try {
			exif = new ExifInterface(path);

			String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
			int orientation = orientString != null ? Integer.parseInt(orientString) :  ExifInterface.ORIENTATION_NORMAL;

			int rotationAngle = 0;
			if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
			if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
			if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;

			if(bm == null)
				return null;

			Matrix matrix = new Matrix();
			matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
			Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
			return rotatedBitmap;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static int getScreenWidth(Context c) {

		int screenWidth = 0;
		WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		screenWidth = size.x;

		return screenWidth;
	}
}
