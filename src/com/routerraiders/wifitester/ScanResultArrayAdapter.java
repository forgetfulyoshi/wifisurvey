package com.routerraiders.wifitester;

import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class ScanResultArrayAdapter extends ArrayAdapter<ScanResult> {

    public ScanResultArrayAdapter(Context context, int textViewResourceId, List<ScanResult> objects) {
	super(context, textViewResourceId, objects);
	// TODO Auto-generated constructor stub
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
	return null;
    }

}
