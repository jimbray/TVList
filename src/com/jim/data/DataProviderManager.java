package com.jim.data;

import java.util.List;

import android.app.Application;
import android.content.Context;

public class DataProviderManager extends Application {
	private static DataProviderManager mRef;
	
	private DatabaseProvider mDatabaseProvider = null;

	@Override
	public void onCreate() {
		super.onCreate();
		mRef = this;
		mDatabaseProvider = DatabaseProvider.getInstance();
	}
	
	public static DataProviderManager getInstance() {
		return mRef;
	}
	
	public static Context getAppContext() {
		if(mRef == null) {
			return null;
		}
		return mRef.getApplicationContext();
	}
	
	public boolean isTypeNameExists(String name) {
		return mDatabaseProvider.isTypeNameExists(name);
	}
	
	public boolean addItemToPersonalList(StreamInfo info, String typeName) {
		return mDatabaseProvider.addItemToPersonalList(info, typeName);
	}
	
	public List<StreamInfo> getListByType(String typeName) {
		return mDatabaseProvider.getListByType(typeName);
	}
	
	public List<String> getAllTypeName() {
		return mDatabaseProvider.getAllTypeName();
	}
	
	public boolean changeTypeName(String oldName, String newName) {
		return mDatabaseProvider.changeTypeName(oldName, newName);
	}
	
	public boolean deleteType(String typeName) {
		return mDatabaseProvider.deleteType(typeName);
	}
	
	public boolean removeItemFromType(String typeName, String title) {
		return mDatabaseProvider.removeItemFromType(typeName, title);
	}
}
