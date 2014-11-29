package com.barfi.android;

import java.util.Date;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class SchedulerEventService extends Service {
	private static final String APP_TAG = "com.barfi.android.scheduler";

	@Override
	public IBinder onBind(final Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(final Intent intent, final int flags,
			final int startId) {
		Toast.makeText(getApplicationContext(), "started at :" + new Date(),
				Toast.LENGTH_LONG).show();
		Log.d(APP_TAG, "event received in service: " + new Date().toString());
		return Service.START_NOT_STICKY;
	}

}
