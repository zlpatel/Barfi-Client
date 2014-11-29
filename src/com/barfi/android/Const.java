package com.barfi.android;

import android.os.Environment;

public class Const {

	public static final String DB_PATH = Environment
			.getExternalStorageDirectory().getPath().toString();
	public static final String KEY_DB = "BarfiDB";
	public static final String KEY_TABLE = "user_access_details";
	public static final String KEY_EMAIL = "email";
	public static final String KEY_ACCESS_TOKEN = "access_token";
	public static final String SERVER_ADDRESS = "http://10.144.155.141:8001/";
	public static final int EXEC_INTERVAL = 1 * 1000 * 20;
}
