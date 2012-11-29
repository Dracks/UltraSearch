package es.jaumesingla.ultrasearch;

import java.util.ArrayList;
import java.util.Calendar;

import es.jaumesingla.ultrasearch.Constants.ListMode;
import es.jaumesingla.ultrasearch.Constants.ListOrder;
import es.jaumesingla.ultrasearch.Constants.ListServiceUpdate;
import es.jaumesingla.ultrasearch.database.DataBaseInterface;
import es.jaumesingla.ultrasearch.database.Scheme;
import es.jaumesingla.ultrasearch.model.InfoLaunchApplication;
import es.jaumesingla.ultrasearch.service.DatabaseUpdateReceiver;
import es.jaumesingla.ultrasearch.threads.ChargeInfo;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
		listNotifications=new ArrayList<DataBaseChanged>();
		preferences=getSharedPreferences(preferenceName, 0);
		
		if (preferences.getBoolean(Constants.Preferences.UPDATE_DB_ON_START, true)){
			this.launchRefreshDataBase();
		}
		int version=preferences.getInt(Constants.Preferences.VERSION_KEY, 0);
		if (version==0){
			this.createConfiguration();
		}else if (version !=Constants.Preferences.VERSION){
			this.upgradeConfiguration(version);
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
		appInterface.clear();
		for (InfoLaunchApplication app: appList){
			appInterface.addApplication(app);
		}
		
		this.onDataBaseChanged();
	}
	
	private void createConfiguration() {
		Editor config = this.preferences.edit();
		config.putInt(Constants.Preferences.VERSION_KEY, Constants.Preferences.VERSION);
		config.putBoolean(Constants.Preferences.UPDATE_DB_ON_START, false);
		config.putString(Constants.Preferences.LIST_MODE_KEY, ListMode.LIST.toString());
		config.putString(Constants.Preferences.UPDATE_SERVICE_KEY, ListServiceUpdate.TWO_DAYS.toString());
		config.putString(Constants.Preferences.LIST_ORDER, ListOrder.ALPHABETIC.toString());
		config.commit();
	}

	private void upgradeConfiguration(int version) {
		Editor config=this.preferences.edit();
		switch (version){
		case 1:
			config.putString(Constants.Preferences.LIST_ORDER, ListOrder.ALPHABETIC.toString());
		}
		config.commit();
		
	}

    public void setListWidgetConfiguration(int widgetId, Constants.ListOrder order){
        Editor config = this.preferences.edit();

        config.putString(Constants.Preferences.LIST_WIDGET_PREFIX+widgetId, order.toString());

        config.commit();
    }

    public Constants.ListOrder getListWidgetOrder(int widgetId){
        return Constants.ListOrder.valueOf(this.preferences.getString(Constants.Preferences.LIST_WIDGET_PREFIX+widgetId, Constants.ListOrder.ALPHABETIC.toString()));
    }



	public void launchAutoUpdate(int triger) {
		Log.v(TAG, "launchAutoUpdate"+triger);
		AlarmManager service = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(getApplicationContext(), DatabaseUpdateReceiver.class);
        PendingIntent pending = PendingIntent.getBroadcast(getApplicationContext(), 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
        
        
		long repeat=2;
        switch (ListServiceUpdate.valueOf(UltraSearchApp.getInstance().getPreferences().getString(Constants.Preferences.UPDATE_SERVICE_KEY, ListServiceUpdate.TWO_DAYS.toString()))){
        	case DAY:
        		repeat=1;
        	break;
        	case TWO_DAYS:
        		repeat=2;
        	break;
        	case WEEKLY:
        		repeat=7;
        	break;
        	case MONTHLY:
        		repeat=30;
        	break;
        	case NEVER:
        		service.cancel(pending);
        		return;
       
        }
        Log.d(TAG, "repeat"+repeat);
        Calendar cal = Calendar.getInstance();
        // Start 120 seconds after boot completed
        cal.add(Calendar.SECOND, triger);
        
        
		service.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), repeat*Constants.Time.DAYS, pending);
	}
}
