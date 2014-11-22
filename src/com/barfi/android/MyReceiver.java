package com.barfi.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {

	SharedPreferences pref;
	private static final String APP_TAG = "com.barfi.android.MyReceiver";
	
	@Override
	public void onReceive(Context ctx, Intent intent) {
		pref = ctx.getSharedPreferences("AppPref", Context.MODE_PRIVATE);
	    Log.d("AppPref",String.valueOf(pref.getBoolean("SERVICE_ALREADY_STARTED", false)));
		if(pref.getBoolean("SERVICE_ALREADY_STARTED",false)){
			Toast.makeText(ctx, "MyReceiver started", Toast.LENGTH_SHORT).show();
			
			Log.d(APP_TAG, "MyReceiver.onReceive() called");
			Intent eventService = new Intent(ctx, MyService.class);
			ctx.startService(eventService);
		}
		
			
	}

}
