package com.routerraiders.wifitester;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
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
}
