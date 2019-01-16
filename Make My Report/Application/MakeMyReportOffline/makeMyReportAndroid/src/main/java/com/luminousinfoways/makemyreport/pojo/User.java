package com.luminousinfoways.makemyreport.pojo;

import java.util.List;

public class User {

	String apikey;
	String name;
	int loginStatus;
	String contact_no;
	String dob;
	String org_id;
	public String getOrg_id() {
		return org_id;
	}
	public void setOrg_id(String org_id) {
		this.org_id = org_id;
	}
	public String getOrg_name() {
		return org_name;
	}
	public void setOrg_name(String org_name) {
		this.org_name = org_name;
	}
	String org_name;
	
	List<Report> submitedReport = null;
	List<Report> expierdReport = null;
	List<Report> rejectedReport = null;
	List<Report> pendingReport = null;
	
	public List<Report> getSubmitedReport() {
		return submitedReport;
	}
	public void setSubmitedReport(List<Report> submitedReport) {
		this.submitedReport = submitedReport;
	}
	public List<Report> getExpierdReport() {
		return expierdReport;
	}
	public void setExpierdReport(List<Report> expierdReport) {
		this.expierdReport = expierdReport;
	}
	public List<Report> getRejectedReport() {
		return rejectedReport;
	}
	public void setRejectedReport(List<Report> rejectedReport) {
		this.rejectedReport = rejectedReport;
	}
	public List<Report> getPendingReport() {
		return pendingReport;
	}
	public void setPendingReport(List<Report> pendingReport) {
		this.pendingReport = pendingReport;
	}
	public String getApikey() {
		return apikey;
	}
	public void setApikey(String apikey) {
		this.apikey = apikey;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getLoginStatus() {
		return loginStatus;
	}
	public void setLoginStatus(int loginStatus) {
		this.loginStatus = loginStatus;
	}
	public String getContact_no() {
		return contact_no;
	}
	public void setContact_no(String contact_no) {
		this.contact_no = contact_no;
	}
	public String getDob() {
		return dob;
	}
	public void setDob(String dob) {
		this.dob = dob;
	}
	
}
