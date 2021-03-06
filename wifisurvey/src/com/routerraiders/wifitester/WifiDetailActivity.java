package com.routerraiders.wifitester;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

    private String mWifiId;

    @TargetApi(11)
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	this.setContentView(R.layout.activity_wifi_details);

	if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
	    getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	mSsid = (TextView) this.findViewById(R.id.detail_ssid);
	mBssid = (TextView) this.findViewById(R.id.detail_bssid);
	mLocation = (TextView) this.findViewById(R.id.detail_location);
	mLastSeen = (TextView) this.findViewById(R.id.detail_last_seen);
	mFrequency = (TextView) this.findViewById(R.id.detail_frequency);
	mSignal = (TextView) this.findViewById(R.id.detail_signal);
	mSecurity = (TextView) this.findViewById(R.id.detail_security);
	mPassword = (EditText) this.findViewById(R.id.detail_password);
	mNotes = (EditText) this.findViewById(R.id.detail_notes);

	mWifiId = getIntent().getStringExtra(WIFI_ID_KEY);

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

	Cursor cursor = database.query(WifiDatabaseHelper.TABLE_WIFI_INFO, queryColumns,
		WifiDatabaseHelper.COLUMN_BSSID + "=?", new String[] { mWifiId }, null, null, null);

	if (cursor.getCount() > 0) {

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
	}

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

	database.update(WifiDatabaseHelper.TABLE_WIFI_INFO, values, WifiDatabaseHelper.COLUMN_BSSID + "=?",
		new String[] { mWifiId });

	database.close();

	Log.d(TAG, "exiting onPause");
    }

    public void onLoginClick(View button) {

	// Pull network data straight off the UI - faster and it gives the most
	// recent data
	String ssid = mSsid.getText().toString();
	String bssid = mBssid.getText().toString();
	String security = mSecurity.getText().toString();
	String password = mPassword.getText().toString();

	WifiManager manager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);

	// Check to see if a configuration of SSID + BSSID already exists
	Integer netId = -1;
	List<WifiConfiguration> configured = manager.getConfiguredNetworks();
	for (WifiConfiguration config : configured) {
	    Log.i(TAG, config.SSID + " vs " + ssid);
	    Log.i(TAG, config.BSSID + " vs " + bssid);

	    if (config.BSSID == null)
		continue;
	    if (config.SSID == null)
		continue;

	    if (config.BSSID.equals(bssid) && config.SSID.equals("\"" + ssid + "\"")) {

		netId = config.networkId;
		Log.i(TAG, "Setting netId to " + netId.toString());
		break;
	    }
	}

	// If not, we'll create one
	if (netId == -1) {
	    WifiConfiguration wifiConfig = generateWifiConfig(ssid, bssid, security, password);
	    netId = manager.addNetwork(wifiConfig);
	    Log.i(TAG, "Generated new config with id " + netId.toString());
	}

	// Try to connect
	if (-1 != netId) {
	    if (manager.enableNetwork(netId, true)) {
		Toast.makeText(this.getApplicationContext(), R.string.login_successful, Toast.LENGTH_SHORT).show();
		Log.i(TAG, "enabled network " + netId.toString());
		manager.saveConfiguration();
	    } else {
		Toast.makeText(this.getApplicationContext(), R.string.login_failed, Toast.LENGTH_SHORT).show();
	    }
	} else {
	    Toast.makeText(this.getApplicationContext(), R.string.wifi_add_failure, Toast.LENGTH_SHORT).show();
	}

	Log.d(TAG, "exiting onLoginClick");
    }

    // Generate a new configuration based on the following params
    private WifiConfiguration generateWifiConfig(String ssid, String bssid, String security, String password) {
	WifiConfiguration config = new WifiConfiguration();
	config.SSID = "\"" + ssid + "\"";
	config.BSSID = bssid;
	config.status = WifiConfiguration.Status.ENABLED;
	config.priority = 1;

	if (security.contains("WPA")) {
	    config.preSharedKey = "\"" + password + "\"";
	    config.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
	} else if (security.contains("WEP")) {
	    config.wepKeys[0] = password;
	    config.wepTxKeyIndex = 0;
	    config.allowedKeyManagement.set(KeyMgmt.NONE);
	} else {
	    config.allowedKeyManagement.set(KeyMgmt.NONE);
	}

	Log.i(TAG, "Created configuration: " + config.SSID + " :: " + config.preSharedKey);

	return config;
    }
}
