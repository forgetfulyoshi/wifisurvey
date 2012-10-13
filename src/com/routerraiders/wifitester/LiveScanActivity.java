package com.routerraiders.wifitester;

import android.annotation.TargetApi;
import android.app.ListActivity;
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

	Log.d(TAG, "exiting onCreate");
    }

    @Override
    public void onResume() {
	super.onResume();
	Log.d(TAG, "exiting onResume");
    }

    @Override
    public void onPause() {
	super.onPause();
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
	// TODO Auto-generated method stub

    }

}
