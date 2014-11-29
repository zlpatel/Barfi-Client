package com.barfi.android;

import android.os.Environment;

public class Const {
	
	public static String DB_PATH = Environment.getExternalStorageDirectory().getPath().toString();
	public static String KEY_DB="BarfiDB";
	public static String KEY_TABLE = "user_access_details";
	public static String KEY_EMAIL="email";
	public static String KEY_ACCESS_TOKEN="access_token";
	public static String SERVER_ADDRESS="http://10.144.155.141:8001/";
}
