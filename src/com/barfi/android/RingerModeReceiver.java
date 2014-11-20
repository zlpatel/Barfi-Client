package com.barfi.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.widget.Toast;

public class RingerModeReceiver extends BroadcastReceiver{

private static final String APP_TAG = "com.barfi.android.RingerModeReceiver";
	
	@Override
	public void onReceive(Context ctx, Intent intent) {		
		AudioManager am = (AudioManager)ctx.getSystemService(Context.AUDIO_SERVICE);

		switch (am.getRingerMode()) {
		    case AudioManager.RINGER_MODE_SILENT:
		        Toast.makeText(ctx, "Silent mode", Toast.LENGTH_SHORT).show();
		        break;
		    case AudioManager.RINGER_MODE_VIBRATE:
		        Toast.makeText(ctx, "Vibrate mode", Toast.LENGTH_SHORT).show();
		        break;
		    case AudioManager.RINGER_MODE_NORMAL:
		        Toast.makeText(ctx, "Normal mode", Toast.LENGTH_SHORT).show();
		        break;
		
		}
	}
}
