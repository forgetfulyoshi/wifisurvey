package com.routerraiders.wifitester;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import android.annotation.TargetApi;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;

public class LiveScanActivity extends ListActivity implements OnItemClickListener {
    private static final String TAG = "LiveScanActivity";
    private ScanTask mScanTask;
    private ScanResultArrayAdapter mArrayAdapter;
    private List<ScanResult> mResults;

    @TargetApi(11)
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_live_scan);

	if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
	    getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	ProgressBar emptyView = (ProgressBar) findViewById(R.id.live_scan_progress);
	getListView().setEmptyView(emptyView);
	getListView().setOnItemClickListener(this);

	mResults = new ArrayList<ScanResult>();

	mArrayAdapter = new ScanResultArrayAdapter(this, R.layout.wifi_entry, mResults);
	getListView().setAdapter(mArrayAdapter);

	Log.d(TAG, "exiting onCreate");
    }

    @Override
    public void onResume() {
	super.onResume();
	mScanTask = new ScanTask();
	try {
	    mScanTask.execute(this);
	} catch (IllegalStateException e) {
	    Log.e(TAG, "Scan already running");
	}
	Log.d(TAG, "exiting onResume");
    }

    @Override
    public void onPause() {
	super.onPause();
	mScanTask.cancel(false);
	Log.d(TAG, "exiting onPause");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	getMenuInflater().inflate(R.menu.activity_live_scan, menu);
	return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	switch (item.getItemId()) {
	case android.R.id.home:
	    NavUtils.navigateUpFromSameTask(this);
	    return true;
	}
	return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
	String bssid = String.valueOf(((ScanResultArrayAdapter.ScanResultHolder) v.getTag()).bssidTxt.getText());
	Intent showDetails = new Intent(this, WifiDetailActivity.class);
	showDetails.putExtra(WifiDetailActivity.WIFI_ID_KEY, bssid);

	startActivity(showDetails);

    }

    private class ScanTask extends AsyncTask<Context, ScanResult, Void> {

	@Override
	protected Void doInBackground(Context... params) {
	    Context context = params[0];
	    WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

	    do {
		Log.v(TAG, "Running scan...");
		if (manager.isWifiEnabled()) {

		    manager.startScan();
		    List<ScanResult> results = manager.getScanResults();

		    if (results != null && !results.isEmpty()) {
			publishProgress(results.toArray(new ScanResult[] {}));
		    }
		}

		try {
		    Thread.sleep(10 * 1000);
		} catch (InterruptedException e) {
		    Log.e(TAG, "Could not sleep", e);
		}

	    } while (!isCancelled());
	    Log.d(TAG, "Got cancelled");

	    return null;
	}

	@Override
	protected void onProgressUpdate(ScanResult... scanResults) {
	    List<ScanResult> results = Arrays.asList(scanResults);

	    mArrayAdapter.clear();

	    for (ScanResult result : results) {
		mArrayAdapter.add(result);
	    }
	    mArrayAdapter.sort(new Comparator<ScanResult>() {

		@Override
		public int compare(ScanResult arg0, ScanResult arg1) {
		    return arg1.level - arg0.level;
		}

	    });
	    mArrayAdapter.notifyDataSetChanged();
	}

    }

}
