package com.routerraiders.wifitester;

import android.content.Context;
import android.database.Cursor;
import android.net.wifi.WifiManager;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ScanResultCursorAdapter extends CursorAdapter {

    private LayoutInflater mLayoutInflater;
    
    
    public ScanResultCursorAdapter(Context context, Cursor c) {
	super(context, c, false);

	mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
	// TODO Auto-generated method stub
	
	TextView ssid = (TextView) view.findViewById(R.id.wifi_ssid_text);
	ssid.setText(cursor.getString(cursor.getColumnIndex(WifiDatabaseHelper.COLUMN_SSID)));
	
	TextView bssid = (TextView) view.findViewById(R.id.wifi_bssid_text);
	bssid.setText(cursor.getString(cursor.getColumnIndex(WifiDatabaseHelper.COLUMN_BSSID)));
	
	ImageView signal = (ImageView) view.findViewById(R.id.wifi_signal_image);
	Integer level = WifiManager.calculateSignalLevel((int) cursor.getLong(cursor.getColumnIndex(WifiDatabaseHelper.COLUMN_LEVEL)), 5);

	// TODO: There's probably a better way to do this
	switch (level) {
	case 0:
	    signal.setImageResource(R.drawable.wifi_strength_0);
	    break;
	case 1:
	    signal.setImageResource(R.drawable.wifi_strength_1);
	    break;
	case 2:
	    signal.setImageResource(R.drawable.wifi_strength_2);
	    break;
	case 3:
	    signal.setImageResource(R.drawable.wifi_strength_3);
	    break;
	case 4:
	    signal.setImageResource(R.drawable.wifi_strength_4);
	    break;
	default:
	    signal.setImageResource(R.drawable.wifi_strength_0);
	    break;
	}
	
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
	final View view = mLayoutInflater.inflate(R.layout.wifi_entry, parent, false);
	return view;
    }

}
