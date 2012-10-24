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

	switch (WifiManager.calculateSignalLevel(signalStrength, 5)) {
	case 0:
	    resourceId = R.drawable.wifi_strength_0;
	    break;

	case 1:
	    resourceId = R.drawable.wifi_strength_1;
	    break;

	case 2:
	    resourceId = R.drawable.wifi_strength_2;
	    break;

	case 3:
	    resourceId = R.drawable.wifi_strength_3;
	    break;

	case 4:
	    resourceId = R.drawable.wifi_strength_4;
	    break;
	    
	default:
	    resourceId = R.drawable.wifi_strength_0;
	    break;
	}

	return resourceId;
    }

}
