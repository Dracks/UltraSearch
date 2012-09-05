package es.jaumesingla.ultrasearch;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

public class UltraSearchApp extends Application {
	
	private static final String TAG = "UltraSearchApp";
	private static final String preferenceName = "QuickSearchPreferences";
	private static UltraSearchApp instance; 
	
	public static UltraSearchApp getInstance(){
		return instance;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "onCreate");
		instance=this;
	}
	
	@Override
	public void onTerminate() {
		super.onTerminate();
	}
	
	public SharedPreferences getPreferences(){
		return getSharedPreferences(preferenceName, 0);
	}

}
