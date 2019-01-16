package com.luminousinfoways.makemyreport.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class UserReports implements Parcelable {
	
	private String reportID;
	private String reportTitle;
	private String reportType;
	private String reportShortDesc;
	private String reportCreatedBy;
	private String reportCreatedOn;
	private String lastDateofSubmission;
	private String fbAssignUserID;
	private String recentSubmission;
	private String showSubmitButton;
	private String showRejectButton;
	private String showRejectMessage;
	private String showViewButton;
	private String reportStatus;
	
	public UserReports(Parcel in){
		readFromParcel(in);
	}

	public static final Parcelable.Creator<UserReports> CREATOR = new Parcelable.Creator<UserReports>() {
		@Override
		public UserReports createFromParcel(Parcel source) {
			return new UserReports(source);
		}
		@Override
		public UserReports[] newArray(int size) {
			return new UserReports[size];
		}
	};
	
	public String getReportID() {
		return reportID;
	}

	public void setReportID(String reportID) {
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

	public String getReportShortDesc() {
		return reportShortDesc;
	}

	public void setReportShortDesc(String reportShortDesc) {
		this.reportShortDesc = reportShortDesc;
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
		return lastDateofSubmission;
	}

	public void setLastDateofSubmission(String lastDateofSubmission) {
		this.lastDateofSubmission = lastDateofSubmission;
	}

	public String getFbAssignUserID() {
		return fbAssignUserID;
	}

	public void setFbAssignUserID(String fbAssignUserID) {
		this.fbAssignUserID = fbAssignUserID;
	}

	public String getRecentSubmission() {
		return recentSubmission;
	}

	public void setRecentSubmission(String recentSubmission) {
		this.recentSubmission = recentSubmission;
	}

	public String getShowSubmitButton() {
		return showSubmitButton;
	}

	public void setShowSubmitButton(String showSubmitButton) {
		this.showSubmitButton = showSubmitButton;
	}

	public String getShowRejectButton() {
		return showRejectButton;
	}

	public void setShowRejectButton(String showRejectButton) {
		this.showRejectButton = showRejectButton;
	}

	public String getShowRejectMessage() {
		return showRejectMessage;
	}

	public void setShowRejectMessage(String showRejectMessage) {
		this.showRejectMessage = showRejectMessage;
	}

	public String getShowViewButton() {
		return showViewButton;
	}

	public void setShowViewButton(String showViewButton) {
		this.showViewButton = showViewButton;
	}

	public String getReportStatus() {
		return reportStatus;
	}

	public void setReportStatus(String reportStatus) {
		this.reportStatus = reportStatus;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(reportID);
		dest.writeString(reportTitle);
		dest.writeString(reportType);
		dest.writeString(reportShortDesc);
		dest.writeString(reportCreatedBy);
		dest.writeString(reportCreatedOn);
		dest.writeString(lastDateofSubmission);
		dest.writeString(fbAssignUserID);
		dest.writeString(recentSubmission);
		dest.writeString(showSubmitButton);
		dest.writeString(showRejectButton);
		dest.writeString(showRejectMessage);
		dest.writeString(showViewButton);
		dest.writeString(reportStatus);
	}

	private void readFromParcel(Parcel dest){
		reportID = dest.readString();
		reportTitle = dest.readString();
		reportType = dest.readString();
		reportShortDesc = dest.readString();
		reportCreatedBy = dest.readString();
		reportCreatedOn = dest.readString();
		lastDateofSubmission = dest.readString();
		fbAssignUserID = dest.readString();
		recentSubmission = dest.readString();
		showSubmitButton = dest.readString();
		showRejectButton = dest.readString();
		showRejectMessage = dest.readString();
		showViewButton = dest.readString();
		reportStatus = dest.readString();
	}

}
