package com.luminousinfoways.makemyreportandroid.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.luminousinfoways.makemyreportandroid.pojo.Report;
import com.luminousinfoways.makemyreportandroid.pojo.User;

import java.util.ArrayList;
import java.util.List;

public class MMRDatabaseHelper extends SQLiteOpenHelper {

	   public static final String DATABASE_NAME = "makemyreport.db";
	   
	   //User
	   public static final String USER_TABLE_NAME = "User";
	   public static final String USER_COLUMN_NAME = "user_name";
	   public static final String USER_COLUMN_ID = "user_id";
	   public static final String USER_COLUMN_LOGIN_STATUS = "user_login_status";
	   public static final String USER_COLUMN_APIKEY = "user_api_key";
	   public static final String USER_COLUMN_CONTACT_NO = "user_contact_no";
	   public static final String USER_COLUMN_DOB = "user_dob";
	   public static final String USER_ORG_ID = "org_id";
	   public static final String USER_ORG_NAME = "org_name";
	   
	   //Report
	   public static final String REPORT_TABLE_NAME = "Report";
	   public static final String REPORT_ID = "reportID";
	   public static final String REPORT_TITLE = "reportTitle";
	   public static final String REPORT_TYPE = "reportType";
	   public static final String REPORT_CREATED_BY = "reportCreateBy";
	   public static final String REPORT_CREATED_ON = "reportCreateOn";
	   public static final String REPORT_LAST_DATE_OF_SUBMISSION = "reportLastDateOfSubmission";
	   public static final String REPORT_FB_ASSIGN_USER_ID = "reportfbAssignUserID";
	   public static final String REPORT_RECENT_SUBMISSION = "reportRecentSubmission";
	   public static final String REPORT_SHOW_SUBMIT_BUTTON = "reportShowSubmitButton";
	   public static final String REPORT_SHOW_REJECT_BUTTON = "reportShowRejectButton";
	   public static final String REPORT_SHOW_REJECT_MESSAGE = "reportShowRejectMessage";
	   public static final String REPORT_SHOW_VIEW_BUTTON = "reportShowViewButton";
	   public static final String REPORT_STATUS = "reportStatus";
	   public static final String REPORT_NO_OF_TIMES_REPORT_SUBMISSION = "noOfTimesReportSubmission";
	   public static final String REPORT_NO_OF_TIMES_REPORT_SUBMITTED = "noOfTimesReportSubmitted";
	   public static final String REPORT_NO_OF_TIMES_REPORT_TO_BE_SUBMITTED = "noOfTimesReportToBeSubmitted";
	   public static final String REPORT_CAN_SUBMIT_REPORT_AFTER_DEADLINE = "canSubmitReportAfterDeadLine";

	   public MMRDatabaseHelper(Context context)
	   {
	      super(context, DATABASE_NAME , null, 1);
	   }

	   @Override
	   public void onCreate(SQLiteDatabase db) {
		   
		   /*
		    * Creation of User table
		    */
		   
		   db.execSQL(
				      "create table " +USER_TABLE_NAME+
				      "("+USER_COLUMN_ID+" integer primary key, "+USER_COLUMN_APIKEY+" text , "+USER_COLUMN_NAME+" text,"
				      			+USER_ORG_ID+" text, "
				      			+USER_ORG_NAME+" text, "
				      			+USER_COLUMN_LOGIN_STATUS+" numeric)"
				      );
	      
	      
	      /*
		    * Creation of Report table
		    */
		   
	      db.execSQL(
			      "create table " +REPORT_TABLE_NAME+
			      "("+REPORT_ID+" integer primary key, "
			    		  +REPORT_TITLE+" text,"
			    		  +REPORT_TYPE+" text,"
			    		  +REPORT_CREATED_BY+" text, "
			    		  +REPORT_CREATED_ON+" text, "
			    		  +REPORT_LAST_DATE_OF_SUBMISSION+" text, "
			    		  +REPORT_FB_ASSIGN_USER_ID+" text, "
			    		  +REPORT_RECENT_SUBMISSION+" text, "
			    		  +REPORT_SHOW_SUBMIT_BUTTON+" text, "
			    		  +REPORT_SHOW_REJECT_BUTTON+" text, "
			    		  +REPORT_SHOW_REJECT_MESSAGE+" text, "
			    		  +REPORT_SHOW_VIEW_BUTTON+" text, "
			    		  +REPORT_STATUS+" text, "
			    		  +REPORT_NO_OF_TIMES_REPORT_SUBMISSION+" numeric, "
			    		  +REPORT_NO_OF_TIMES_REPORT_SUBMITTED+" numeric, "
			    		  +REPORT_NO_OF_TIMES_REPORT_TO_BE_SUBMITTED+" numeric, "
			    		  +REPORT_CAN_SUBMIT_REPORT_AFTER_DEADLINE+" text)"
			      );
	      
	   }

