package com.jim.data;

import android.os.Parcel;
import android.os.Parcelable;

public class StreamInfo implements Parcelable {
	private String mTitle = null;
	private String mUrl = null;
	
	public StreamInfo() {
		
	}
	
	public String getTitle() {
		return mTitle;
	}
	
	public void setTitle(String mTitle) {
		this.mTitle = mTitle;
	}
	
	public String getUrl() {
		return mUrl;
	}
	
	public void setUrl(String mUrl) {
		this.mUrl = mUrl;
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(mTitle);
		dest.writeString(mUrl);
	}
	
	public static final Parcelable.Creator<StreamInfo> CREATOR = new Parcelable.Creator<StreamInfo>() {

		@Override
		public StreamInfo createFromParcel(Parcel in) {
			return new StreamInfo(in);
		}

		@Override
		public StreamInfo[] newArray(int size) {
			return new StreamInfo[size];
		}
		
	};
	
	private StreamInfo(Parcel in) {
		mTitle = in.readString();
		mUrl = in.readString();
	}
}
