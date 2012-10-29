package com.routerraiders.wifitester;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ScanLogActivity extends ListActivity implements OnItemClickListener {
    /** Called when the activity is first created. */

    private final static String TAG = "ScanLogActivity";

    private static final int DIALOG_CLEAR_CHECK = 0;

    private WifiDatabaseHelper mWifiDatabaseHelper;
    private SQLiteDatabase mWifiDatabase;
    private ScanResultCursorAdapter mAdapter;

    @TargetApi(11)
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	this.setContentView(R.layout.activity_scan_log);

	if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
	    getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	ProgressBar emptyView = (ProgressBar) this.findViewById(R.id.scan_summary_progress);
	getListView().setEmptyView(emptyView);

	mAdapter = new ScanResultCursorAdapter(this, null);
	setListAdapter(mAdapter);
	getListView().setOnItemClickListener(this);

	Log.d(TAG, "exiting onCreate");
    }

    @Override
    protected void onResume() {
	super.onResume();

	mWifiDatabaseHelper = new WifiDatabaseHelper(ScanLogActivity.this);
	mWifiDatabase = mWifiDatabaseHelper.getReadableDatabase();

	updateWifiList();

	Log.d(TAG, "exiting onResume");
    }

    @Override
    protected void onPause() {
	super.onPause();

	mWifiDatabase.close();
	mWifiDatabaseHelper.close();

	Log.d(TAG, "exiting onPause");
    }

    @Override
    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
	Intent intent = new Intent(this, WifiDetailActivity.class);
	String bssid = String.valueOf(((TextView) v.findViewById(R.id.wifi_bssid_text)).getText());
	intent.putExtra(WifiDetailActivity.WIFI_ID_KEY, bssid);

	startActivity(intent);

	Log.d(TAG, "exiting onItemClick");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	MenuInflater inflater = getMenuInflater();
	inflater.inflate(R.menu.activity_scan_log, menu);
	return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	// Handle item selection

	switch (item.getItemId()) {
	case android.R.id.home:
	    NavUtils.navigateUpFromSameTask(this);
	    return true;
	case R.id.scan_menu_refresh:
	    updateWifiList();
	    return true;
	case R.id.scan_menu_clear:
	    this.showDialog(DIALOG_CLEAR_CHECK);
	    return true;
	default:
	    return super.onOptionsItemSelected(item);
	}
    }

    @Override
    protected Dialog onCreateDialog(int id) {
	switch (id) {
	case DIALOG_CLEAR_CHECK:
	    Builder builder = new AlertDialog.Builder(this);
	    builder.setMessage(R.string.dialog_confirm_clear);
	    builder.setCancelable(false);
	    builder.setPositiveButton(R.string.dialog_yes, new OnClickListener(){

		@Override
		public void onClick(DialogInterface arg0, int arg1) {
		    ScanLogActivity.this.clearWifiList();
		}
	    });
	    builder.setNegativeButton(R.string.dialog_no, new OnClickListener(){

		@Override
		public void onClick(DialogInterface dialog, int which) {
		    dialog.cancel();
		}
	    });
	    AlertDialog dialog = builder.create();
	    dialog.show();
	}
	
	return super.onCreateDialog(id);
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
    }

    private void updateWifiList() {
	new AsyncTask<Void, Void, Cursor>() {
	    private static final String TAG = "ScanLogActivity.updateTask";

	    @Override
	    protected Cursor doInBackground(Void... arg0) {

		String[] queryColumns = { WifiDatabaseHelper.COLUMN_ID, WifiDatabaseHelper.COLUMN_SSID,
			WifiDatabaseHelper.COLUMN_BSSID, WifiDatabaseHelper.COLUMN_LEVEL,
			WifiDatabaseHelper.COLUMN_SECURITY };

		Cursor cursor = mWifiDatabase.query(WifiDatabaseHelper.TABLE_WIFI_INFO, queryColumns, "", null, null,
			null, WifiDatabaseHelper.COLUMN_LAST_SEEN + " DESC");

		Log.d(TAG, "exiting doInBackground");
		return cursor;
	    }

	    @Override
	    protected void onPostExecute(Cursor cursor) {
		mAdapter.changeCursor(cursor);
		mAdapter.notifyDataSetChanged();
		Log.d(TAG, "exiting onPostExecute");
	    }
	}.execute();
    }
}
