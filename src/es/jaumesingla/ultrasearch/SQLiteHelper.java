package es.jaumesingla.ultrasearch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

@SuppressLint("NewApi")
public class SQLiteHelper extends SQLiteOpenHelper {

	public static final String TABLE_APPLICATIONS="Applications";
	public static final String TABLE_DOCUMENTS="Documents";
	public static final String COLUMN_APP_ID="id";
	public static final String COLUMN_Document_ID="id";
	public static final String COLUMN_APP_NAME="name";
	public static final String COLUMN_DOCUMENT_NAME="name";
	public static final String COLUMN_APP_ICON="iconId";
	
	
	
	public SQLiteHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	public SQLiteHelper(Context context, String name, CursorFactory factory,
			int version, DatabaseErrorHandler errorHandler) {
		super(context, name, factory, version, errorHandler);
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
