package com.barfi.android;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class SchedulerEventService extends Service {
	private static final String APP_TAG = "com.barfi.android.scheduler";
	SharedPreferences pref;
	AudioManager am;
	@Override
	public IBinder onBind(final Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(final Intent intent, final int flags,
			final int startId) {
		pref = getSharedPreferences("AppPref", MODE_PRIVATE);
		String username = pref.getString("Email", "").split("@")[0];
		new ParsingJSON().execute(Const.SERVER_ADDRESS+"api/prediction/?username="+username+"&time="+getDateTime());
		am= (AudioManager) this
				.getSystemService(Context.AUDIO_SERVICE);
		return Service.START_NOT_STICKY;
	}
	
	private class ParsingJSON extends AsyncTask<String, Void, JSONObject> {

		
		@Override
		protected void onPreExecute() {
		
		}

		@Override
		protected JSONObject doInBackground(String... urls) {

			ParseJSON parser = new ParseJSON();
			JSONObject jObj=new JSONObject();
			jObj=parser.getJSONFromUrl(urls[0]);
			return jObj;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
		
			if (result != null) {
				try {
					boolean performAction=result.getBoolean("takeAction");
					int action=result.getInt("action");
					if(performAction){
						switch (action) {
						case 0:
							am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
							break;
						case 1:
							am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
							break;
						case 2:
							am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
							break;

						default:
							break;
						}
					}
//					Toast.makeText(SchedulerEventService.this, "performAction : "+performAction +" \nAction to take : "+action, Toast.LENGTH_LONG).show();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	private String getDateTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				Const.DATE_TIME_FORMAT, Locale.getDefault());
		Date date = new Date();
		return dateFormat.format(date);
	}
}
