package com.luminousinfoways.makemyreport.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class UserDetails implements Parcelable{
		
	String loginStatus;
	String loginMessage;
	String userName;
	String userFullName;
	String userLastLoggedIn;
	String userID;
	
	public static final Parcelable.Creator<UserDetails> CREATOR = new Parcelable.Creator<UserDetails>() {
		
		@Override
		public UserDetails createFromParcel(Parcel source) {
			return new UserDetails(source);
		}
		
		@Override
		public UserDetails[] newArray(int size) {
			UserDetails[] userDetailsArray = new UserDetails[size]; 
			return  userDetailsArray;
		}
	};
	
	public UserDetails(Parcel in){
		readFromParcel(in);
	}
	
	public String getLoginStatus() {
		return loginStatus;
	}
	public void setLoginStatus(String loginStatus) {
		this.loginStatus = loginStatus;
	}
	public String getLoginMessage() {
		return loginMessage;
	}
	public void setLoginMessage(String loginMessage) {
		this.loginMessage = loginMessage;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserFullName() {
		return userFullName;
	}
	public void setUserFullName(String userFullName) {
		this.userFullName = userFullName;
	}
	public String getUserLastLoggedIn() {
		return userLastLoggedIn;
	}
	public void setUserLastLoggedIn(String userLastLoggedIn) {
		this.userLastLoggedIn = userLastLoggedIn;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(loginStatus);
		dest.writeString(loginMessage);
		dest.writeString(userName);
		dest.writeString(userFullName);
		dest.writeString(userLastLoggedIn);
		dest.writeString(userID);
	}
	
	private void readFromParcel(Parcel in){
		loginStatus = in.readString();
		loginMessage = in.readString();
		userName = in.readString();
		userFullName = in.readString();
		userLastLoggedIn = in.readString();
		userID = in.readString();
	}
}
