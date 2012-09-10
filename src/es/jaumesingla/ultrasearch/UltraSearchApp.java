package es.jaumesingla.ultrasearch;

import java.util.ArrayList;

import es.jaumesingla.ultrasearch.database.DataBaseInterface;
import es.jaumesingla.ultrasearch.database.Scheme;
import es.jaumesingla.ultrasearch.model.InfoLaunchApplication;
import es.jaumesingla.ultrasearch.threads.ChargeInfo;
import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

public class UltraSearchApp extends Application {
	
	public interface DataBaseChanged{
		public void onDataBaseChanged();
	}
	
	private static final String TAG = "UltraSearchApp";
	private static final String preferenceName = "QuickSearchPreferences";
	private static UltraSearchApp instance; 
	
	
	private DataBaseInterface dbi;
	
	private ArrayList<DataBaseChanged> listNotifications;
	private SharedPreferences preferences;
	
	public static UltraSearchApp getInstance(){
		return instance;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "onCreate");
		instance=this;
		dbi=new DataBaseInterface(this.getApplicationContext());
		dbi.open();
		this.launchRefreshDataBase();
		listNotifications=new ArrayList<DataBaseChanged>();
		preferences=getSharedPreferences(preferenceName, 0);
		
		if (preferences.getInt(Constants.Preferences.VERSION_KEY, Constants.Preferences.VERSION)!=Constants.Preferences.VERSION){
			this.upgradeConfiguration(preferences.getInt(Constants.Preferences.VERSION_KEY, Constants.Preferences.VERSION));
		}
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}
	
	public SharedPreferences getPreferences(){
		return preferences;
	}
	
	public DataBaseInterface getDataBase(){
		return dbi;
	}
	
	public void registerOnDataBaseChanged(DataBaseChanged dbc){
		listNotifications.add(dbc);
	}
	
	public void unregisterOnDataBaseChanged(DataBaseChanged dbc){
		listNotifications.remove(dbc);
	}
	
	public void launchRefreshDataBase(){
		new Thread(new ChargeInfo(getApplicationContext())).start();
	}
	
	public void onDataBaseChanged(){
		for (DataBaseChanged dbc: listNotifications){
			dbc.onDataBaseChanged();
		}
	}
	
	public void chargeDataBase(ArrayList<InfoLaunchApplication> appList){
		Scheme.Applications appInterface=dbi.getApplications();
		
		for (InfoLaunchApplication app: appList){
			appInterface.addApplication(app);
		}
		
		this.onDataBaseChanged();
	}

	private void upgradeConfiguration(int version) {
		
	}
}
