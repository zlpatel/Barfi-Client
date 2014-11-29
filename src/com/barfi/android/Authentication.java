package com.barfi.android;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class Authentication extends Activity {

	com.google.android.gms.common.SignInButton select;

	String[] avail_accounts;
	private AccountManager mAccountManager;
	ListView list;
	ArrayAdapter<String> adapter;
	SharedPreferences pref;
	Database database;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		database = Database.getDatabaseInstance(this);

		if (database.checkForTable(this)) {
			goToMainActivity();
		} else {
			setContentView(R.layout.authentication);
			select = (com.google.android.gms.common.SignInButton) findViewById(R.id.sign_in_button);
			avail_accounts = getAccountNames();
			adapter = new ArrayAdapter<String>(this, R.layout.mylistview,
					avail_accounts);
			pref = getSharedPreferences("AppPref", MODE_PRIVATE);
			select.setOnClickListener(new View.OnClickListener() {
				Dialog accountDialog;

				@Override
				public void onClick(View arg0) {
					if (avail_accounts.length != 0) {
						accountDialog = new Dialog(Authentication.this,
								R.style.DialogBoxStyle);
						accountDialog.setContentView(R.layout.accounts_dialog);
						accountDialog.setTitle("Select Google Account");
						list = (ListView) accountDialog.findViewById(R.id.list);
						list.setAdapter(adapter);
						list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								SharedPreferences.Editor edit = pref.edit();
								// Storing Data using SharedPreferences
								edit.putString("Email",
										avail_accounts[position]);
								edit.commit();
								new Authenticate().execute();
								accountDialog.cancel();
							}
						});
						accountDialog.show();
					} else {
						Toast.makeText(
								getApplicationContext(),
								"No accounts found, Add a Account and Continue.",
								Toast.LENGTH_SHORT).show();
					}
				}
			});
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		database.closeDB();
	}

	private String[] getAccountNames() {
		mAccountManager = AccountManager.get(this);
		Account[] accounts = mAccountManager
				.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
		String[] names = new String[accounts.length];
		for (int i = 0; i < names.length; i++) {
			names[i] = accounts[i].name;
		}
		return names;
	}

	private class Authenticate extends AsyncTask<String, String, String> {
		ProgressDialog pDialog;
		String mEmail;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Authentication.this);
			pDialog.setMessage("Authenticating....");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			mEmail = pref.getString("Email", "");
			pDialog.show();
		}

		@Override
		protected void onPostExecute(String token) {
			pDialog.dismiss();
			if (token != null) {
				SharedPreferences.Editor edit = pref.edit();
				// Storing Access Token using SharedPreferences
				edit.putString("Access Token", token);
				edit.commit();
				Log.i("Token", "Access Token retrieved:" + token);
				// Toast.makeText(getApplicationContext(),"Access Token is "
				// +token, Toast.LENGTH_SHORT).show();
				try {
					database.insertUserData(mEmail, token);
				} catch (SQLiteException e) {
					Log.e("sqlite", e.getMessage());
				}

				JSONObject newObj = new JSONObject();
				try {
					newObj.put("username", mEmail.split("@")[0]);
					newObj.put("email", mEmail);
					newObj.put("is_staff", true);
					SendJSON send = new SendJSON();
					send.sendJson(newObj, Const.SERVER_ADDRESS + "users/");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				goToMainActivity();
				// select.setText(pref.getString("Email", "")
				// + " is Authenticated");
			}
			new GoogleCalender().execute(
					"https://www.googleapis.com/calendar/v3/calendars/",
					"?access_token=" + token, mEmail);
		}

		@Override
		protected String doInBackground(String... arg0) {
			String token = null;
			try {
				token = GoogleAuthUtil.getToken(Authentication.this, mEmail,
						"oauth2:https://www.googleapis.com/auth/calendar");
				database.createTable(Authentication.this);
			} catch (IOException transientEx) {
				// Network or server error, try later
				Log.e("IOException", transientEx.toString());
			} catch (UserRecoverableAuthException e) {
				// Recover (with e.getIntent())
				startActivityForResult(e.getIntent(), 1001);
				Log.e("AuthException", e.toString());
			} catch (GoogleAuthException authEx) {
				// The call is not ever expected to succeed
				// assuming you have already verified that
				// Google Play services is installed.
				Log.e("GoogleAuthException", authEx.toString());
			}
			return token;
		}
	};

	private class GoogleCalender extends AsyncTask<String, Void, JSONObject> {

		private ProgressDialog dialog = new ProgressDialog(Authentication.this);

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Please wait..");
			dialog.show();

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
			dialog.dismiss();

			if (result != null) {
				try {
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

	public void goToMainActivity() {
		Intent intent = new Intent(Authentication.this, MainActivity.class);
		startActivity(intent);
	}

}
