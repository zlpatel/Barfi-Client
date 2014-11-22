package com.barfi.android;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.Settings;

public class RingerModeReceiver extends BroadcastReceiver {
	private static Location location;
	private static Context context;
	private String provider;
	private static double latitude;
	private static double longitude;
	private static LocationManager locationManager;
	private SharedPreferences pref;
	
	private static LocationListener locationListener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			if (location != null) {
				latitude = location.getLatitude();
				longitude = location.getLongitude();
			}

		}

		@Override
		public void onProviderDisabled(String arg0) {

		}

		@Override
		public void onProviderEnabled(String arg0) {

		}

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {

		}

	};

	public double getLatitude() {
		if (location != null) {
			latitude = location.getLatitude();
		}

		// return latitude
		return latitude;
	}

	/**
	 * Function to get longitude
	 * */
	public double getLongitude() {
		if (location != null) {
			longitude = location.getLongitude();
		}

		// return longitude
		return longitude;
	}

	public void setupGPS() {
		locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		if (location == null) {
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 0, 0, locationListener);

			boolean enabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);
			// check if enabled and if not send user to the GSP settings
			// Better solution would be to display a dialog and suggesting to
			// go to the settings
			if (!enabled) {
				Intent intent = new Intent(
						Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
			}
		}
	}

	public void stopGPS() {
		if (location != null) {
			locationManager.removeUpdates(locationListener);
		}
	}

	public void startLocating() {
		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, false);
		location = locationManager.getLastKnownLocation(provider);

		// Initialize the location fields
		if (location != null) {
			System.out.println("Provider " + provider + " has been selected.");
			locationListener.onLocationChanged(location);
		} else {

		}
	}

	@Override
	public void onReceive(Context ctx, Intent intent) {

		context = ctx;
		
		pref = context.getSharedPreferences("AppPref", Context.MODE_PRIVATE);
		AudioManager am = (AudioManager) ctx
				.getSystemService(Context.AUDIO_SERVICE);

		setupGPS();
		startLocating();
		stopGPS();

		switch (am.getRingerMode()) {
		case AudioManager.RINGER_MODE_SILENT:
			createJSONObject(am.getRingerMode(), getDateTime(), getLatitude(),
					getLongitude(),getDayOfWeek());
			break;
		case AudioManager.RINGER_MODE_VIBRATE:
			createJSONObject(am.getRingerMode(), getDateTime(), getLatitude(),
					getLongitude(),getDayOfWeek());
			break;
		case AudioManager.RINGER_MODE_NORMAL:
			createJSONObject(am.getRingerMode(), getDateTime(), getLatitude(),
					getLongitude(),getDayOfWeek());
			break;

		}
	}

	private void createJSONObject(int ringerMode, String dateTime,
			double latitude, double longitude,int dayOfWeek) {
		JSONObject newObj = new JSONObject();
		try {
			newObj.put("action", ringerMode);
			newObj.put("action_datetime", dateTime);
			newObj.put("day_of_week", dayOfWeek);
			newObj.put("latitude", latitude);
			newObj.put("longitude", longitude);
			newObj.put("user", pref.getString("Email", ""));
			SendJSON send = new SendJSON();
			send.sendJson(newObj, Const.SERVER_ADDRESS+"action/events/");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private int getDayOfWeek(){
		Calendar c = Calendar.getInstance();
		return c.get(Calendar.DAY_OF_WEEK);		
	}
	
	private String getDateTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
		Date date = new Date();
		return dateFormat.format(date);
	}
}
