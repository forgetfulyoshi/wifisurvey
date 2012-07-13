package com.routerraiders.wifitester;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

public class WifiDetailActivity extends Activity {

    private static final String TAG = "WifiDetailActivity";
    public static final String WIFI_ID_KEY = TAG + "_WifiId";

    private TextView mSsid;
    private TextView mBssid;
    private TextView mLocation;
    private TextView mLastSeen;
    private TextView mFrequency;
    private TextView mSignal;
    private TextView mSecurity;
    private EditText mPassword;
    private EditText mNotes;
 
    private Long mWifiId;
    

    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	this.setContentView(R.layout.wifi_details);

	mSsid = (TextView) this.findViewById(R.id.detail_ssid);
	mBssid = (TextView) this.findViewById(R.id.detail_bssid);
	mLocation = (TextView) this.findViewById(R.id.detail_location);
	mLastSeen = (TextView) this.findViewById(R.id.detail_last_seen);
	mFrequency = (TextView) this.findViewById(R.id.detail_frequency);
	mSignal = (TextView) this.findViewById(R.id.detail_signal);
	mSecurity = (TextView) this.findViewById(R.id.detail_security);
	mPassword = (EditText) this.findViewById(R.id.detail_password);
	mNotes = (EditText) this.findViewById(R.id.detail_notes);
	
	mWifiId = this.getIntent().getLongExtra(WIFI_ID_KEY, 0);

	Log.d(TAG, "exiting onCreate");
    }

    public void onStart() {
	super.onStart();

	WifiDatabaseHelper helper = new WifiDatabaseHelper(this);
	SQLiteDatabase database = helper.getReadableDatabase();

	String[] queryColumns = { WifiDatabaseHelper.COLUMN_ID, WifiDatabaseHelper.COLUMN_SSID,
		WifiDatabaseHelper.COLUMN_BSSID, WifiDatabaseHelper.COLUMN_LOCATION,
		WifiDatabaseHelper.COLUMN_LAST_SEEN, WifiDatabaseHelper.COLUMN_SECURITY,
		WifiDatabaseHelper.COLUMN_PASSWORD, WifiDatabaseHelper.COLUMN_NOTES,
		WifiDatabaseHelper.COLUMN_FREQUENCY, WifiDatabaseHelper.COLUMN_LEVEL };

	Cursor cursor = database.query(WifiDatabaseHelper.TABLE_WIFI_INFO, queryColumns, WifiDatabaseHelper.COLUMN_ID
		+ "=?", new String[] { mWifiId.toString() }, null, null, null);

	cursor.moveToFirst();
	String ssid = cursor.getString(cursor.getColumnIndex(WifiDatabaseHelper.COLUMN_SSID));
	String bssid = cursor.getString(cursor.getColumnIndex(WifiDatabaseHelper.COLUMN_BSSID));
	String location = cursor.getString(cursor.getColumnIndex(WifiDatabaseHelper.COLUMN_LOCATION));
	Long lastSeen = cursor.getLong(cursor.getColumnIndex(WifiDatabaseHelper.COLUMN_LAST_SEEN));
	String security = cursor.getString(cursor.getColumnIndex(WifiDatabaseHelper.COLUMN_SECURITY));
	String password = cursor.getString(cursor.getColumnIndex(WifiDatabaseHelper.COLUMN_PASSWORD));
	String notes = cursor.getString(cursor.getColumnIndex(WifiDatabaseHelper.COLUMN_NOTES));
	Long frequency = cursor.getLong(cursor.getColumnIndex(WifiDatabaseHelper.COLUMN_FREQUENCY));
	Long level = cursor.getLong(cursor.getColumnIndex(WifiDatabaseHelper.COLUMN_LEVEL));

	mSsid.setText(ssid);
	mBssid.setText(bssid);
	mLocation.setText(location);

	Date date = new Date(lastSeen);
	SimpleDateFormat formatter = new SimpleDateFormat("MMMM d yyy 'at' hh:mm:ss");
	String formattedDateString = formatter.format(date);
	mLastSeen.setText(formattedDateString);
	
	mFrequency.setText(frequency.toString() + " MHz");
	mSignal.setText(level.toString() + " dBm");
	
	mSecurity.setText(security);
	mPassword.setText(password);
	mNotes.setText(notes);

	cursor.close();
	database.close();
	
	Log.d(TAG, "exiting onStart");
    }

    public void onPause() {
	super.onPause();

	WifiDatabaseHelper helper = new WifiDatabaseHelper(this);
	SQLiteDatabase database = helper.getWritableDatabase();

	ContentValues values = new ContentValues();
	values.put(WifiDatabaseHelper.COLUMN_PASSWORD, mPassword.getText().toString());
	values.put(WifiDatabaseHelper.COLUMN_NOTES, mNotes.getText().toString());

	database.update(WifiDatabaseHelper.TABLE_WIFI_INFO, values, WifiDatabaseHelper.COLUMN_ID + "=?",
		new String[] { mWifiId.toString() });

	database.close();
	
	Log.d(TAG, "exiting onPause");
    }

    public void onStop() {
	super.onStop();
	Log.d(TAG, "exiting onStop");
    }

    public void onDestroy() {
	super.onDestroy();

	Log.d(TAG, "exiting onDestroy");
    }
}
