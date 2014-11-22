package com.barfi.android;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.widget.Toast;


public class Database {

	SQLiteDatabase db;
	
	private static Database instance = null;
	   protected Database(Context context) {
	      // Exists only to defeat instantiation.
		   db = context.openOrCreateDatabase(Const.DB_PATH+"/"+Const.KEY_DB,android.content.Context.MODE_PRIVATE, null);
	   }
	   public static Database getDatabaseInstance(Context context) {
	      if(instance == null) {
	         instance = new Database(context);
	      }
	      return instance;
	   }
	
	
//	protected void getOrCreateDB(Context context){
//    	db = context.openOrCreateDatabase(Const.DB_PATH+"/"+Const.KEY_DB,android.content.Context.MODE_PRIVATE, null);
//    }
    
    protected boolean checkForTable(Context context) {
		db.beginTransaction();
		try {
			Cursor c = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"+Const.KEY_TABLE+"'",null);
			if(c!=null && c.getCount()>0){
				c.close();
				return true;
			}
			c.close();
			db.setTransactionSuccessful(); //commit your changes
		}
		catch (SQLiteException e) {
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
		}
		finally {
			db.endTransaction();
		}		
		return false;
    }
    
    protected void createTable(Context context){
		db.beginTransaction();
		try {

				db.execSQL("create table if not exists "+Const.KEY_TABLE+" (" 
						+Const.KEY_EMAIL +" text PRIMARY KEY , " 
						+Const.KEY_ACCESS_TOKEN +" text ); " );

			db.setTransactionSuccessful(); //commit your changes
		}
		catch (SQLiteException e) {
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
		}
		finally {
			db.endTransaction();
		}		
	}
    
    protected void insertUserData(String eMail,String accessToken){   	
    	db.execSQL( "insert into "+Const.KEY_TABLE+" ("+Const.KEY_EMAIL+","+Const.KEY_ACCESS_TOKEN+") values ("+"\""+eMail+"\","+"\""+accessToken+"\" );" );   	
    }
    
    protected void closeDB(){
    	db.close();
    }
    
    protected void deleteDB(){
    	SQLiteDatabase.deleteDatabase(new File(Const.DB_PATH+"/"+Const.KEY_DB));
    }
}
