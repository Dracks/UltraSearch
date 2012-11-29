package es.jaumesingla.ultrasearch.widgets;

import es.jaumesingla.ultrasearch.R;
import es.jaumesingla.ultrasearch.UltraSearchApp;
import es.jaumesingla.ultrasearch.model.InfoLaunchApplication;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import es.jaumesingla.ultrasearch.widgets.activities.ListWidgetSettings;

public class ListWidgetProvider extends AppWidgetProvider {
	
	public static final String LAUNCH_APP = "LaunchApp";
	private static final String TAG = "ListWidgetProvider";
	public static final String LAUNCH_PACKAGE_NAME = "LaunchPackageName";
	public static final String LAUNCH_ACTIVITY_NAME = "LaunchActivityName";
    public static final String COMPARATOR = "ComparatorConfiguration";
	
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		
		for (int widgetId : appWidgetIds){
			ListWidgetProvider.updateWidget(context, appWidgetManager, widgetId);
		}
	}
	
	public static void updateWidget(Context context, AppWidgetManager appWidgetManager, int widgetId){
		Log.d(TAG, "updateWidget:"+widgetId);
		Intent adapterIntent=new Intent(context, ListWidgetService.class);
	      
		adapterIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        adapterIntent.putExtra(COMPARATOR, UltraSearchApp.getInstance().getListWidgetOrder(widgetId).toString());
		adapterIntent.setData(Uri.parse(adapterIntent.toUri(Intent.URI_INTENT_SCHEME)));
		
		RemoteViews widgetView = new RemoteViews(context.getPackageName(), R.layout.widget_list);
		
		widgetView.setRemoteAdapter(R.id.widget_grid, adapterIntent);
		
		Intent launchIntent = new Intent(context, ListWidgetProvider.class);
		launchIntent.setAction(LAUNCH_APP);
		launchIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
		launchIntent.setData(Uri.parse(launchIntent.toUri(Intent.URI_INTENT_SCHEME)));	
		
		//PendingIntent pendingIntentTemplate = PendingIntent.getBroadcast(context, 0, launchIntent,  PendingIntent.FLAG_UPDATE_CURRENT);
		PendingIntent pendingIntentTemplate = PendingIntent.getBroadcast(context, 0, launchIntent,  PendingIntent.FLAG_UPDATE_CURRENT);
		
		widgetView.setPendingIntentTemplate(R.id.widget_grid, pendingIntentTemplate);
		
		appWidgetManager.updateAppWidget(widgetId, widgetView);
	}
	/*
	@Override
	public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
		super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId,	newOptions);
		int minW,minH,maxW, maxH;
		minW=newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
		minH=newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
		maxW=newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
		maxH=newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);
		Log.i("ListWidgetProvider", "Min("+minW+","+minH+"), Max("+maxW+","+maxH+")");
	}
	*/
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		Log.d(TAG, "onReceive:"+intent+" extras:"+intent.getExtras());
		if (intent.getAction().equals(LAUNCH_APP)){
			//Log.i(TAG, "Extra Widget id:"+intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID));
			//Log.i(TAG, "Extra 2:"+ intent.getExtras().getInt("TestInt"));
			
			Log.i(TAG, "Extras:"+intent.getStringExtra(LAUNCH_PACKAGE_NAME)+"/"+intent.getStringExtra(LAUNCH_ACTIVITY_NAME));
			
			InfoLaunchApplication app = UltraSearchApp.getInstance().getDataBase().getApplications().getApplication(intent.getStringExtra(LAUNCH_PACKAGE_NAME), intent.getStringExtra(LAUNCH_ACTIVITY_NAME));
			Intent newIntent=app.getIntentLaunch();
			UltraSearchApp.getInstance().getDataBase().getStatistics().launchApp(app);
			context.startActivity(newIntent);
		}
	}//*/
}
