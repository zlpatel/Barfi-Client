package com.barfi.android;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {

	SharedPreferences pref;
	private static final String APP_TAG = "com.barfi.android.MyReceiver";
	private static final int EXEC_INTERVAL = 1 * 1000*60;
	
	@Override
	public void onReceive(Context ctx, Intent intent) {
		pref = ctx.getSharedPreferences("AppPref", Context.MODE_PRIVATE);
	    Log.d("AppPref",String.valueOf(pref.getBoolean("SERVICE_ALREADY_STARTED", false)));
		if(pref.getBoolean("SERVICE_ALREADY_STARTED",false)){
			Toast.makeText(ctx, "MyReceiver started", Toast.LENGTH_SHORT).show();
			
			Log.d(APP_TAG, "MyReceiver.onReceive() called");
			Intent eventService = new Intent(ctx, MyService.class);
			ctx.startService(eventService);
			
			AlarmManager alarmManager = (AlarmManager) ctx
					.getSystemService(Context.ALARM_SERVICE);
			Intent i = new Intent(ctx, SchedulerEventReceiver.class); // explicit
																		// intent
			PendingIntent intentExecuted = PendingIntent.getBroadcast(ctx, 0, i,
					PendingIntent.FLAG_CANCEL_CURRENT);
			Calendar now = Calendar.getInstance();
			now.add(Calendar.MINUTE, 1);
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
					now.getTimeInMillis(), EXEC_INTERVAL, intentExecuted);
		}
		
			
	}

}
