package com.barfi.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class RingerModeReceiver extends BroadcastReceiver{

private static final String APP_TAG = "com.barfi.android.RingerModeReceiver";
	
	@Override
	public void onReceive(Context ctx, Intent intent) {
		// TODO Auto-generated method stub
		
		Toast.makeText(ctx, "RingerModeReceiver started", Toast.LENGTH_SHORT).show();
		
		Log.d(APP_TAG, "RingerModeReceiver.onReceive() called");	
	}
}
