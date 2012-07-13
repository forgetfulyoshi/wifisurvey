package com.routerraiders.wifitester;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiScannerService extends IntentService {
	private static final String TAG = "WifiScannerService";

	private WifiManager mWifiService;
	private WifiBroadcastReceiver mWifiReceiver;
	private Boolean mOriginalWifiState;

	public WifiScannerService() {
		super("WifiScannerService");
		Log.d(TAG, "Constructed");
	}

	public void onCreate() {
		super.onCreate();
		mWifiService = (WifiManager) this
				.getSystemService(Context.WIFI_SERVICE);
		
		mOriginalWifiState = mWifiService.isWifiEnabled();
		
		if (false == mOriginalWifiState) {
			mWifiService.setWifiEnabled(true);
		}

		mWifiReceiver = new WifiBroadcastReceiver();

		this.registerReceiver(mWifiReceiver, new IntentFilter(
				WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

		Log.d(TAG, "exiting onCreate");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// Start a wifi scan and sleep
		Log.d(WifiScannerService.TAG, "entering onHandleIntent");

		if (mWifiService.isWifiEnabled()) {
			mWifiService.startScan();

			try {
				Thread.sleep(10 * 1000);
			} catch (InterruptedException e) {
				Log.e(TAG, "Could not sleep", e);
			}
		}
	}

	public void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(mWifiReceiver);
		mWifiService.setWifiEnabled(mOriginalWifiState);
	}
}
