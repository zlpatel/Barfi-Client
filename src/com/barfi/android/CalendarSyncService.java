package com.barfi.android;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class CalendarSyncService extends Service {
	private static final String APP_TAG = "com.barfi.android.calendarSync";
	SharedPreferences pref;
	Database database;
	String token;
	String syncToken;
	String email;
	
	@Override
	public IBinder onBind(final Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(final Intent intent, final int flags,
			final int startId) {
		Log.d("CalendarSync","come here");
		database = Database.getDatabaseInstance(this);
		pref = getSharedPreferences("AppPref", MODE_PRIVATE);
		email = pref.getString("Email", "");
		
		try {
			token=database.getAccessToken(email);
			syncToken=database.getNextSyncToken(email);
		} catch (SQLiteException e) {
			Log.e("sqlite", e.getMessage());
		}
//		Toast.makeText(CalendarSyncService.this, syncToken+"  ::   "+token, Toast.LENGTH_LONG).show();
		
		new GoogleCalender().execute(
				"https://www.googleapis.com/calendar/v3/calendars/",
				"?syncToken=" + syncToken+"&"+"access_token=" + token, email);
		
		return Service.START_NOT_STICKY;
	}
	
	private class GoogleCalender extends AsyncTask<String, Void, JSONObject> {

		private ProgressDialog dialog = new ProgressDialog(CalendarSyncService.this);

		@Override
		protected void onPreExecute() {
//			dialog.setMessage("Please wait..");
//			dialog.show();
		}

		@Override
		protected JSONObject doInBackground(String... urls) {

			ParseJSON parser = new ParseJSON();
			JSONObject jObj2 = parser.getJSONFromUrl(urls[0] + urls[2]
					+ "/events" + urls[1]);
			return jObj2;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
//			dialog.dismiss();

			if (result != null) {
				try {
					String nextSyncToken=result.getString("nextSyncToken");
//					SharedPreferences.Editor edit = pref.edit();
//					// Storing Access Token using SharedPreferences
//					edit.putString("Sync Token", nextSyncToken);
//					edit.commit();
					
					try {
						database.updateNextSyncToken(pref.getString("Email", ""), nextSyncToken);
					} catch (SQLiteException e) {
						Log.e("sqlite", e.getMessage());
					}
										
//					Toast.makeText(Authentication.this, nextSyncToken, Toast.LENGTH_LONG).show();
					
					JSONArray arr = result.getJSONArray("items");
					for (int i = 0; i < arr.length(); i++) {

						JSONObject newObj = new JSONObject();

						newObj.put("start_date", arr.getJSONObject(i)
								.getJSONObject("start").getString("dateTime"));
						newObj.put("end_date", arr.getJSONObject(i)
								.getJSONObject("end").getString("dateTime"));
						newObj.put("status",
								arr.getJSONObject(i).getString("status"));
						newObj.put("recurring",
								arr.getJSONObject(i).has("recurrence"));
						newObj.put("user", pref.getString("Email", ""));

						SendJSON send = new SendJSON();
						send.sendJson(newObj, Const.SERVER_ADDRESS
								+ "calendar/events/");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
