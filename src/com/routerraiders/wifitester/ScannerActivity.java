package com.routerraiders.wifitester;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class ScannerActivity extends ListActivity implements OnItemClickListener {
    /** Called when the activity is first created. */

    private final static String TAG = "ScannerActivity";
    private final static int DIALOG_ACTIVATE_WIFI = 1;

    private WifiBroadcastReceiver mWifiReceiver;
    private WifiDatabaseHelper mWifiDatabaseHelper;
    private SQLiteDatabase mWifiDatabase;
    private SimpleCursorAdapter mAdapter;
    private WifiManager mWifiManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	this.setContentView(R.layout.main);

	this.getListView().setOnItemClickListener(this);

	mWifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
	mWifiReceiver = new WifiBroadcastReceiver();
	mWifiDatabaseHelper = new WifiDatabaseHelper(this);
	mWifiDatabase = mWifiDatabaseHelper.getReadableDatabase();

	String[] queryColumns = { WifiDatabaseHelper.COLUMN_ID, WifiDatabaseHelper.COLUMN_SSID,
		WifiDatabaseHelper.COLUMN_BSSID };

	Cursor cursor = mWifiDatabase.query(WifiDatabaseHelper.TABLE_WIFI_INFO, queryColumns, "", null, null, null,
		WifiDatabaseHelper.COLUMN_LAST_SEEN + " DESC");

	String[] fromColumns = { WifiDatabaseHelper.COLUMN_SSID, WifiDatabaseHelper.COLUMN_BSSID };
	int[] toViews = { R.id.ssid_text, R.id.bssid_text };

	mAdapter = new SimpleCursorAdapter(this, R.layout.wifi_entry, cursor, fromColumns, toViews);

	this.setListAdapter(mAdapter);

	Log.d(TAG, "exiting onCreate");
    }

    @Override
    protected void onStart() {
	super.onStart();
	// The activity is about to become visible.

	this.registerReceiver(mWifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

	this.scanWifi();

	if (!this.getListAdapter().isEmpty()) {
	    TextView hint = (TextView) this.findViewById(R.id.main_refresh);
	    hint.setVisibility(View.GONE);
	}

	Log.d(TAG, "exiting onStart");
    }

    @Override
    protected void onResume() {
	super.onResume();
	// The activity has become visible (it is now "resumed")

	Log.d(TAG, "exiting onResume");
    }

    @Override
    protected void onPause() {
	super.onPause();
	// Another activity is taking focus (this activity is about to be
	// "paused").

	Log.d(TAG, "exiting onPause");
    }

    @Override
    protected void onStop() {
	super.onStop();
	// The activity is no longer visible (it is now "stopped")

	this.unregisterReceiver(mWifiReceiver);
	Log.d(TAG, "exiting onStop");
    }

    @Override
    protected void onDestroy() {
	super.onDestroy();
	// The activity is about to be destroyed.
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	Intent intent = new Intent(this, WifiDetailActivity.class);
	intent.putExtra(WifiDetailActivity.WIFI_ID_KEY, id);

	this.startActivity(intent);

	Log.d(TAG, "exiting onItemClick");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	MenuInflater inflater = getMenuInflater();
	inflater.inflate(R.menu.scan_menu, menu);
	return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	// Handle item selection
	
	switch (item.getItemId()) {
	case R.id.scan_menu_refresh:
	    this.updateWifiList();
	    return true;
	case R.id.scan_menu_clear:
	    this.clearWifiList();
	    return true;
	default:
	    return super.onOptionsItemSelected(item);
	}
    }

    @Override
    protected Dialog onCreateDialog(int id) {
	Dialog dialog;
	switch (id) {
	case DIALOG_ACTIVATE_WIFI:
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setMessage(R.string.dialog_activate_wifi);
	    builder.setCancelable(false);
	    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

		public void onClick(DialogInterface dialog, int which) {
		    ScannerActivity.this.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
		}
	    });

	    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

		public void onClick(DialogInterface dialog, int which) {
		    dialog.cancel();
		}
	    });

	    dialog = builder.create();
	    break;
	
	default:
	    dialog = null;
	}
	return dialog;
    }

    private void clearWifiList() {
	mWifiDatabase.delete(WifiDatabaseHelper.TABLE_WIFI_INFO, null, null);
	mAdapter.getCursor().requery();
	mAdapter.notifyDataSetChanged();

	if (this.getListAdapter().isEmpty()) {
	    TextView hint = (TextView) this.findViewById(R.id.main_refresh);
	    hint.setVisibility(View.VISIBLE);
	}

	this.scanWifi();

    }

    private void scanWifi() {
	if (!mWifiManager.isWifiEnabled()) {
	    this.showDialog(DIALOG_ACTIVATE_WIFI);
	}

	mWifiManager.startScan();
    }

    private void updateWifiList() {
	this.scanWifi();
	mAdapter.getCursor().requery();
	mAdapter.notifyDataSetChanged();

	if (!this.getListAdapter().isEmpty()) {
	    TextView hint = (TextView) this.findViewById(R.id.main_refresh);
	    hint.setVisibility(View.GONE);
	}
    }
}
