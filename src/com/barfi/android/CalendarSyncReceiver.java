package com.barfi.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CalendarSyncReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context ctx, final Intent intent) {
		Intent eventService = new Intent(ctx, CalendarSyncService.class);
		ctx.startService(eventService);
	}
}
