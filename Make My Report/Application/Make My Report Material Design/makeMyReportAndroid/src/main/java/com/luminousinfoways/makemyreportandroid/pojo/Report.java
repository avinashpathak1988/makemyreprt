package com.luminousinfoways.makemyreportandroid.pojo;

import java.io.Serializable;

public class Report implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/*
	 * reportID: "45"
		reportTitle: "Mail test"
		reportType: "Instant"
		reportCreatedBy: "Demo Admin"
		reportCreatedOn: "15/11/2014"
		LastDateofSubmission: "30/11/2014"
		FbAssignUserID: "169"
		RecentSubmission: "Y"
		ShowSubmitButton: "Y"
		ShowRejectButton: "N"
		ShowRejectMessage: ""
		ShowViewButton: "Y"
		reportStatus: "P"
		noOfTimesReportSubmission: 1000
		noOfTimesReportSubmitted: 1
		noOfTimesReportToBeSubmitted: 999
		canSubmitReportAfterDeadLine: "N"
	 * */
	
	int reportID;
	String reportTitle;
	String reportType;
	String reportCreatedBy;
	String reportCreatedOn;
	String LastDateofSubmission;
	String FbAssignUserID;
	String RecentSubmission;
	String ShowSubmitButton;
	String ShowRejectButton;
	String ShowRejectMessage;
	String ShowViewButton;
	String reportStatus;
	int noOfTimesReportSubmission;
    int noOfTimesReportSubmitted;
    int noOfTimesReportToBeSubmitted;
    String canSubmitReportAfterDeadLine;
	
	public int getReportID() {
		return reportID;
	}
	public void setReportID(int reportID) {
		this.reportID = reportID;
	}
	public String getReportTitle() {
		return reportTitle;
	}
	public void setReportTitle(String reportTitle) {
		this.reportTitle = reportTitle;
	}
	public String getReportType() {
		return reportType;
	}
	public void setReportType(String reportType) {
		this.reportType = reportType;
	}
	public int getNoOfTimesReportSubmission() {
		return noOfTimesReportSubmission;
	}
	public void setNoOfTimesReportSubmission(int noOfTimesReportSubmission) {
		this.noOfTimesReportSubmission = noOfTimesReportSubmission;
	}
	public int getNoOfTimesReportToBeSubmitted() {
		return noOfTimesReportToBeSubmitted;
	}
	public void setNoOfTimesReportToBeSubmitted(int noOfTimesReportToBeSubmitted) {
		this.noOfTimesReportToBeSubmitted = noOfTimesReportToBeSubmitted;
	}
	public String getCanSubmitReportAfterDeadLine() {
		return canSubmitReportAfterDeadLine;
	}
	public void setCanSubmitReportAfterDeadLine(String canSubmitReportAfterDeadLine) {
		this.canSubmitReportAfterDeadLine = canSubmitReportAfterDeadLine;
	}
	public String getReportCreatedBy() {
		return reportCreatedBy;
	}
	public void setReportCreatedBy(String reportCreatedBy) {
		this.reportCreatedBy = reportCreatedBy;
	}
	public String getReportCreatedOn() {
		return reportCreatedOn;
	}
	public void setReportCreatedOn(String reportCreatedOn) {
		this.reportCreatedOn = reportCreatedOn;
	}
	public String getLastDateofSubmission() {
		return LastDateofSubmission;
	}
	public void setLastDateofSubmission(String lastDateofSubmission) {
		LastDateofSubmission = lastDateofSubmission;
	}
	public String getFbAssignUserID() {
		return FbAssignUserID;
	}
	public void setFbAssignUserID(String fbAssignUserID) {
		FbAssignUserID = fbAssignUserID;
	}
	public String getRecentSubmission() {
		return RecentSubmission;
	}
	public void setRecentSubmission(String recentSubmission) {
		RecentSubmission = recentSubmission;
	}
	public String getShowSubmitButton() {
		return ShowSubmitButton;
	}
	public void setShowSubmitButton(String showSubmitButton) {
		ShowSubmitButton = showSubmitButton;
	}
	public String getShowRejectButton() {
		return ShowRejectButton;
	}
	public void setShowRejectButton(String showRejectButton) {
		ShowRejectButton = showRejectButton;
	}
	public String getShowRejectMessage() {
		return ShowRejectMessage;
	}
	public void setShowRejectMessage(String showRejectMessage) {
		ShowRejectMessage = showRejectMessage;
	}
	public String getShowViewButton() {
		return ShowViewButton;
	}
	public void setShowViewButton(String showViewButton) {
		ShowViewButton = showViewButton;
	}
	public String getReportStatus() {
		return reportStatus;
	}
	public void setReportStatus(String reportStatus) {
		this.reportStatus = reportStatus;
	}
	public int getNoOfTimesReportSubmitted() {
		return noOfTimesReportSubmitted;
	}
	public void setNoOfTimesReportSubmitted(int noOfTimesReportSubmitted) {
		this.noOfTimesReportSubmitted = noOfTimesReportSubmitted;
	}


}
