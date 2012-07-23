package com.routerraiders.wifitester;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

public class WifiBroadcastReceiver extends BroadcastReceiver {

    //private static final String TAG = "WifiBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
	WifiManager wifiService = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

	WifiDatabaseHelper databaseHelper = new WifiDatabaseHelper(context);
	SQLiteDatabase database = databaseHelper.getWritableDatabase();

	List<ScanResult> results = wifiService.getScanResults();

	for (ScanResult result : results) {

	    Cursor cursor = database.query(WifiDatabaseHelper.TABLE_WIFI_INFO,
		    new String[] { WifiDatabaseHelper.COLUMN_BSSID }, WifiDatabaseHelper.COLUMN_BSSID + " = ? ",
		    new String[] { result.BSSID }, null, null, null);

	    ContentValues values = new ContentValues();
	    values.put(WifiDatabaseHelper.COLUMN_BSSID, result.BSSID);
	    values.put(WifiDatabaseHelper.COLUMN_SSID, result.SSID);
	    values.put(WifiDatabaseHelper.COLUMN_SECURITY, result.capabilities);
	    values.put(WifiDatabaseHelper.COLUMN_LAST_SEEN, System.currentTimeMillis());
	    values.put(WifiDatabaseHelper.COLUMN_FREQUENCY, result.frequency);
	    values.put(WifiDatabaseHelper.COLUMN_LEVEL, result.level);

	    if (0 == cursor.getCount()) {

		try {
		    database.insertOrThrow(WifiDatabaseHelper.TABLE_WIFI_INFO, null, values);
		} catch (SQLException e) {
		    //Log.e(TAG, "Error inserting new result into wifi database", e);
		    throw e;
		}
	    } else {

		database.update(WifiDatabaseHelper.TABLE_WIFI_INFO, values, WifiDatabaseHelper.COLUMN_BSSID + "=?",
			new String[] { result.BSSID });

	    }
	    cursor.close();
	}
	database.close();

	//Log.d(TAG, "exiting onReceive");
    }
}
