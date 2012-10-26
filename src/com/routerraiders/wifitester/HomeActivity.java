package com.routerraiders.wifitester;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class HomeActivity extends Activity {

    private static final String TAG = "HomeActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_home);

	Intent scanServiceStart = new Intent(this, WifiScannerService.class);
	startService(scanServiceStart);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	getMenuInflater().inflate(R.menu.activity_home, menu);
	return true;
    }

    public void onScanLogClicked(View v) {
	Log.d(TAG, "onScanLogClicked");
	Intent launch = new Intent(this, ScanLogActivity.class);
	startActivity(launch);
    }

    public void onLiveScanClicked(View v) {
	Log.d(TAG, "onLiveScanClicked");
	Intent launch = new Intent(this, LiveScanActivity.class);
	startActivity(launch);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
	switch (item.getItemId()) {
	case R.id.menu_about:
	    AboutDialog about = new AboutDialog(this);
	    about.setTitle(R.string.menu_about);
	    about.show();
	    break;
	}
	return true;
    }
}
