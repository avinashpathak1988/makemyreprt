package com.luminousinfoways.makemyreport.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.luminousinfoways.makemyreport.pojo.Report;
import com.luminousinfoways.makemyreport.pojo.ReportForm;

public class MMRSingleTone {

	private static MMRSingleTone instance = null;
	public List<Report> submitedReport = null;
	public List<Report> expierdReport = null;
	public List<Report> rejectedReport = null;
	public List<Report> pendingReport = null;
	public List<Report> allReport = null;
	public ReportForm mReportForm = null;

	public int noOfReportAssign = 0;
    public int noOfReportSubmitted = 0;
    public int noOfReportExpired = 0;
    public int noOfReportPending = 0;
    public int noOfReportRejected = 0;
    public int noOfLastSubmission = 0;
	
	public Context context = null;
	
	public Fragment currentFragment = null;
	public int currentReportCategoryShowing = -1;
	public int drawerMenuItemSelectedNo = -1;
	public boolean isRunningReportAsynncTask = false;
	public boolean isAlreadyRunReportAsyncBefore = false;
	
	public boolean isBackFromSubmit = false;

	public MMRSingleTone(){
		submitedReport = new ArrayList<Report>();
		expierdReport = new ArrayList<Report>();
		rejectedReport = new ArrayList<Report>();
		pendingReport = new ArrayList<Report>();
		mReportForm = new ReportForm();
	}
	
	public static MMRSingleTone getInstance(){
		if(instance == null){
			instance = new MMRSingleTone();
		}
		
		return instance;
	}
}
