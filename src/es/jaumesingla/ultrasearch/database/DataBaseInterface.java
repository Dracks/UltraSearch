package es.jaumesingla.ultrasearch.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DataBaseInterface {
	private SQLiteDatabase 	mDb;
	private SQLiteHelper 	sqliteHelper;
	
	public DataBaseInterface(Context c){
		sqliteHelper=new SQLiteHelper(c);
	}
	
	public void open(){
		mDb=sqliteHelper.getWritableDatabase();
	}
	
	public void close(){
		sqliteHelper.close();
		mDb=null;
	}
	
	public Scheme.Applications getApplications(){
		return new Scheme.Applications(mDb);
	}

}
