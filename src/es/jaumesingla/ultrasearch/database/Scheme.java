package es.jaumesingla.ultrasearch.database;

import android.database.sqlite.SQLiteDatabase;

public class Scheme {
	public static class Applications{
		public static final String TABLE="Applications";
		public static final String COLUMN_NAME="name";
		public static final String COLUMN_ACTIVITY="activity";
		public static final String COLUMN_PACKAGE="package";
		public static final String COLUMN_ICON="icon";
		
		private static final String CREATE="CREATE TABLE "+TABLE+" ("+
						COLUMN_NAME+" text not null,"+
						COLUMN_ACTIVITY+" text not null,"+
						COLUMN_PACKAGE+" text not null,"+
						COLUMN_ICON+" integer not null,"+
						"primary key ("+COLUMN_ACTIVITY+","+COLUMN_PACKAGE+"));";
		
		public static void create(SQLiteDatabase db){
			db.execSQL(CREATE);
		}
		
		public static void upgrade(SQLiteDatabase db, int oldVersion, int newVersion){
			
		}
	}
	
	public static class Documents{
		public static final String TABLE="Documents";
		public static final String COLUMN_NAME="name";
		public static final String COLUMN_PATH="path";
		public static final String COLUMN_TYPE="type";
		
		private static final String CREATE="CREATE TABLE "+TABLE+" ("+
				COLUMN_NAME+" text not null,"+
				COLUMN_PATH+" text not null,"+
				COLUMN_TYPE+" text not null"+
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
	}

}
