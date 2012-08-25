package com.jim.data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jim.common.Utils;

public class DatabaseProvider {
	private static DatabaseProvider mDatabaseProvider = null;
	private DatabaseHelper mDBHelper = null;
	
	private DatabaseProvider(Context context) {
		mDBHelper = new DatabaseHelper(context);
	}
	
	public synchronized static DatabaseProvider getInstance() {
		if(mDatabaseProvider == null) {
			mDatabaseProvider = new DatabaseProvider(DataProviderManager.getAppContext());
		}
		
		return mDatabaseProvider;
	}
	
	public List<StreamInfo> getListByType(String typeName) {
		List<StreamInfo> list = new ArrayList<StreamInfo>();
		
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		Cursor cs = db.query(typeName, null, null, null, null, null, null);
		if(cs.moveToFirst()) {
			do {
				StreamInfo info = new StreamInfo();
				setStreamInfo(cs, info);
				
				list.add(info);
			} while(cs.moveToNext());
		}
		cs.close();
		return list;
	}
	
	public List<String> getAllTypeName() {
		ArrayList<String> list = new ArrayList<String>();
		
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		
		Cursor cs = db.query("sqlite_master", null, "type=?", new String[]{"table"}, null, null, null);
		
		if(cs.moveToFirst()) {
			do {
				int index = cs.getColumnIndex("name");
				list.add(cs.getString(index));
			} while(cs.moveToNext());
		} else {
			return null;
		}
		cs.close();
		return list;
	}
	
	private void setStreamInfo(Cursor cs, StreamInfo info) {
		int index = cs.getColumnIndex("title");
		info.setTitle(cs.getString(index));
		
		index = cs.getColumnIndex("url");
		info.setUrl(cs.getString(index));
	}
	
	public boolean isTypeNameExists(String name) {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		
		Cursor cs = db.query("sqlite_master", null, "type=?", new String[]{"table"}, null, null, null);
		
		if(cs.moveToFirst()) {
			do {
				int index = cs.getColumnIndex("name");
				if(cs.getString(index).equals(name)) {
					return true;
				}
			} while(cs.moveToNext());
		} else {
			return false;
		}
		cs.close();
		return false;
	}
	
	public boolean addItemToPersonalList(StreamInfo info, String typeName) {
		boolean result = false;
		
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		if(isTypeNameExists(typeName)) {
			addItemToTable(info, typeName);
			result = true;
		} else {
			if(Utils.isNumber(typeName)) {
				typeName = "_" + typeName;
			}
			String sql = "CREATE TABLE IF NOT EXISTS " + 
									typeName +
									"(title TEXT PRIMARY KEY," +
									"url TEXT NOT NULL)";
			
			try {
				db.execSQL(sql);
				
				addItemToTable(info, typeName);
				result = true;
			} catch (Exception e) {
				result = false;
			}
		}
		
		return result;
	}
	
	private void addItemToTable(StreamInfo info, String tableName) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		
		Cursor cs = db.query(tableName, null, "title=?", new String[]{info.getTitle()}, null, null, null);
		ContentValues values = setValues(info,tableName);
		if(cs.moveToFirst()) {
			db.update(tableName, values, "title=?", new String[]{info.getTitle()});
		} else {
			db.insert(tableName, null, values);
		}
		cs.close();
	}
	
	private ContentValues setValues(StreamInfo info, String tableName) {
		ContentValues values = new ContentValues();
		
		values.put("title", info.getTitle());
		values.put("url", info.getUrl());
		
		return values;
	}
	
	public boolean changeTypeName(String oldName, String newName) {
		List<StreamInfo> oldList = getListByType(oldName);
		for(int i = 0 ; i < oldList.size() ; i++) {
			addItemToPersonalList(oldList.get(i), newName);
		}
		
		return deleteType(oldName);
	}
	
	public boolean deleteType(String tableName) {
		boolean result = false;
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		
		String sql = "DROP TABLE " + tableName;
		
		try {
			db.execSQL(sql);
			result = true;
		} catch (Exception e) {
			result = false;
		}
		
		return result;
	}
	
	public boolean removeItemFromType(String tableName, String title) {
		boolean result = false;
		
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		
		String sql = "DELETE FROM " + tableName + " WHERE title='" + title + "'";
		
		try {
			db.execSQL(sql);
			result = true;
		} catch (Exception e) {
			result = false;
		}
		
		return result;
	}
}
