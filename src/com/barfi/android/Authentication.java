package com.barfi.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.identity.intents.AddressConstants.Themes;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.res.Resources.Theme;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Authentication extends Activity{

	Button select,api_call;
	  String[] avail_accounts;
	  private AccountManager mAccountManager;
	  ListView list;
	  ArrayAdapter<String> adapter;
	  SharedPreferences pref;
	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.authentication);
	    select = (Button)findViewById(R.id.select_button);
	    api_call = (Button)findViewById(R.id.apicall_button);
	    avail_accounts = getAccountNames();
	    adapter = new ArrayAdapter<String>(this,
	                R.layout.mylistview,avail_accounts );
	    pref = getSharedPreferences("AppPref", MODE_PRIVATE);
	    select.setOnClickListener(new View.OnClickListener() {
	    Dialog accountDialog;
	      @Override
	      public void onClick(View arg0) {
	        // TODO Auto-generated method stub
	        if (avail_accounts.length != 0){
	        accountDialog = new Dialog(Authentication.this,R.style.DialogBoxStyle);
	        accountDialog.setContentView(R.layout.accounts_dialog);
	        accountDialog.setTitle("Select Google Account");
	        list = (ListView)accountDialog.findViewById(R.id.list);
	        list.setAdapter(adapter);
	         list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	                  @Override
	                  public void onItemClick(AdapterView<?> parent, View view,
	                                          int position, long id) {
	                  SharedPreferences.Editor edit = pref.edit();
	                    //Storing Data using SharedPreferences
	                   edit.putString("Email", avail_accounts[position]);
	                   edit.commit();
	                   new Authenticate().execute();
	                  accountDialog.cancel();
	                  }
	              });
	        accountDialog.show();
	      }else{
	        Toast.makeText(getApplicationContext(), "No accounts found, Add a Account and Continue.", Toast.LENGTH_SHORT).show();
	      }
	      }
	    });
	    
	  }
	  private String[] getAccountNames() {
	    mAccountManager = AccountManager.get(this);
	        Account[] accounts = mAccountManager.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
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
	             mEmail= pref.getString("Email", "");
	             pDialog.show();
	         }
	        @Override
	        protected void onPostExecute(String token) {
	          pDialog.dismiss();
	          if(token != null){
	        SharedPreferences.Editor edit = pref.edit();
	                //Storing Access Token using SharedPreferences
	               edit.putString("Access Token", token);
	               edit.commit();
	               Log.i("Token", "Access Token retrieved:" + token);
//	        Toast.makeText(getApplicationContext(),"Access Token is " +token, Toast.LENGTH_SHORT).show();
	        select.setText(pref.getString("Email", "")+" is Authenticated");
	        }
	          
	          new GoogleCalender().execute("https://www.googleapis.com/calendar/v3/users/me/calendarList","https://www.googleapis.com/calendar/v3/calendars/","?access_token="+token,mEmail);
		        
	        }
	    @Override
	    protected String doInBackground(String... arg0) {
	      // TODO Auto-generated method stub
	      String token = null;
	           try {
	        	   token = GoogleAuthUtil.getToken(
	                       Authentication.this,
	                       mEmail,
	                       "oauth2:https://www.googleapis.com/auth/calendar");   
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
	    
	    private class GoogleCalender extends AsyncTask<String, Void, JSONObject>{

	    	
	        private ProgressDialog dialog = new ProgressDialog(Authentication.this);
	        
			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				dialog.setMessage("Please wait..");
	            dialog.show();
	            
			}

			@Override
			protected JSONObject doInBackground(String... urls) {
				// TODO Auto-generated method stub
				
				
				ParseJSON parser=new ParseJSON();
//				JSONObject jObj1=parser.getJSONFromUrl(urls[0]+ urls[2]);
//	            // Return JSON String
//	            String id=null;
//	            try {
//					id=jObj1.getString("id");
//					Log.i("ID!", id);
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
	            
				JSONObject jObj2=parser.getJSONFromUrl(urls[1]+urls[3]+"/events"+ urls[2]);
				Log.i("hey!",jObj2.toString());
				return jObj2;
			}
	    	
			@Override
			protected void onPostExecute(JSONObject result) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				
				if(result!=null){
					Toast.makeText(Authentication.this, result.toString(), Toast.LENGTH_LONG).show();
				}
			}
	    }
	    
}
