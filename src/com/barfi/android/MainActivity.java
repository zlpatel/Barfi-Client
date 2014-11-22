package com.barfi.android;

import com.barfi.android.R;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

	private Button btnMonitoring;
	private ServiceConnection m_serviceConnection;
	private MyService m_service;
	private SharedPreferences pref;
	Database database;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		btnMonitoring=(Button)findViewById(R.id.buttonMonitoring);
		pref = getSharedPreferences("AppPref", MODE_PRIVATE);
		database=Database.getDatabaseInstance(this);
		 m_serviceConnection = new ServiceConnection() {
			
			@Override
			public void onServiceDisconnected(ComponentName name) {
				m_service = null;
			}
			
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				m_service = ((MyService.MyBinder)service).getService();
			}
		};
		
		btnMonitoring.setOnClickListener(this);	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()){
		
		case R.id.deleteDB:
			deleteDatabase();
		break;
	
	}	
	return false;
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonMonitoring:
			setPreferenceForButtonClick();
			startMyService();
			break;
		default:
			break;
		}
		
	}

	private void deleteDatabase() {
		database.deleteDB();
	}

	private void setPreferenceForButtonClick() {
		SharedPreferences.Editor edit = pref.edit();
        //Storing Data using SharedPreferences
       edit.putBoolean("SERVICE_ALREADY_STARTED",true);
       edit.commit();
	}

	private void startMyService() {
		Intent newIntent=new Intent(MainActivity.this,MyService.class);
		startService(newIntent);
		bindService(newIntent, m_serviceConnection, BIND_AUTO_CREATE);
	}
}
