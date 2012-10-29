package com.routerraiders.wifitester.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;

import com.routerraiders.wifitester.R;

public class WifiDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	builder.setMessage(R.string.dialog_activate_wifi);
	builder.setCancelable(false);
	builder.setPositiveButton(R.string.wifi_dialog_yes, new DialogInterface.OnClickListener() {

	    public void onClick(DialogInterface dialog, int which) {
		startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
	    }
	});

	builder.setNegativeButton(R.string.wifi_dialog_no, new DialogInterface.OnClickListener() {

	    public void onClick(DialogInterface dialog, int which) {
		dialog.cancel();
	    }
	});

	return builder.create();
    }

}
