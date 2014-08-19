package es.jaumesingla.ultrasearch.database;

import junit.framework.Assert;
import es.jaumesingla.ultrasearch.database.Scheme.Applications;
import es.jaumesingla.ultrasearch.database.Scheme.Statistics;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DataBaseInterface {
	private SQLiteDatabase 	mDb;
	private SQLiteHelper 	sqliteHelper;
	private Statistics statistics;
	private Applications applications;
	
	public DataBaseInterface(Context c){
		sqliteHelper=new SQLiteHelper(c);
	}
	
	public void open(){
		mDb=sqliteHelper.getWritableDatabase();
		statistics=new Scheme.Statistics(mDb);
		applications=new Scheme.Applications(mDb);
	}
	
	public void close(){
		sqliteHelper.close();
		mDb=null;
	}
	
	public Scheme.Applications getApplications(){
		Assert.assertNotNull(mDb);
		return this.applications;
	}
	
	public Scheme.Statistics getStatistics(){
		Assert.assertNotNull(mDb);
		return this.statistics;
	}

}