	   @Override
	   public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	      db.execSQL("DROP TABLE IF EXISTS "+USER_TABLE_NAME);
	      db.execSQL("DROP TABLE IF EXISTS "+REPORT_TABLE_NAME);
	      onCreate(db);
	   }

	   /*
	    * @User Table
	    */
	   public boolean insertUser  (String api_key, String name, int login_status,
			   String org_id, String org_name)
	   {
	      SQLiteDatabase db = this.getWritableDatabase();
	      ContentValues contentValues = new ContentValues();
	      
	      contentValues.put(USER_COLUMN_APIKEY, api_key);
	      contentValues.put(USER_COLUMN_NAME, name);
	      contentValues.put(USER_COLUMN_LOGIN_STATUS, login_status);	
	      contentValues.put(USER_ORG_ID, org_id);
	      contentValues.put(USER_ORG_NAME, org_name);

	      db.insert(USER_TABLE_NAME, null, contentValues);
	      return true;
	   }
	   
	   public User getLoggedInUser() {
		   
		   User user = null;
	      //hp = new HashMap();
	      SQLiteDatabase db = this.getReadableDatabase();
	      Cursor res =  db.rawQuery( "select * from "+USER_TABLE_NAME+" where "+USER_COLUMN_LOGIN_STATUS+"=1", null );
	      res.moveToFirst();
	      while(res.isAfterLast() == false){
	    	  user = new User();
	    	  user.setApikey(res.getString(res.getColumnIndex(USER_COLUMN_APIKEY)));
	    	  user.setName(res.getString(res.getColumnIndex(USER_COLUMN_NAME)));
	    	  user.setLoginStatus(res.getInt(res.getColumnIndex(USER_COLUMN_LOGIN_STATUS)));
	          user.setOrg_id(res.getString(res.getColumnIndex(USER_ORG_ID)));
	          user.setOrg_name(res.getString(res.getColumnIndex(USER_ORG_NAME)));
	          res.moveToNext();
	      }
	      return user;
	   }
	   	   
	   public int numberOfRowsOfUserTable(){
	      SQLiteDatabase db = this.getReadableDatabase();
	      int numRows = (int) DatabaseUtils.queryNumEntries(db, USER_TABLE_NAME);
	      return numRows;
	   }
	   
	   public boolean updateUserApikey (String old_apikey, String new_api_key)
	   {
		   SQLiteDatabase db = this.getWritableDatabase();
		   ContentValues contentValues = new ContentValues();

		   contentValues.put(USER_COLUMN_APIKEY, new_api_key);

	       db.update(USER_TABLE_NAME, contentValues, USER_COLUMN_APIKEY+" = ? ", new String[] { old_apikey } );
	      return true;
	   }
	   
	   public boolean updateUserLogInStatusWhereApiKeyIS (String apikey, int loginStatus)
	   {
		   SQLiteDatabase db = this.getWritableDatabase();
		   ContentValues contentValues = new ContentValues();

		   contentValues.put(USER_COLUMN_LOGIN_STATUS, loginStatus);

	       db.update(USER_TABLE_NAME, contentValues, USER_COLUMN_APIKEY+" = ? ", new String[] { apikey } );
	      return true;
	   }
	   
	   public boolean updateUserDetailsWhereApiKeyIS (String apikey, String name, String dob, String contactno)
	   {
		   SQLiteDatabase db = this.getWritableDatabase();
		   ContentValues contentValues = new ContentValues();

		   contentValues.put(USER_COLUMN_NAME, name);
		   contentValues.put(USER_COLUMN_CONTACT_NO, contactno);
		   contentValues.put(USER_COLUMN_DOB, dob);

	       db.update(USER_TABLE_NAME, contentValues, USER_COLUMN_APIKEY+" = ? ", new String[] { apikey } );
	      return true;
	   }
	   
	   public int deleteUser (String apiKey)
	   {
	      SQLiteDatabase db = this.getWritableDatabase();
	      return db.delete(USER_TABLE_NAME, 
	    		  USER_COLUMN_APIKEY+" = ? ", 
	      new String[] { apiKey });
	   }
	   
	   public int deleteAllUser ()
	   {
	      SQLiteDatabase db = this.getWritableDatabase();
	      return db.delete(USER_TABLE_NAME, null, null);
	   }
	   
	   public User getUserWhereApiKeyIs(String apiKey)
	   {
		   User user = null;
	      //hp = new HashMap();
	      SQLiteDatabase db = this.getReadableDatabase();
	      Cursor res =  db.rawQuery( "select * from "+USER_TABLE_NAME+" where "+USER_COLUMN_APIKEY+"=\""+apiKey+"\"", null );
	      res.moveToFirst();
	      while(res.isAfterLast() == false){
	    	  user = new User();
	    	  user.setApikey(res.getString(res.getColumnIndex(USER_COLUMN_APIKEY)));
	    	  user.setName(res.getString(res.getColumnIndex(USER_COLUMN_NAME)));
	    	  user.setLoginStatus(res.getInt(res.getColumnIndex(USER_COLUMN_LOGIN_STATUS)));
	    	  user.setContact_no(res.getString(res.getColumnIndex(USER_COLUMN_CONTACT_NO)));
	          user.setDob(res.getString(res.getColumnIndex(USER_COLUMN_DOB)));
	    	  	          
	          res.moveToNext();
	      }
	      return user;
	   }
	   
	   
	   /*
	    * @Report Table
	    */
	   public int numberOfRowsOfReportsTable(){
	      SQLiteDatabase db = this.getReadableDatabase();
	      int numRows = (int) DatabaseUtils.queryNumEntries(db, REPORT_TABLE_NAME);
	      return numRows;
	   }
	   
	   public List<Report> getSubmittedReports(){
		  List<Report> reportList = new ArrayList<Report>();
	      SQLiteDatabase db = this.getReadableDatabase();
	      Cursor res =  db.rawQuery( "select * from "+REPORT_TABLE_NAME+" where "+REPORT_NO_OF_TIMES_REPORT_SUBMITTED+">0", null );
	      res.moveToFirst();
	      while(res.isAfterLast() == false){
	    	  Report report = new Report();
	    	  report.setReportID(res.getInt(res.getColumnIndex(REPORT_ID)));
	    	  report.setReportTitle(res.getString(res.getColumnIndex(REPORT_TITLE)));
	    	  report.setReportType(res.getString(res.getColumnIndex(REPORT_TYPE)));
	    	  report.setReportCreatedBy(res.getString(res.getColumnIndex(REPORT_CREATED_BY)));
	    	  report.setReportCreatedOn(res.getString(res.getColumnIndex(REPORT_CREATED_ON)));
	    	  report.setLastDateofSubmission(res.getString(res.getColumnIndex(REPORT_LAST_DATE_OF_SUBMISSION)));
	    	  report.setFbAssignUserID(res.getString(res.getColumnIndex(REPORT_FB_ASSIGN_USER_ID)));
	    	  report.setRecentSubmission(res.getString(res.getColumnIndex(REPORT_RECENT_SUBMISSION)));
	    	  report.setShowSubmitButton(res.getString(res.getColumnIndex(REPORT_SHOW_SUBMIT_BUTTON)));
	    	  report.setShowRejectButton(res.getString(res.getColumnIndex(REPORT_SHOW_REJECT_BUTTON)));
	    	  report.setShowRejectMessage(res.getString(res.getColumnIndex(REPORT_SHOW_REJECT_MESSAGE)));
	    	  report.setShowViewButton(res.getString(res.getColumnIndex(REPORT_SHOW_VIEW_BUTTON)));
	    	  report.setReportStatus(res.getString(res.getColumnIndex(REPORT_STATUS)));
	    	  report.setNoOfTimesReportSubmission(res.getInt(res.getColumnIndex(REPORT_NO_OF_TIMES_REPORT_SUBMISSION)));
	    	  report.setNoOfTimesReportSubmitted(res.getInt(res.getColumnIndex(REPORT_NO_OF_TIMES_REPORT_SUBMITTED)));
	    	  report.setNoOfTimesReportToBeSubmitted(res.getInt(res.getColumnIndex(REPORT_NO_OF_TIMES_REPORT_TO_BE_SUBMITTED)));
	    	  report.setCanSubmitReportAfterDeadLine(res.getString(res.getColumnIndex(REPORT_CAN_SUBMIT_REPORT_AFTER_DEADLINE)));
	    	  reportList.add(report);
	          res.moveToNext();
	      }
	      return reportList;
	   }
	   
	   public List<Report> getPendingReports(){
			  List<Report> reportList = new ArrayList<Report>();
		      SQLiteDatabase db = this.getReadableDatabase();
		      Cursor res =  db.rawQuery( "select * from "+REPORT_TABLE_NAME+" where "+REPORT_STATUS+"=\"S\""
		    		  +" AND "+REPORT_NO_OF_TIMES_REPORT_SUBMITTED+"<"+REPORT_NO_OF_TIMES_REPORT_SUBMISSION, null );
		      res.moveToFirst();
		      while(res.isAfterLast() == false){
		    	  Report report = new Report();
		    	  report.setReportID(res.getInt(res.getColumnIndex(REPORT_ID)));
		    	  report.setReportTitle(res.getString(res.getColumnIndex(REPORT_TITLE)));
		    	  report.setReportType(res.getString(res.getColumnIndex(REPORT_TYPE)));
		    	  report.setReportCreatedBy(res.getString(res.getColumnIndex(REPORT_CREATED_BY)));
		    	  report.setReportCreatedOn(res.getString(res.getColumnIndex(REPORT_CREATED_ON)));
		    	  report.setLastDateofSubmission(res.getString(res.getColumnIndex(REPORT_LAST_DATE_OF_SUBMISSION)));
		    	  report.setFbAssignUserID(res.getString(res.getColumnIndex(REPORT_FB_ASSIGN_USER_ID)));
		    	  report.setRecentSubmission(res.getString(res.getColumnIndex(REPORT_RECENT_SUBMISSION)));
		    	  report.setShowSubmitButton(res.getString(res.getColumnIndex(REPORT_SHOW_SUBMIT_BUTTON)));
		    	  report.setShowRejectButton(res.getString(res.getColumnIndex(REPORT_SHOW_REJECT_BUTTON)));
		    	  report.setShowRejectMessage(res.getString(res.getColumnIndex(REPORT_SHOW_REJECT_MESSAGE)));
		    	  report.setShowViewButton(res.getString(res.getColumnIndex(REPORT_SHOW_VIEW_BUTTON)));
		    	  report.setReportStatus(res.getString(res.getColumnIndex(REPORT_STATUS)));
		    	  report.setNoOfTimesReportSubmission(res.getInt(res.getColumnIndex(REPORT_NO_OF_TIMES_REPORT_SUBMISSION)));
		    	  report.setNoOfTimesReportSubmitted(res.getInt(res.getColumnIndex(REPORT_NO_OF_TIMES_REPORT_SUBMITTED)));
		    	  report.setNoOfTimesReportToBeSubmitted(res.getInt(res.getColumnIndex(REPORT_NO_OF_TIMES_REPORT_TO_BE_SUBMITTED)));
		    	  report.setCanSubmitReportAfterDeadLine(res.getString(res.getColumnIndex(REPORT_CAN_SUBMIT_REPORT_AFTER_DEADLINE)));
		    	  reportList.add(report);
		          res.moveToNext();
		      }
		      return reportList;
		   }
	   
	   public List<Report> getRejectedReports(){
		  List<Report> reportList = new ArrayList<Report>();
	      SQLiteDatabase db = this.getReadableDatabase();
	      Cursor res =  db.rawQuery( "select * from "+REPORT_TABLE_NAME+" where "+REPORT_STATUS+"=\"R\"", null );
	      res.moveToFirst();
	      while(res.isAfterLast() == false){
	    	  Report report = new Report();
	    	  report.setReportID(res.getInt(res.getColumnIndex(REPORT_ID)));
	    	  report.setReportTitle(res.getString(res.getColumnIndex(REPORT_TITLE)));
	    	  report.setReportType(res.getString(res.getColumnIndex(REPORT_TYPE)));
	    	  report.setReportCreatedBy(res.getString(res.getColumnIndex(REPORT_CREATED_BY)));
	    	  report.setReportCreatedOn(res.getString(res.getColumnIndex(REPORT_CREATED_ON)));
	    	  report.setLastDateofSubmission(res.getString(res.getColumnIndex(REPORT_LAST_DATE_OF_SUBMISSION)));
	    	  report.setFbAssignUserID(res.getString(res.getColumnIndex(REPORT_FB_ASSIGN_USER_ID)));
	    	  report.setRecentSubmission(res.getString(res.getColumnIndex(REPORT_RECENT_SUBMISSION)));
	    	  report.setShowSubmitButton(res.getString(res.getColumnIndex(REPORT_SHOW_SUBMIT_BUTTON)));
	    	  report.setShowRejectButton(res.getString(res.getColumnIndex(REPORT_SHOW_REJECT_BUTTON)));
	    	  report.setShowRejectMessage(res.getString(res.getColumnIndex(REPORT_SHOW_REJECT_MESSAGE)));
	    	  report.setShowViewButton(res.getString(res.getColumnIndex(REPORT_SHOW_VIEW_BUTTON)));
	    	  report.setReportStatus(res.getString(res.getColumnIndex(REPORT_STATUS)));
	    	  report.setNoOfTimesReportSubmission(res.getInt(res.getColumnIndex(REPORT_NO_OF_TIMES_REPORT_SUBMISSION)));
	    	  report.setNoOfTimesReportSubmitted(res.getInt(res.getColumnIndex(REPORT_NO_OF_TIMES_REPORT_SUBMITTED)));
	    	  report.setNoOfTimesReportToBeSubmitted(res.getInt(res.getColumnIndex(REPORT_NO_OF_TIMES_REPORT_TO_BE_SUBMITTED)));
	    	  report.setCanSubmitReportAfterDeadLine(res.getString(res.getColumnIndex(REPORT_CAN_SUBMIT_REPORT_AFTER_DEADLINE)));
	    	  reportList.add(report);
	          res.moveToNext();
	      }
	      return reportList;
	   }
	   
	   public List<Report> getExpieredReports(){
		  List<Report> reportList = new ArrayList<Report>();
	      SQLiteDatabase db = this.getReadableDatabase();
	      Cursor res =  db.rawQuery( "select * from "+REPORT_TABLE_NAME+" where "+REPORT_STATUS+"=\"E\"", null );
	      res.moveToFirst();
	      while(res.isAfterLast() == false){
	    	  Report report = new Report();
	    	  report.setReportID(res.getInt(res.getColumnIndex(REPORT_ID)));
	    	  report.setReportTitle(res.getString(res.getColumnIndex(REPORT_TITLE)));
	    	  report.setReportType(res.getString(res.getColumnIndex(REPORT_TYPE)));
	    	  report.setReportCreatedBy(res.getString(res.getColumnIndex(REPORT_CREATED_BY)));
	    	  report.setReportCreatedOn(res.getString(res.getColumnIndex(REPORT_CREATED_ON)));
	    	  report.setLastDateofSubmission(res.getString(res.getColumnIndex(REPORT_LAST_DATE_OF_SUBMISSION)));
	    	  report.setFbAssignUserID(res.getString(res.getColumnIndex(REPORT_FB_ASSIGN_USER_ID)));
	    	  report.setRecentSubmission(res.getString(res.getColumnIndex(REPORT_RECENT_SUBMISSION)));
	    	  report.setShowSubmitButton(res.getString(res.getColumnIndex(REPORT_SHOW_SUBMIT_BUTTON)));
	    	  report.setShowRejectButton(res.getString(res.getColumnIndex(REPORT_SHOW_REJECT_BUTTON)));
	    	  report.setShowRejectMessage(res.getString(res.getColumnIndex(REPORT_SHOW_REJECT_MESSAGE)));
	    	  report.setShowViewButton(res.getString(res.getColumnIndex(REPORT_SHOW_VIEW_BUTTON)));
	    	  report.setReportStatus(res.getString(res.getColumnIndex(REPORT_STATUS)));
	    	  report.setNoOfTimesReportSubmission(res.getInt(res.getColumnIndex(REPORT_NO_OF_TIMES_REPORT_SUBMISSION)));
	    	  report.setNoOfTimesReportSubmitted(res.getInt(res.getColumnIndex(REPORT_NO_OF_TIMES_REPORT_SUBMITTED)));
	    	  report.setNoOfTimesReportToBeSubmitted(res.getInt(res.getColumnIndex(REPORT_NO_OF_TIMES_REPORT_TO_BE_SUBMITTED)));
	    	  report.setCanSubmitReportAfterDeadLine(res.getString(res.getColumnIndex(REPORT_CAN_SUBMIT_REPORT_AFTER_DEADLINE)));
	    	  reportList.add(report);
	          res.moveToNext();
	      }
	      return reportList;
	   }
	   
	   public boolean insertUpdateReport (Report report)
	   {
		   SQLiteDatabase db = this.getWritableDatabase();
		   ContentValues contentValues = new ContentValues();

		   contentValues.put(REPORT_ID, report.getReportID());
		   contentValues.put(REPORT_TITLE, report.getReportTitle());
		   contentValues.put(REPORT_TYPE, report.getReportType());	
		   contentValues.put(REPORT_CREATED_BY, report.getReportCreatedBy());
		   contentValues.put(REPORT_CREATED_ON, report.getReportCreatedOn());
		   contentValues.put(REPORT_LAST_DATE_OF_SUBMISSION, report.getLastDateofSubmission());
		   contentValues.put(REPORT_FB_ASSIGN_USER_ID, report.getFbAssignUserID());
		   contentValues.put(REPORT_RECENT_SUBMISSION, report.getRecentSubmission());
		   contentValues.put(REPORT_SHOW_SUBMIT_BUTTON, report.getShowSubmitButton());
		   contentValues.put(REPORT_SHOW_REJECT_BUTTON, report.getShowRejectButton());
		   contentValues.put(REPORT_SHOW_REJECT_MESSAGE, report.getShowRejectMessage());
		   contentValues.put(REPORT_SHOW_VIEW_BUTTON, report.getShowViewButton());
		   contentValues.put(REPORT_STATUS, report.getReportStatus());
		   contentValues.put(REPORT_NO_OF_TIMES_REPORT_SUBMISSION, report.getNoOfTimesReportSubmission());
		   contentValues.put(REPORT_NO_OF_TIMES_REPORT_SUBMITTED, report.getNoOfTimesReportSubmitted());
		   contentValues.put(REPORT_NO_OF_TIMES_REPORT_TO_BE_SUBMITTED, report.getNoOfTimesReportToBeSubmitted());
		   contentValues.put(REPORT_CAN_SUBMIT_REPORT_AFTER_DEADLINE, report.getCanSubmitReportAfterDeadLine());
		   
		   if(isContainReport(report.getReportID())){
			   db.update(REPORT_TABLE_NAME, contentValues, REPORT_ID+" = ? ", new String[] { Integer.toString(report.getReportID()) } );
		   } else{
			   db.insert(REPORT_TABLE_NAME, null, contentValues);
		   }
	      
	      return true;
	   }
	   
	   private boolean isContainReport (int report_id){
	      SQLiteDatabase db = this.getReadableDatabase();
	      Cursor res =  db.rawQuery( "select * from "+REPORT_TABLE_NAME+" where "+REPORT_ID+"="+report_id, null );
	      res.moveToFirst();
	      int count = 0 ;
	      while(res.isAfterLast() == false){
	    	  count = count + 1;
	    	  res.moveToNext();
	      }
	      
	      if(count > 0){
	    	  return true;
	      }else{
	    	  return false;
	      }
	   }
	   
	   public int deleteAllReports ()
	   {
	      SQLiteDatabase db = this.getWritableDatabase();
	      return db.delete(REPORT_TABLE_NAME, null, null);
	   }
	   
	   public int deleteReport (Report report)
	   {
	      SQLiteDatabase db = this.getWritableDatabase();
	      return db.delete(REPORT_TABLE_NAME, 
	    		  REPORT_ID+" = ? ", 
	      new String[] { Integer.toString(report.getReportID()) });
	   }
}