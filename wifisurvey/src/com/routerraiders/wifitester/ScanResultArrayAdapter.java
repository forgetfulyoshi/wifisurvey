package com.routerraiders.wifitester;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.routerraiders.wifitester.util.ImageHandler;

public class ScanResultArrayAdapter extends ArrayAdapter<ScanResult> {

    private Context mContext;
    private Integer mLayoutResourceId;

    public ScanResultArrayAdapter(Context context, int textViewResourceId, List<ScanResult> objects) {
	super(context, textViewResourceId, objects);
	mContext = context;
	mLayoutResourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
	View row = convertView;
	ScanResultHolder holder = null;

	if (row == null) {
	    LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
	    row = inflater.inflate(mLayoutResourceId, parent, false);

	    holder = new ScanResultHolder();
	    holder.wifiStrengthImg = (ImageView) row.findViewById(R.id.wifi_signal_image);
	    holder.bssidTxt = (TextView) row.findViewById(R.id.wifi_bssid_text);
	    holder.ssidTxt = (TextView) row.findViewById(R.id.wifi_ssid_text);

	    row.setTag(holder);
	} else {
	    holder = (ScanResultHolder) row.getTag();
	}
	
	Integer iconId = ImageHandler.getWifiIconResource(mContext, getItem(position).level, getItem(position).capabilities);
	holder.wifiStrengthImg.setImageResource(iconId);

	holder.bssidTxt.setText(getItem(position).BSSID);
	holder.ssidTxt.setText(getItem(position).SSID);

	return row;
    }

    static class ScanResultHolder {
	ImageView wifiStrengthImg;
	TextView bssidTxt;
	TextView ssidTxt;
    }

}
