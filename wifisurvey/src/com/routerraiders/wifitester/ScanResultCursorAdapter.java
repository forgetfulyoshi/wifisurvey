package com.routerraiders.wifitester;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.routerraiders.wifitester.util.ImageHandler;

public class ScanResultCursorAdapter extends CursorAdapter {

    private LayoutInflater mLayoutInflater;
    
    
    public ScanResultCursorAdapter(Context context, Cursor c) {
	super(context, c, false);

	mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
	
	TextView ssid = (TextView) view.findViewById(R.id.wifi_ssid_text);
	ssid.setText(cursor.getString(cursor.getColumnIndex(WifiDatabaseHelper.COLUMN_SSID)));
	
	TextView bssid = (TextView) view.findViewById(R.id.wifi_bssid_text);
	bssid.setText(cursor.getString(cursor.getColumnIndex(WifiDatabaseHelper.COLUMN_BSSID)));
	
	ImageView signal = (ImageView) view.findViewById(R.id.wifi_signal_image);

	Integer level = (int) cursor.getLong(cursor.getColumnIndex(WifiDatabaseHelper.COLUMN_LEVEL));
	String security = cursor.getString(cursor.getColumnIndex(WifiDatabaseHelper.COLUMN_SECURITY));
	
	Integer iconId = ImageHandler.getWifiIconResource(context, level, security);
	signal.setImageResource(iconId);
	
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
	final View view = mLayoutInflater.inflate(R.layout.wifi_entry, parent, false);
	return view;
    }

}
