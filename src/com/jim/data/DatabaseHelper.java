package com.jim.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	private static final String DB_NAME = "database.db";
	private static final int VERSION = 1;
	
//	private static final String CREATE_TABLE_CUSTOM_TYPE = "CREATE TABLE IF NOT EXISTS " + 
//									TableNames.TABLE_NAME_CUSTOM_TYPE + 
//									"(type_id INTEGER PRIMARY KEY," +
//									"type_name TEXT NOT NULL)";
//	
//	private static final String CREATE_TABLE_CUSTOM_LIST = "CREATE TABLE IF NOT EXISTS " + 
//									TableNames.TABLE_NAME_CUSTOM_LIST +
//									"(title TEXT PRIMARY KEY," +
//									"url TEXT)";
	
	public DatabaseHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
//		db.execSQL(CREATE_TABLE_CUSTOM_TYPE);
//		db.execSQL(CREATE_TABLE_CUSTOM_LIST);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
