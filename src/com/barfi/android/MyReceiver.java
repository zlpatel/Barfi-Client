package com.barfi.android;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {

	SharedPreferences pref;
	Context context;
	

	@Override
	public void onReceive(Context ctx, Intent intent) {
		context = ctx;
		pref = ctx.getSharedPreferences("AppPref", Context.MODE_PRIVATE);
		if (pref.getBoolean("SERVICE_ALREADY_STARTED", false)) {
			Toast.makeText(ctx, "MyReceiver started", Toast.LENGTH_SHORT)
					.show();
			Intent eventService = new Intent(ctx, MyService.class);
			ctx.startService(eventService);
			startRepeatingService();
		}
	}

	private void startRepeatingService() {
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, SchedulerEventReceiver.class); // explicit
																		// intent
		PendingIntent intentExecuted = PendingIntent.getBroadcast(context, 0,
				i, PendingIntent.FLAG_CANCEL_CURRENT);
		Calendar now = Calendar.getInstance();
		now.add(Calendar.MINUTE, Const.EXEX_INTERVAL_MINUTES);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				now.getTimeInMillis(),Const.EXEC_INTERVAL, intentExecuted);
	}
}
