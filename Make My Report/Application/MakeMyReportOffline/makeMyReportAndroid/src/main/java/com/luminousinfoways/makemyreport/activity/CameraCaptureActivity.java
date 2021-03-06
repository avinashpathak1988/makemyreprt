package com.luminousinfoways.makemyreport.activity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.luminousinfoways.makemyreport.R;
import com.luminousinfoways.makemyreport.pojo.FieldDetails;
import com.luminousinfoways.makemyreport.pojo.Options;
import com.luminousinfoways.makemyreport.pojo.ReportForm;
import com.luminousinfoways.makemyreport.util.Constants;
import com.luminousinfoways.makemyreport.util.DynamicForm;
import com.luminousinfoways.makemyreport.util.GPSTracker;
import com.luminousinfoways.makemyreport.util.MMRSingleTone;
import com.luminousinfoways.makemyreport.util.MultipartUtility;
import com.luminousinfoways.makemyreport.util.Util;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class CameraCaptureActivity extends SherlockFragmentActivity implements AnimationListener {
	
	SmoothProgressBar mPocketBar;
	LinearLayout lProLayout;
	
	int TAKE_PHOTO_CODE =20202;
	int IMAGE_CAPTURE = 12122;
	public static File PhotoToBeUpload = null;
	public static File CapturedImgFile = null;
	public static ArrayList<String> GalleryList = new ArrayList<String>();

	public static Uri CurrentUri = null;
	public static String sFilePath = ""; 
	LinearLayout layoutRemoveChange;

	
	RelativeLayout mainL;
	ImageView imgPhoto;
	TextView btnSubmit;

	//TextView tvaddress;
	
	GPSTracker gps;
	static double lattitude = 0.0f;
	static double longitude = 0.0f;
    private String address;
    private String postalCode;
	
	Animation animation;
	RelativeLayout layout;
	boolean moveUp = false;
	boolean moveDown = false;
	LinearLayout layoutTransparent;

	TextView btnRemovePic;
	TextView btnCahngePic;

	private static String userResponseId;
	private static String title;
	private static String no_of_times_report_submited;

	boolean isUserLeavingPage = false;
	private boolean isAnyNetworkOperationIsProcessing = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.capture_image_layout);
		
		MMRSingleTone.getInstance().context = this;
		
		layoutTransparent = (LinearLayout) findViewById(R.id.layoutTransparent);
		
		lProLayout = (LinearLayout) findViewById(R.id.layoutProgress);
		mPocketBar = (SmoothProgressBar) findViewById(R.id.google_now);
		mainL = (RelativeLayout) findViewById(R.id.mainL);
		//tvaddress = (TextView) findViewById(R.id.address);
	    
	    userResponseId = getIntent().getExtras().getString("userResponseId");
		
		title = getIntent().getExtras().getString("report_title");
		TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setText(Html.fromHtml("<b>"+title+"</b><br> Take Picture(Optional)"));
		
		no_of_times_report_submited = getIntent().getExtras().getString("no_of_times_report_submited");
		TextView tvNo_of_times_report_submited = (TextView) findViewById(R.id.no_of_times_report_submited);
		tvNo_of_times_report_submited.setText(no_of_times_report_submited);

		layoutRemoveChange = (LinearLayout) findViewById(R.id.layoutRemoveChange);
		imgPhoto = (ImageView) findViewById(R.id.imgPhoto);
		imgPhoto.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startCamera();			
			}
		});
		
		btnRemovePic = (TextView) findViewById(R.id.btnRemovePic);
		btnRemovePic.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				imgPhoto.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_camera));
				layoutRemoveChange.setVisibility(View.GONE);
				CapturedImgFile = null;
			}
		});
		btnCahngePic = (TextView) findViewById(R.id.btnCahngePic);
		btnCahngePic.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startCamera();
			}
		});
		
		btnSubmit = (TextView) findViewById(R.id.btnSubmit);
		btnSubmit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startSubmitReportAsync();
				//ReportEntrySubmissionAsyncTask asyncTask = new ReportEntrySubmissionAsyncTask();
				//asyncTask.execute();
			}
		});
		
		/*mainL.setEnabled(false);
		imgPhoto.setEnabled(false);
		btnSubmit.setEnabled(false);
		lProLayout.setVisibility(View.VISIBLE);
		mPocketBar.progressiveStart();*/

		Log.i("Before Location", "Lat"+lattitude+"\nLong : "+longitude);

		startService(new Intent(getBaseContext(), GPSTracker.class));
		
	}

    public void showAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                CameraCaptureActivity.this);
        alertDialog.setTitle("SETTINGS");
        alertDialog.setMessage("Enable Location Provider! Go to settings menu?");
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        CameraCaptureActivity.this.startActivity(intent);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		alertDialog.show();
    }
	
	@Override
	protected void onDestroy() {
		if(isUserLeavingPage) {
			CapturedImgFile = null;
			stopService(new Intent(getBaseContext(), GPSTracker.class));
		}
		super.onDestroy();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if((CapturedImgFile != null) && 
				(CapturedImgFile.getPath() != null) && 
				(CapturedImgFile.getPath().length() > 0) ){
			ImageView imageView1 = (ImageView) findViewById(R.id.imgPhoto);
			BitmapFactory.Options options = new BitmapFactory.Options();
            // downsizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 2;
            final Bitmap bitmap = BitmapFactory.decodeFile(CapturedImgFile.getPath(),
                    options);
			imageView1.setImageBitmap(bitmap);
			if(layoutRemoveChange == null) layoutRemoveChange = (LinearLayout) findViewById(R.id.layoutRemoveChange);
			layoutRemoveChange.setVisibility(View.VISIBLE);

			FileOutputStream out = null;
			String filename = CapturedImgFile.getName();
			try {
				out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
				bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void startCamera() {
		
	   Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	   // Specify the output. This will be unique.
	   setsFilePath(getTempFileString());
	   //
	   intent.putExtra(MediaStore.EXTRA_OUTPUT, CurrentUri);
	   //
	   // Keep a list for afterwards
	   FillPhotoList();
	   //
	   // finally start the intent and wait for a result.
	   startActivityForResult(intent, IMAGE_CAPTURE);
	}

	private void FillPhotoList()
	{
	   // initialize the list!
	   GalleryList.clear();
	   String[] projection = { MediaStore.Images.ImageColumns.DISPLAY_NAME };
	   // intialize the Uri and the Cursor, and the current expected size.
	   Cursor c = null;
	   Uri u = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
	   //
	   // Query the Uri to get the data path.  Only if the Uri is valid.
	   if (u != null)
	   {
		  c = managedQuery(u, projection, null, null, null);
	   }

	   // If we found the cursor and found a record in it (we also have the id).
	   if ((c != null) && (c.moveToFirst()))
	   {
		  do
		  {
			// Loop each and add to the list.
			GalleryList.add(c.getString(0));
		  }
		  while (c.moveToNext());
	   }
	}

	private String getTempFileString()
	{
	   // Only one time will we grab this location.
	   final File path = new File(Environment.getExternalStorageDirectory(),
			 getString(getApplicationInfo().labelRes));
	   //
	   // If this does not exist, we can create it here.
	   if (!path.exists())
	   {
		  path.mkdir();
	   }
	   //
	   return new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg").getPath();
	}

	public void setsFilePath(String value)
	{
	   // We just updated this value. Set the property first.
	   sFilePath = value;
	   //
	   // initialize these two
	   PhotoToBeUpload = null;
	   CurrentUri = null;
	   //
	   // If we have something real, setup the file and the Uri.
	   if (!sFilePath.equalsIgnoreCase(""))
	   {
		  PhotoToBeUpload = new File(sFilePath);
		  CurrentUri = Uri.fromFile(PhotoToBeUpload);
	   }
	}

	public void openCamera(){
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI.getPath());
		startActivityForResult(intent, TAKE_PHOTO_CODE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == IMAGE_CAPTURE)
		   {
			  // based on the result we either set the preview or show a quick toast splash.
			  if (resultCode == android.app.Activity.RESULT_OK)
			  {
				 // This is ##### ridiculous.  Some versions of Android save
				 // to the MediaStore as well.  Not sure why!  We don't know what
				 // name Android will give either, so we get to search for this
				 // manually and remove it.
				 String[] projection = { MediaStore.Images.ImageColumns.SIZE,
										 MediaStore.Images.ImageColumns.DISPLAY_NAME,
										 MediaStore.Images.ImageColumns.DATA,
										 BaseColumns._ID};
				 //
				 // intialize the Uri and the Cursor, and the current expected size.
				 Cursor c = null;
				 Uri u = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				 //
				 if (PhotoToBeUpload != null)
				 {
					// Query the Uri to get the data path.  Only if the Uri is valid,
					// and we had a valid size to be searching for.
					if ((u != null) && (PhotoToBeUpload.length() > 0))
					{
					   c = managedQuery(u, projection, null, null, null);
					}
					//
					// If we found the cursor and found a record in it (we also have the size).
					if ((c != null) && (c.moveToFirst()))
					{
					   do
					   {
						  // Check each area in the gallary we built before.
						  boolean bFound = false;

						  for (String sGallery : GalleryList)
						  {
							 if (sGallery.equalsIgnoreCase(c.getString(1)))
							 {
								bFound = true;
								break;
							 }
						  }
						  //
						  // To here we looped the full gallery.
						  if (!bFound)
						  {
							 // This is the NEW image.  If the size is bigger, copy it.
							 // Then delete it!
							 File f = new File(c.getString(2));

							 // Ensure it's there, check size, and delete!
							 if ((f.exists()) && (PhotoToBeUpload.length() < c.getLong(0)) && (PhotoToBeUpload.delete()))
							 {
								// Finally we can stop the copy.
								try
								{
								   PhotoToBeUpload.createNewFile();
								   FileChannel source = null;
								   FileChannel destination = null;
								   try
								   {
									  source = new FileInputStream(f).getChannel();
									  destination = new FileOutputStream(PhotoToBeUpload).getChannel();
									  destination.transferFrom(source, 0, source.size());
								   }
								   finally
								   {
									  if (source != null)
									  {
										 source.close();
									  }
									  if (destination != null)
									  {
										 destination.close();
									  }
								   }
								}
								catch (IOException e)
								{
								   // Could not copy the file over.
								   Toast.makeText(this, "ErrorOccured", Toast.LENGTH_SHORT).show();
								}
							 }
							 //
							 ContentResolver cr = getContentResolver();
							 cr.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
								BaseColumns._ID + "=" + c.getString(3), null);
							 break;
						  }
					   } while (c.moveToNext());
					}
				 }
			  }

			  if(PhotoToBeUpload.exists()){
				  CapturedImgFile = PhotoToBeUpload;
			  }

		   }
		super.onActivityResult(requestCode, resultCode, data);
	}
		
		
	
	private class ReportEntrySubmissionAsyncTask extends AsyncTask<Void, Void, Void>{
		
			String TAG = "ReportEntrySubmissionAsyncTask";
		
			String submitStatus;
			String submitMessage;
			int snapFound = 0;
			int resVal = -1;
			
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				lProLayout.setVisibility(View.VISIBLE);
				mPocketBar.progressiveStop();
				mPocketBar.progressiveStart();
			}
			
			@Override
			protected Void doInBackground(Void... params) {
				
				String orgID = getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 1).getString(Constants.SP_ORG_ID, null);
				if(orgID == null)
					return null;
				String apikey = getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 1).getString(Constants.SP_API_KEY, null);
				if(apikey == null)
					return null;
				
				String charset = "UTF-8";
		        String requestURL = Constants.URL+Constants.LOCATION_SNAP_SUBMIT+orgID;
		 
		        try {
		            MultipartUtility multipart = new MultipartUtility(requestURL, charset, apikey);
		            	            
					multipart.addFormField("fb_user_resp_id", userResponseId);
					multipart.addFormField("lattitude", lattitude+""); //TODO Send address
					multipart.addFormField("longitude", longitude+""); //TODO Send Pincode
                    //multipart.addFormField("lattitude", address); //TODO Send address
                    //multipart.addFormField("longitude", postalCode); //TODO Send Pincode
					if(CapturedImgFile != null && CapturedImgFile.exists())
						multipart.addFilePart("field_snap", CapturedImgFile);					
		 
		            List<String> response = multipart.finish();
		             
		            System.out.println("SERVER REPLIED:");
		            String res = ""; 
		            for (String line : response) {
		                res = res + line + "\n";
		            }
		            Log.i(TAG, res);
		            
		            /* 				
						{
							"Response":[
								{
									"Response":
										{
											"submitStatus":"Success",
											"Message":"No snap found"
										}
								}
								]
						}


		             */

		            
		            JSONArray jsonArray;
					JSONObject jObject;
					try {
						jObject = new JSONObject(res);
						jsonArray = jObject.getJSONArray("Response");
						for (int index = 0; index < jsonArray.length(); index++) {
							JSONObject obj = jsonArray.getJSONObject(index).getJSONObject("Response");
							submitStatus = obj.getString("submitStatus");
							submitMessage = obj.getString("Message");
							if(submitStatus.equalsIgnoreCase("Success")){
								resVal = 1;
							} else{
								resVal = 0;
							}
							if(submitMessage.equalsIgnoreCase("No snap found")){
								snapFound = 0;
							} else{
								snapFound = 1;
							}
						}
					} catch (JSONException e) {
						Log.e(TAG, "doInBackground()", e);
					}
		            	             
		        } catch(ConnectTimeoutException e){
		        	resVal = 3;
		        }catch (IOException ex) {
		            System.err.println(ex);
		        }
				
				return null;
			}
			
			@Override
			protected void onPostExecute(final Void result) {
				super.onPostExecute(result);
				lProLayout.setVisibility(View.INVISIBLE);
				mPocketBar.progressiveStop();
				
				layout = (RelativeLayout) findViewById(R.id.layoutBg);
				animation = AnimationUtils.loadAnimation(CameraCaptureActivity.this, R.anim.move_up);
				animation.setAnimationListener(CameraCaptureActivity.this);
				moveUp = true;
				moveDown = false;
				layout.setDrawingCacheEnabled(true);
				layout.startAnimation(animation);
				
				if(resVal == 1){
					if(snapFound == 1)
						customAlert("Submitted successfully with picture.", 1, false, false);
					else
						customAlert("Submitted successfully without picture.", 1, false, false);
				} else if(resVal != -1 && resVal == 0){
					customAlert("Submission failed.", 0, false, true);
				} else if(resVal ==3){
					customAlert("Connection time out.\nPlease reset internet connection.", 0, false, true);
				}else{
					customAlert("No internet connection.", 0, false, true);
				}
			}
		}	

    public void availableLocation(String _address, String _postalCode, double lat, double lon){
        lattitude = lat;
        longitude = lon;
		address = _address;
		postalCode = _postalCode;

		//tvaddress.setVisibility(View.VISIBLE);
		//tvaddress.setText(address+"\n"+postalCode);

       // Toast.makeText(CameraCaptureActivity.this, "Address : "+address+"\n"+"Postal Code : "+postalCode, Toast.LENGTH_SHORT ).show();

		/*lProLayout.setVisibility(View.INVISIBLE);
		mPocketBar.progressiveStop();
		mainL.setEnabled(true);
		imgPhoto.setEnabled(true);
		btnSubmit.setEnabled(true);*/
    }
	
	private void customAlert(String message, final int status, final boolean is_gps, final boolean isSubmitFailed){

		layout = (RelativeLayout) findViewById(R.id.layoutBg);
		animation = AnimationUtils.loadAnimation(CameraCaptureActivity.this, R.anim.move_up);
		animation.setAnimationListener(CameraCaptureActivity.this);
		moveUp = true;
		moveDown = false;
		layout.setDrawingCacheEnabled(true);
		layout.startAnimation(animation);

		TextView tvMsg = (TextView) findViewById(R.id.tvMsg);
		TextView tvOk = (TextView) findViewById(R.id.tvOk);
		ImageView img = (ImageView) findViewById(R.id.img);
		tvMsg.setText(message);

		if(layout == null){
			return;
		}
		
		if(status == 0){
			img.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_remove));
			layout.setBackgroundColor(getResources().getColor(R.color.red));
		} else if(status == 1){
			img.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_accept));
			layout.setBackgroundColor(getResources().getColor(R.color.login_btn_color_pressed));
		}
		
		tvOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(is_gps){
					Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	            	startActivity(intent);
				} else{							
					if(status == 1){
						MMRSingleTone.getInstance().isBackFromSubmit = true;
						MMRSingleTone.getInstance().mReportForm = null;
						isUserLeavingPage = true;
						finish();
					} else if(isSubmitFailed == true){
						Intent intent = new Intent(CameraCaptureActivity.this, ReportFromDetailsActivity.class);
						intent.putExtra("no_of_times_report_submited", no_of_times_report_submited);
						intent.putExtra("report_title", title);
						intent.putExtra("userResponseId", userResponseId);
						intent.putExtra("isSubmitFailed", true);
						startActivity(intent);
						isUserLeavingPage = true;
						finish();
					} else{
						layout = (RelativeLayout) findViewById(R.id.layoutBg);
						animation = AnimationUtils.loadAnimation(CameraCaptureActivity.this, R.anim.move_down);
						animation.setAnimationListener(CameraCaptureActivity.this);
						moveUp = false;
						moveDown = true;
						layout.startAnimation(animation);
					}
				}
			}
		});
	}
	
	@Override
	public void onBackPressed() {
		if(isAnyNetworkOperationIsProcessing == false) {
			MMRSingleTone.getInstance().isBackFromSubmit = true;
			isUserLeavingPage = true;
			super.onBackPressed();
		}
	}
	
	public void showSettingsAlert(){
		layout = (RelativeLayout) findViewById(R.id.layoutBg);
		animation = AnimationUtils.loadAnimation(CameraCaptureActivity.this, R.anim.move_up);
		animation.setAnimationListener(CameraCaptureActivity.this);
		moveUp = true;
		moveDown = false;
		layout.setDrawingCacheEnabled(true);
		layout.startAnimation(animation);
		
		customAlert("GPS is not enabled. Do you want to go to settings menu?", 0, true, false);
	}
	

	@Override
	public void onAnimationStart(Animation animation) {
		animation = new TranslateAnimation(0.0f, 0.0f, 0.0f, 0.0f);
        animation.setDuration(1);
        layoutTransparent.startAnimation(animation);
		if(moveUp)
			layout.setVisibility(View.VISIBLE);
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		if(moveDown){
			moveDown = false;
			moveUp = false;
			layout.setVisibility(View.GONE);
			layout.setDrawingCacheEnabled(false);
		} 
		
		animation = new TranslateAnimation(0.0f, 0.0f, 0.0f, 0.0f);
        animation.setDuration(1);
        layoutTransparent.startAnimation(animation);
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		
	}

	public void startSubmitReportAsync(){
		ReportForm mReportForm = MMRSingleTone.getInstance().mReportForm;
		if(mReportForm != null) {
			ReportSubmissionAsyncTask asyncTask = new ReportSubmissionAsyncTask(mReportForm);
			asyncTask.execute();
		} else{
			customAlert("Internal error occured.", 0, false, false);
		}
	}

	private class ReportSubmissionAsyncTask extends AsyncTask<Void, Void, Boolean>{

		String TAG = "ReportEntrySubmissionAsyncTask";
		String userResponseId = null;
		String submitMessage = null;
		ReportForm mReportForm = null;

		public ReportSubmissionAsyncTask(ReportForm mReportForm ){
			this.mReportForm = mReportForm;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			lProLayout.setVisibility(View.VISIBLE);
			btnSubmit.setEnabled(false);
			btnCahngePic.setEnabled(false);
			btnRemovePic.setEnabled(false);
			imgPhoto.setEnabled(false);
			mPocketBar.progressiveStop();
			mPocketBar.progressiveStart();
			isAnyNetworkOperationIsProcessing = true;
		}

		@Override
		protected Boolean doInBackground(Void... params) {

			String orgID = getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 1).getString(Constants.SP_ORG_ID, null);
			if(orgID == null)
				return null;
			String apikey = getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 1).getString(Constants.SP_API_KEY, null);
			if(apikey == null)
				return null;

			String charset = "UTF-8";
			String requestURL = Constants.URL+Constants.REPORT_ENTRY_SUBMIT+orgID;

			try {
				MultipartUtility multipart = new MultipartUtility(requestURL, charset, apikey);

				for(int i = 0; i < mReportForm.getFormFieldList().size(); i++){
					multipart.addFormField("orderElement[] : ", mReportForm.getFormFieldList().get(i).getFieldId()+"");
					Log.i("Result", "orderElement[] : "+ mReportForm.getFormFieldList().get(i).getFieldId()+"");
				}

				multipart.addFormField("FbField[fb_form_id]", mReportForm.getFormDetailsList().get(0).getFormID());
				Log.i("Result", "FbField[fb_form_id]" + mReportForm.getFormDetailsList().get(0).getFormID());


				for(int i = 0; i < mReportForm.getFormFieldList().size(); i++){
					FieldDetails fieldDetails = mReportForm.getFormFieldList().get(i).getFieldDetails();
					if(fieldDetails.getFieldType() == DynamicForm.FieldType.FILE){
						File fileTobeUpload = new File(fieldDetails.getFieldValue());
						if(fileTobeUpload.exists()) {

							String something = fileTobeUpload.getName();
							String extension = something.substring(something.lastIndexOf(".")+1);

							if(extension.equalsIgnoreCase("jpg")
									|| extension.equalsIgnoreCase("jpeg")
									|| extension.equalsIgnoreCase("bmp")
									|| extension.equalsIgnoreCase("png")){

								/*Log.i("Submit Form", CapturedImgFile.toString());
								Log.i("Submit Form : Name", CapturedImgFile.getName());
								multipart.addFilePart("field_snap", CapturedImgFile);*/

								//Bitmap bmp = BitmapFactory.decodeFile(CapturedImgFile.getPath());
								Bitmap bmp = Util.decodeFile(fileTobeUpload, 400);
								ByteArrayOutputStream bos = new ByteArrayOutputStream();
								bmp.compress(Bitmap.CompressFormat.JPEG, 70, bos);
								InputStream in = new ByteArrayInputStream(bos.toByteArray());
								//ContentBody foto = new InputStreamBody(in, "image/jpeg", "filename");

								OutputStream outputStream = null;

								try {
									// write the inputStream to a FileOutputStream
									outputStream =
											new FileOutputStream(new File(Environment.getExternalStorageDirectory(),
													getString(getApplicationInfo().labelRes)+"file-new.jpeg"));

									int read = 0;
									byte[] bytes = new byte[1024];

									while ((read = in.read(bytes)) != -1) {
										outputStream.write(bytes, 0, read);
									}

									System.out.println("Done!");

								} catch (IOException e) {
									e.printStackTrace();
								} finally {
									if (in != null) {
										try {
											in.close();
										} catch (IOException e) {
											e.printStackTrace();
										}
									}
									if (outputStream != null) {
										try {
											// outputStream.flush();
											outputStream.close();
										} catch (IOException e) {
											e.printStackTrace();
										}

									}
								}

								File file = new File(Environment.getExternalStorageDirectory(),
										getString(getApplicationInfo().labelRes)+"file-new.jpeg");
								if(file != null && file.exists()) {

									multipart.addFilePart("FbField[field_" + fieldDetails.getFieldID() + "][tmp_name]", file);
									Log.i("Result", "FbField[field_" + fieldDetails.getFieldID() + "][tmp_name]" + "");
									multipart.addFormField("FbField[field_" + fieldDetails.getFieldID() + "][name]", file.getName());
									Log.i("Result", "FbField[field_" + fieldDetails.getFieldID() + "][tmp_name]" + "");
								}

							} else {

								multipart.addFilePart("FbField[field_" + fieldDetails.getFieldID() + "][tmp_name]", fileTobeUpload);
								Log.i("Result", "FbField[field_" + fieldDetails.getFieldID() + "][tmp_name]" + "");
								multipart.addFormField("FbField[field_" + fieldDetails.getFieldID() + "][name]", fileTobeUpload.getName());
								Log.i("Result", "FbField[field_" + fieldDetails.getFieldID() + "][tmp_name]" + "");
							}
						}
					} else if(fieldDetails.getFieldType() == DynamicForm.FieldType.SELECT
							|| fieldDetails.getFieldType() == DynamicForm.FieldType.CHECKBOX
							|| fieldDetails.getFieldType() == DynamicForm.FieldType.RADIO) {
						for (int cnt = 0; cnt < fieldDetails.getFieldOptions().getOptions().size(); cnt++) {
							Options option = fieldDetails.getFieldOptions().getOptions().get(cnt);
							if(option.isSelected()) {
								multipart.addFormField("FbField[field_" + fieldDetails.getFieldID() + "][]", option.getOptionId()+"");
								Log.i("Result", "FbField[field_" + "FbField[field_" + fieldDetails.getFieldID() + "][]"+ option.getOptionId()+"");
							}
						}
					} else{
						multipart.addFormField("FbField[field_"+fieldDetails.getFieldID()+"]", fieldDetails.getFieldValue());
						Log.i("Result", "FbField[field_"+fieldDetails.getFieldID()+"]"+ fieldDetails.getFieldValue());
					}
				}

				multipart.addFormField("lattitude", lattitude+""); //TODO Send address
				Log.i("Result", "lattitude" + lattitude);
				multipart.addFormField("longitude", longitude + "");
				Log.i("Result", "longitude"+longitude);



				if(CapturedImgFile != null && CapturedImgFile.exists()) {

					/*Log.i("Submit Form", CapturedImgFile.toString());
					Log.i("Submit Form : Name", CapturedImgFile.getName());
					multipart.addFilePart("field_snap", CapturedImgFile);*/

					//Bitmap bmp = BitmapFactory.decodeFile(CapturedImgFile.getPath());
					Bitmap bmp = Util.decodeFile(CapturedImgFile, 400);
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					bmp.compress(Bitmap.CompressFormat.JPEG, 70, bos);
					InputStream in = new ByteArrayInputStream(bos.toByteArray());
					//ContentBody foto = new InputStreamBody(in, "image/jpeg", "filename");

					OutputStream outputStream = null;

					try {
						// write the inputStream to a FileOutputStream
						outputStream =
								new FileOutputStream(new File(Environment.getExternalStorageDirectory(),
										getString(getApplicationInfo().labelRes)+"holder-new.jpeg"));

						int read = 0;
						byte[] bytes = new byte[1024];

						while ((read = in.read(bytes)) != -1) {
							outputStream.write(bytes, 0, read);
						}

						System.out.println("Done!");

					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						if (in != null) {
							try {
								in.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						if (outputStream != null) {
							try {
								// outputStream.flush();
								outputStream.close();
							} catch (IOException e) {
								e.printStackTrace();
							}

						}
					}

					File file = new File(Environment.getExternalStorageDirectory(),
							getString(getApplicationInfo().labelRes)+"holder-new.jpeg");
					if(file != null && file.exists()) {

						Log.i("Submit Form", file.toString());
						Log.i("Submit Form : Name", file.getName());
						multipart.addFilePart("field_snap", file);
					}
				}

				List<String> response = multipart.finish();

				System.out.println("SERVER REPLIED:");
				String res = "";
				for (String line : response) {
					res = res + line + "\n";
				}
				Log.i(TAG, res);

	            /* {"Response":
	             * 		[{"submitStatus":"Success",
	             * "		submitMessage":"Report submitted successfully.",
	             * "	userResponseId":"38"}]
	             * }
	             */


				JSONArray jsonArray;
				JSONObject jObject;
				try {
					jObject = new JSONObject(res);

					jsonArray = jObject.getJSONArray("Response");
					for (int index = 0; index < jsonArray.length(); index++) {
						JSONObject jsonObject = jsonArray.getJSONObject(index);

						if(jsonObject != null && jsonObject.isNull("Apikey") == false){
							String msg = jsonObject.getString("Apikey");
							submitMessage = msg+"\nPlease logout and login again to get your app work.";
							return false;
						}

						String submitStatus = jsonObject.optString("submitStatus", "");
						submitMessage = jsonObject.optString("submitMessage", "");
						userResponseId = jsonObject.optString("userResponseId", "");

						if(submitStatus.equalsIgnoreCase("failed")){
							// {"field_344":"This field is required"}

							String res1 = submitMessage;
							String str = "";
							String[] fields = res1.split(":");
							if(fields != null && fields.length > 0){
								str = fields[0];
								str = str.replace("{","");
								str = str.replace("\"","");
							}

							String fieldName = "";
							for(int i = 0; i < mReportForm.getFormFieldList().size(); i++){
								String formID = "field_"+mReportForm.getFormFieldList().get(i).getFieldId();
								if(formID != null && formID.equalsIgnoreCase(str)){
									fieldName = mReportForm.getFormFieldList().get(i).getFieldDetails().getLabelName();
								}
							}

							if(fieldName != null && fieldName.length() > 0) {
								submitMessage = fieldName + " field is required.";
							}

							return false;
						}

						if(submitStatus.equalsIgnoreCase("Success")){
							return true;
						}

						return false;
					}
				} catch (JSONException e) {
					submitMessage = "Getting error response from server.";
					Log.e(TAG, "doInBackground()", e);
				}

			} catch(ConnectTimeoutException e){
				submitMessage = "Connection time out.\nPlease reset internet connection.";
			}catch (IOException ex) {
				submitMessage = "Network Error occured.";
				System.err.println(ex);
			}

			return false;
		}

		@Override
		protected void onPostExecute(final Boolean result) {
			super.onPostExecute(result);
			lProLayout.setVisibility(View.INVISIBLE);
			btnSubmit.setEnabled(true);
			btnCahngePic.setEnabled(true);
			btnRemovePic.setEnabled(true);
			imgPhoto.setEnabled(true);
			mPocketBar.progressiveStop();
			isAnyNetworkOperationIsProcessing = false;

			if(result == true){
				if(CapturedImgFile != null && CapturedImgFile.exists()) {
					customAlert("Submitted successfully with picture.", 1, false, false);
				} else{
					customAlert("Submitted successfully.", 1, false, false);
				}
			} else{
				/*layout = (RelativeLayout) findViewById(R.id.layoutBg);
				animation = AnimationUtils.loadAnimation(ReportFromDetailsActivity.this, R.anim.move_up);
				animation.setAnimationListener(ReportFromDetailsActivity.this);
				moveUp = true;
				moveDown = false;
				layout.setDrawingCacheEnabled(true);
				layout.startAnimation(animation);*/
				Log.e("Submit Time","ErrorMsg : "+ submitMessage);
				customAlert(submitMessage, 0, false, true);
			}

		}

	}

}