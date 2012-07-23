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
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class ScannerActivity extends ListActivity implements OnItemClickListener {
    /** Called when the activity is first created. */

    //private final static String TAG = "ScannerActivity";
    private final static int DIALOG_ACTIVATE_WIFI = 1;

    private WifiDatabaseHelper mWifiDatabaseHelper;
    private SQLiteDatabase mWifiDatabase;
    private WifiBroadcastReceiver mWifiReceiver;
    private SimpleCursorAdapter mAdapter;
    private WifiManager mWifiManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	TextView emptyView = new TextView(this);
	emptyView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	emptyView.setGravity(Gravity.CENTER);
	emptyView.setText(R.string.main_refresh_hint);
	emptyView.setTextAppearance(this, android.R.attr.textAppearanceMedium);
	((ViewGroup) getListView().getParent()).addView(emptyView);
	getListView().setEmptyView(emptyView);

	mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
	mWifiReceiver = new WifiBroadcastReceiver();

	String[] fromColumns = { WifiDatabaseHelper.COLUMN_SSID, WifiDatabaseHelper.COLUMN_BSSID };
	int[] toViews = { R.id.ssid_text, R.id.bssid_text };

	mAdapter = new SimpleCursorAdapter(this, R.layout.wifi_entry, null, fromColumns, toViews);
	setListAdapter(mAdapter);
	getListView().setOnItemClickListener(this);

	//Log.d(TAG, "exiting onCreate");
    }

    @Override
    protected void onResume() {
	super.onResume();

	registerReceiver(mWifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
	mWifiDatabaseHelper = new WifiDatabaseHelper(ScannerActivity.this);
	mWifiDatabase = mWifiDatabaseHelper.getReadableDatabase();
	scanWifi();
	updateWifiList();

	//Log.d(TAG, "exiting onResume");
    }

    @Override
    protected void onPause() {
	super.onPause();

	mWifiDatabase.close();
	mWifiDatabaseHelper.close();
	unregisterReceiver(mWifiReceiver);

	//Log.d(TAG, "exiting onPause");
    }

    @Override
    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
	Intent intent = new Intent(this, WifiDetailActivity.class);
	intent.putExtra(WifiDetailActivity.WIFI_ID_KEY, id);

	startActivity(intent);

	//Log.d(TAG, "exiting onItemClick");
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
	    updateWifiList();
	    return true;
	case R.id.scan_menu_clear:
	    clearWifiList();
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

    private void scanWifi() {
	if (!mWifiManager.isWifiEnabled()) {
	    showDialog(DIALOG_ACTIVATE_WIFI);
	}
	mWifiManager.startScan();
    }

    private void clearWifiList() {
	new AsyncTask<Void, Void, Void>() {

	    @Override
	    protected Void doInBackground(Void... params) {

		mWifiDatabase.delete(WifiDatabaseHelper.TABLE_WIFI_INFO, null, null);

		return null;
	    }

	    @Override
	    protected void onPostExecute(Void result) {
		mAdapter.changeCursor(null);
		mAdapter.notifyDataSetChanged();
	    }
	}.execute();

	scanWifi();
    }

    private void updateWifiList() {
	scanWifi();
	new UpdateTask().execute();
    }

    private class UpdateTask extends AsyncTask<Void, Void, Cursor> {
	private static final String TAG = "ScannerActivity.updateTask";

	@Override
	protected Cursor doInBackground(Void... arg0) {

	    String[] queryColumns = { WifiDatabaseHelper.COLUMN_ID, WifiDatabaseHelper.COLUMN_SSID,
		    WifiDatabaseHelper.COLUMN_BSSID };

	    Cursor cursor = mWifiDatabase.query(WifiDatabaseHelper.TABLE_WIFI_INFO, queryColumns, "", null, null, null,
		    WifiDatabaseHelper.COLUMN_LAST_SEEN + " DESC");

	    //Log.d(TAG, "exiting doInBackground");
	    return cursor;
	}

	@Override
	protected void onPostExecute(Cursor cursor) {
	    mAdapter.changeCursor(cursor);
	    mAdapter.notifyDataSetChanged();
	    //Log.d(TAG, "exiting onPostExecute");
	}
    }
}
