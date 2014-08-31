package es.jaumesingla.ultrasearch;

import java.util.ArrayList;
import java.util.Calendar;

import es.jaumesingla.ultrasearch.Constants.ListMode;
import es.jaumesingla.ultrasearch.Constants.ListOrder;
import es.jaumesingla.ultrasearch.Constants.ListServiceUpdate;
import es.jaumesingla.ultrasearch.database.DataBaseInterface;
import es.jaumesingla.ultrasearch.database.Scheme;
import es.jaumesingla.ultrasearch.model.InfoLaunchApplication;
import es.jaumesingla.ultrasearch.search.AdvertisementActivity;
import es.jaumesingla.ultrasearch.search.MainActivity;
import es.jaumesingla.ultrasearch.search.viewlisteners.OptionsAppClickListener;
import es.jaumesingla.ultrasearch.service.DatabaseUpdateReceiver;
import es.jaumesingla.ultrasearch.threads.ChargeInfo;
import es.jaumesingla.ultrasearchfree.R;
import es.jaumesingla.ultrasearchfree.share.ShareLimitedActivity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Application;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

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
	private MainActivity currentMain;
	
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

	public void registerActivity(MainActivity current){
		this.currentMain=current;
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
		config.putBoolean(Constants.Preferences.UPDATE_DB_ON_START, true);
		config.putString(Constants.Preferences.LIST_MODE_KEY, ListMode.LIST.toString());
		config.putString(Constants.Preferences.UPDATE_SERVICE_KEY, ListServiceUpdate.NEVER.toString());
		config.putString(Constants.Preferences.LIST_ORDER, ListOrder.LAST_RUN.toString());
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

	public long getEndFreeAds(){
		return preferences.getLong(Constants.Free.TIME_BLOQUED_ADS_KEY, 0);
	}

	public void addDaysFreeAds(){
		long now=System.currentTimeMillis()/1000;
		long addTime=Constants.Free.DAYS_FREE_PRESENT*24*3600;
		long timeBloquedAds=preferences.getLong(Constants.Free.TIME_BLOQUED_ADS_KEY, 0);
		if (timeBloquedAds<now){
			timeBloquedAds=now+addTime;
		} else {
			timeBloquedAds+=addTime;
		}

		SharedPreferences.Editor settings = preferences.edit();
		settings.putLong(Constants.Free.TIME_BLOQUED_ADS_KEY, timeBloquedAds);
		settings.apply();
	}

	public long getNextSpamActivity(){
		return preferences.getLong(Constants.Free.NEXT_SPAM_ACTIVITY_SHOW_KEY, 0);
	}

	public void setNextSpamActivity(){
		SharedPreferences.Editor settings = preferences.edit();
		settings.putLong(Constants.Free.NEXT_SPAM_ACTIVITY_SHOW_KEY, Constants.Free.NEXT_SPAM_ACTIVITY_SHOW+System.currentTimeMillis()/1000);
		settings.apply();
	}

    public void setListWidgetConfiguration(int widgetId, Constants.ListOrder order, String query){
        Editor config = this.preferences.edit();

        config.putString(Constants.Preferences.LIST_WIDGET_PREFIX+widgetId, order.toString()+";"+query);

        config.commit();
    }

    public Bundle getListWidgetConfiguration(int widgetId){
	    String[] s=this.preferences.getString(Constants.Preferences.LIST_WIDGET_PREFIX+widgetId, ListOrder.LAST_RUN.toString()).split(";");
	    Bundle ret=new Bundle();
	    ret.putString(Constants.WidgetBundle.KEY_ORDER,s[0]);
	    if (s.length>1){
		    ret.putString(Constants.WidgetBundle.KEY_SEARCH, s[1]);
	    } else {
		    ret.putString(Constants.WidgetBundle.KEY_SEARCH, "");
	    }
	    return ret;
        //return Constants.ListOrder.valueOf(this.preferences.getString(Constants.Preferences.LIST_WIDGET_PREFIX+widgetId, Constants.ListOrder.ALPHABETIC.toString()));
    }

	public void setListWidget(int widgetId, int x, int y){
		Editor config = this.preferences.edit();
		Log.d(TAG, "listWidget: "+widgetId+", "+x+", "+y);

		config.putInt(Constants.Preferences.LIST_WIDGET_SPAN + "_x_" + widgetId, x);
		config.putInt(Constants.Preferences.LIST_WIDGET_SPAN+"_y_"+widgetId, y);
		config.commit();
	}

	public int getListWidgetX(int widgetId){
		return this.preferences.getInt(Constants.Preferences.LIST_WIDGET_SPAN+"_x_"+widgetId, 3);
	}

	public int getListWidgetY(int widgetId){
		return this.preferences.getInt(Constants.Preferences.LIST_WIDGET_SPAN+"_y_"+widgetId, 1);
	}

	public void launchApp(InfoLaunchApplication app) {
		UltraSearchApp.getInstance().getDataBase().getStatistics().launchApp(app);
		Intent mIntent=app.getIntentLaunch();
		try {
			startActivity(mIntent);
		} catch (ActivityNotFoundException err) {
			Toast t = Toast.makeText(getApplicationContext(),
					R.string.app_not_found, Toast.LENGTH_SHORT);
			t.show();
		}
	}

	public void launchOptionsApp(InfoLaunchApplication app){
		AlertDialog.Builder builder=new AlertDialog.Builder(currentMain);
		builder.setTitle(R.string.optionsAppTitle);
		CharSequence[] items=getResources().getTextArray(R.array.optionsApp);
		builder.setItems(items, new OptionsAppClickListener(app));
		builder.setCancelable(true);
		builder.show();
	}

	public void launchApplicationInfo(InfoLaunchApplication app){
		Intent intent=null;
		long now=System.currentTimeMillis()/1000;
		if (now<getNextSpamActivity() || now < getEndFreeAds()) {
			intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", app.getPackageName(), null));
			// start new activity to display extended information
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			this.startActivity(intent);
		} else {
			setNextSpamActivity();
			intent = new Intent(this, AdvertisementActivity.class);
			intent.putExtra(Constants.Free.APP_INFO_KEY, app);
			this.startActivity(intent);
		}
	}

	public void shareApplicationInfo(InfoLaunchApplication app){
		Intent share=new Intent(Intent.ACTION_SEND);
		share.setType("text/plain");
		share.putExtra(Intent.EXTRA_TEXT, "http://play.google.com/store/apps/details?id="+app.getPackageName());
		startActivity(Intent.createChooser(share, getResources().getString(R.string.share)));
	}

	public void shareProgram(Activity activity) {
		Intent share = new Intent(activity, ShareLimitedActivity.class);
		share.setType("text/plain");
		share.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.shareTextTitle));
		share.putExtra(Intent.EXTRA_TEXT, String.format(getString(R.string.shareText), getString(R.string.app_name), getPackageName()));

		share.putExtra(ShareLimitedActivity.FILTER_NAME, "twitter|plus|facebook");
		share.putExtra(ShareLimitedActivity.TITLE_WINDOW, getString(R.string.shareTitle));
		share.putExtra(ShareLimitedActivity.KEY_APPLICATION_NOT_FOUND, getString(R.string.app_not_found));

		//startActivity(Intent.createChooser(share, getString(R.string.shareTitle)));
		activity.startActivityForResult(share, Constants.Free.SHARE_ACTION);
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
