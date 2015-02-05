package com.barfi.android;

import java.io.File;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.widget.Toast;

public class Database {

	SQLiteDatabase db;

	private static Database instance = null;

	protected Database(Context context) {
		// Exists only to defeat instantiation.
		db=context.openOrCreateDatabase(new File(context.getExternalFilesDir(null), Const.KEY_DB).toString(),android.content.Context.MODE_PRIVATE, null);
//		db = context.openOrCreateDatabase(Const.DB_PATH + "/" + Const.KEY_DB,
//				android.content.Context.MODE_PRIVATE, null);
	}

	public static Database getDatabaseInstance(Context context) {
		if (instance == null) {
			instance = new Database(context);
		}
		return instance;
	}

	protected boolean checkForTable(Context context) {
		db.beginTransaction();
		try {
			Cursor c = db.rawQuery(
					"select DISTINCT tbl_name from sqlite_master where tbl_name = '"
							+ Const.KEY_TABLE + "'", null);
			if (c != null && c.getCount() > 0) {
				c.close();
				return true;
			}
			c.close();
			db.setTransactionSuccessful(); // commit your changes
		} catch (SQLiteException e) {
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
		} finally {
			db.endTransaction();
		}
		return false;
	}

	protected void createTable(Context context) {
		db.beginTransaction();
		try {

			db.execSQL("create table if not exists " + Const.KEY_TABLE + " ("
					+ Const.KEY_EMAIL + " text PRIMARY KEY , "
					+ Const.KEY_ACCESS_TOKEN + " text , "
					+ Const.KEY_NEXT_SYNC_TOKEN + " text ); ");

			db.setTransactionSuccessful(); // commit your changes
		} catch (SQLiteException e) {
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
		} finally {
			db.endTransaction();
		}
	}

	protected void insertUserData(String eMail, String accessToken)
			throws SQLiteException {
		db.execSQL("insert into " + Const.KEY_TABLE + " (" + Const.KEY_EMAIL
				+ "," + Const.KEY_ACCESS_TOKEN + "," + Const.KEY_NEXT_SYNC_TOKEN + ") values (" + "\"" + eMail
				+ "\"," + "\"" + accessToken + "\""+","+ "\"" + accessToken + "\" );");
	}
	
	protected void updateNextSyncToken(String eMail,String nextSyncToken)
			throws SQLiteException {
		ContentValues values = new ContentValues();
		values.put(Const.KEY_NEXT_SYNC_TOKEN, nextSyncToken);
		Log.d("update1", nextSyncToken);
		int a=db.update(Const.KEY_TABLE, values, Const.KEY_EMAIL + "=?",
				new String[] { eMail });
		Log.d("update2", nextSyncToken);
//		db.execSQL("update " + Const.KEY_TABLE + " SET " + Const.KEY_NEXT_SYNC_TOKEN + " = " + "\"" + nextSyncToken + "\" where "+ Const.KEY_EMAIL+" = "+ "\"" +eMail+ "\" ");
	}
	
	protected String getNextSyncToken(String eMail) throws SQLiteException{
		String selectQuery = "SELECT " + Const.KEY_NEXT_SYNC_TOKEN +" FROM " + Const.KEY_TABLE+" WHERE "+ Const.KEY_EMAIL+" = "+ "\"" +eMail+ "\"";
		Cursor c = db.rawQuery(selectQuery, null);
		String nextSyncToken=null;
		if (c.moveToFirst()) {
			nextSyncToken= c.getString(c.getColumnIndex(Const.KEY_NEXT_SYNC_TOKEN));
		}
		c.close();
		Log.d("update3", nextSyncToken);
		return nextSyncToken;
	}
	
	protected String getAccessToken(String eMail) throws SQLiteException{
		String selectQuery = "SELECT " + Const.KEY_ACCESS_TOKEN +" FROM " + Const.KEY_TABLE+" WHERE "+ Const.KEY_EMAIL+" = "+ "\"" +eMail+ "\"";
		Cursor c = db.rawQuery(selectQuery, null);
		String accessToken=null;
		if (c.moveToFirst()) {
			accessToken= c.getString(c.getColumnIndex(Const.KEY_ACCESS_TOKEN));
		}
		c.close();
		Log.d("updateAccess3", accessToken);
		return accessToken;
	}
	protected void closeDB() throws SQLiteException {
		db.close();
	}

	protected void deleteDB() {
		SQLiteDatabase.deleteDatabase(new File(Const.DB_PATH + "/"
				+ Const.KEY_DB));
	}
}
