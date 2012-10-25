package com.routerraiders.wifitester.utils;

import android.content.Context;
import android.net.wifi.WifiManager;

import com.routerraiders.wifitester.R;

public class ImageHandler {

    public static Integer getWifiIconResource(Context context, int signalStrength) {
	return getWifiIconResource(context, signalStrength, "");
    }

    public static Integer getWifiIconResource(Context context, int signalStrength, String capabilities) {
	Integer resourceId = R.drawable.wifi_strength_0;
	Boolean encrypted = (capabilities.contains("WPA") || capabilities.contains("WEP"));

	switch (WifiManager.calculateSignalLevel(signalStrength, 5)) {
	case 0:
	    if (encrypted) {
		resourceId = R.drawable.wifi_strength_locked_0;
	    } else {
		resourceId = R.drawable.wifi_strength_0;
	    }
	    break;

	case 1:
	    if (encrypted) {
		resourceId = R.drawable.wifi_strength_locked_1;
	    } else {
		resourceId = R.drawable.wifi_strength_1;
	    }
	    break;

	case 2:
	    if (encrypted) {
		resourceId = R.drawable.wifi_strength_locked_2;
	    } else {
		resourceId = R.drawable.wifi_strength_2;
	    }
	    break;

	case 3:
	    if (encrypted) {
		resourceId = R.drawable.wifi_strength_locked_3;
	    } else {
		resourceId = R.drawable.wifi_strength_3;
	    }
	    break;

	case 4:
	    if (encrypted) {
		resourceId = R.drawable.wifi_strength_locked_4;
	    } else {
		resourceId = R.drawable.wifi_strength_4;
	    }
	    break;

	default:
	    if (encrypted) {
		resourceId = R.drawable.wifi_strength_locked_0;
	    } else {
		resourceId = R.drawable.wifi_strength_0;
	    }
	    break;
	}

	return resourceId;
    }

}
