package es.jaumesingla.ultrasearch.database;

import java.util.ArrayList;

import es.jaumesingla.ultrasearch.model.InfoLaunchApplication;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Scheme {
	public static class Applications{
		private static final String TABLE="Applications";
		private static final String COLUMN_NAME="name";
		private static final String COLUMN_ACTIVITY="activity";
		private static final String COLUMN_PACKAGE="package";
		private static final String COLUMN_ICON="icon";
		
		private static final String[] ALL_COLUMNS={COLUMN_NAME, COLUMN_ICON, COLUMN_ACTIVITY, COLUMN_PACKAGE};
		
		private static final String CREATE="CREATE TABLE "+TABLE+" ("+
						COLUMN_NAME+" text not null,"+
						COLUMN_ACTIVITY+" text not null,"+
						COLUMN_PACKAGE+" text not null,"+
						COLUMN_ICON+" integer not null,"+
						"primary key ("+COLUMN_ACTIVITY+","+COLUMN_PACKAGE+"));";
		
		protected static void create(SQLiteDatabase db){
			db.execSQL(CREATE);
		}
		
		protected static void upgrade(SQLiteDatabase db, int oldVersion, int newVersion){
			
		}
		
		private SQLiteDatabase mDb;
		protected Applications(SQLiteDatabase db){
			mDb=db;
		}
		
		public void addApplication(InfoLaunchApplication app){
			synchronized (mDb) {			
				String where=COLUMN_ACTIVITY+"= '"+app.getActivity()+"' and "+COLUMN_PACKAGE+" = '"+app.getPackageName()+"'";
				
				Cursor cursor=mDb.query(TABLE, null, where, null, null, null, null, "1");
				if (cursor.getCount()==0){
					ContentValues values=new ContentValues();
					values.put(COLUMN_NAME, app.getName());
					values.put(COLUMN_ACTIVITY, app.getActivity());
					values.put(COLUMN_PACKAGE, app.getPackageName());
					values.put(COLUMN_ICON, app.getIcon());
					
					//Log.w("insert", app.toString());
					mDb.insert(TABLE, null, values);
					
				} else{
					ContentValues update=new ContentValues();
					update.put(COLUMN_NAME, app.getName());
					update.put(COLUMN_ICON, app.getIcon());
					//Log.w("update", app.toString());
					mDb.update(TABLE, update, where, null);
					
				}
			}
		}
		
		public ArrayList<InfoLaunchApplication> getApplications(){
			ArrayList<InfoLaunchApplication> ret=new ArrayList<InfoLaunchApplication>();
			if (mDb!=null){
				Cursor cursor = mDb.query(TABLE, ALL_COLUMNS, null, null, null, null, null);
				if (cursor.getCount()>0){
					cursor.moveToFirst();
					InfoLaunchApplication app;
					do{
						app=new InfoLaunchApplication(cursor.getString(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3), "");
						//Log.d("Scheme", "do:"+app );
						ret.add(app);
					}while (cursor.moveToNext());
				}
			}
			return ret;
		}
	}
	/*
	public static class Documents{
		public static final String TABLE="Documents";
		public static final String COLUMN_NAME="name";
		public static final String COLUMN_PATH="path";
		public static final String COLUMN_TYPE="type";
		
		private static final String CREATE="CREATE TABLE "+TABLE+" ("+
				COLUMN_NAME+" text not null,"+
				COLUMN_PATH+" text not null,"+
				COLUMN_TYPE+" text not null,"+
				"primary key ("+COLUMN_NAME+", "+COLUMN_PATH+"));";

		public static void create(SQLiteDatabase db){
			db.execSQL(CREATE);
		}
		
		public static void upgrade(SQLiteDatabase db, int oldVersion, int newVersion){
			
		}
	}
	
	public static class Statistics{
		public static final String TABLE="Statistics";
		public static final String COLUMN_TYPE="type";
		public static final String COLUMN_ID="id";
		public static final String COLUMN_COUNT="count";
		public static final String COLUMN_LASTLAUNCH="ts";
		
		private static final String CREATE="CREATE TABLE "+TABLE+" ("+
				COLUMN_TYPE+" text not null,"+
				COLUMN_ID+" text not null,"+
				COLUMN_COUNT+" integer not null,"+
				COLUMN_LASTLAUNCH+" integer not null,"+
				"primary key ("+COLUMN_TYPE+","+COLUMN_ID+"));";
		
		public static void create(SQLiteDatabase db){
			db.execSQL(CREATE);
		}
		
		public static void upgrade(SQLiteDatabase db, int oldVersion, int newVersion){
			
		}
	}*/

}