package com.routerraiders.wifitester;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class WifiDatabaseHelper extends SQLiteOpenHelper {

	private static final String TAG = "WifiDatabaseHelper";
	
	private static final String DATABASE_NAME = "wifi_info.db";
	private static final Integer DATABASE_VERSION = 2;
	
	public static final String TABLE_WIFI_INFO = "wifi_info";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_SSID = "ssid";
	public static final String COLUMN_BSSID = "bssid";
	public static final String COLUMN_TYPE = "type";
	public static final String COLUMN_SECURITY = "security";
	public static final String COLUMN_PASSWORD = "password";
	public static final String COLUMN_NOTES = "notes";
	public static final String COLUMN_LOCATION = "location";
	public static final String COLUMN_LAST_SEEN = "last_seen";
	public static final String COLUMN_FREQUENCY = "frequency";
	public static final String COLUMN_LEVEL = "level";
	
	private static final String DATABASE_CREATE = "CREATE TABLE " 
			+ TABLE_WIFI_INFO + "(" 
			+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
			+ COLUMN_SSID + " TEXT NOT NULL, "
			+ COLUMN_BSSID + " TEXT NOT NULL, "
			+ COLUMN_TYPE + " TEXT, "
			+ COLUMN_SECURITY + " TEXT, "
			+ COLUMN_PASSWORD + " TEXT, "
			+ COLUMN_NOTES + " TEXT, "
			+ COLUMN_LOCATION + " TEXT, "
			+ COLUMN_LAST_SEEN + " INTEGER, "
			+ COLUMN_FREQUENCY + " INTEGER, "
			+ COLUMN_LEVEL + " INTEGER"
			+ ");";
	
	public WifiDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
		Log.d(TAG, "exiting onCreate");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(TAG, "destroying and re-creating database");
		
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_WIFI_INFO);
		onCreate(db);
		
		Log.d(TAG, "exiting onUpgrade");
	}

}
