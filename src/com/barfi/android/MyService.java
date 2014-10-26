package com.barfi.android;

import java.util.Date;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MyService extends Service {
	
	private static final String APP_TAG = "com.barfi.android.service";
	NotificationManager m_notificationManager;
	
	int NOTIFICATION_ID = 001;
		
	@Override
	public IBinder onBind(final Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(final Intent intent, final int flags,
			final int startId) {
		Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
		Log.d(APP_TAG, "event received in service: " + new Date().toString());
		addNotification();
		return Service.START_STICKY;
	}
	
	public class MyBinder extends Binder {
	    public MyService getService() {
	            return MyService.this;
	    }
	}
	
	private void addNotification() {
        // create the notification
        Notification.Builder m_notificationBuilder = new Notification.Builder(this)
                .setContentTitle(getText(R.string.service_name))
                .setContentText(getResources().getText(R.string.service_status_monitor))
                .setSmallIcon(R.drawable.ic_launcher);

        // create the pending intent and add to the notification
        Intent intent = new Intent(this, MyService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        m_notificationBuilder.setContentIntent(pendingIntent);

     // Gets an instance of the NotificationManager service
        m_notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        
        // send the notification      
        m_notificationManager.notify(NOTIFICATION_ID, m_notificationBuilder.build());
        
        startForeground(NOTIFICATION_ID, m_notificationBuilder.build());
}

}