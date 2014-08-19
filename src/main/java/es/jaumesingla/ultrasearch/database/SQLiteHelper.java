package es.jaumesingla.ultrasearch.database;

import es.jaumesingla.ultrasearch.Constants;
import es.jaumesingla.ultrasearch.UltraSearchApp;
import es.jaumesingla.ultrasearch.Constants.Database;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;


public class SQLiteHelper extends SQLiteOpenHelper {
	
	public SQLiteHelper(Context context) {
		super(context, Constants.Database.NAME, null, Constants.Database.VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Scheme.Applications.create(db);
		//Scheme.Documents.create(db);
		Scheme.Statistics.create(db);
		UltraSearchApp.getInstance().launchRefreshDataBase();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Scheme.Applications.upgrade(db, oldVersion, newVersion);
		if (oldVersion==1){
			Scheme.Statistics.create(db);
		} else {
			Scheme.Statistics.upgrade(db, oldVersion, newVersion);
		}
		//Scheme.Documents.upgrade(db, oldVersion, newVersion);
		//Scheme.Statistics.upgrade(db, oldVersion, newVersion);
	}

}
