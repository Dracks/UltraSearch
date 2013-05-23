package es.jaumesingla.ultrasearch.widgets;

import android.appwidget.AppWidgetProviderInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import es.jaumesingla.ultrasearch.Constants;
import es.jaumesingla.ultrasearch.R;
import es.jaumesingla.ultrasearch.UltraSearchApp;
import es.jaumesingla.ultrasearch.database.Scheme;
import es.jaumesingla.ultrasearch.model.InfoLaunchApplication;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import es.jaumesingla.ultrasearch.widgets.activities.ListWidgetSettings;
import junit.framework.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

public class ListWidgetProvider extends AppWidgetProvider {
	
	public static final String LAUNCH_APP = "LaunchApp";
	private static final String TAG = "ListWidgetProvider";
	public static final String LAUNCH_PACKAGE_NAME = "LaunchPackageName";
	public static final String LAUNCH_ACTIVITY_NAME = "LaunchActivityName";
    public static final String COMPARATOR = "ComparatorConfiguration";
	private static final String ACTION_UPDATE_LIST = "es.jaumesingla.UltraSearchApp.ACTION_UPDATE_WIDGET";

    public static void debugBundle(Bundle data){
        Set<String> list=data.keySet();
        for (final String key: list){
            Log.d("Debug Bundle", key + "=>" + data.get(key)+ "("+data.get(key).getClass().getName()+")");
        }
    }
	
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		
		for (int widgetId : appWidgetIds){
			ListWidgetProvider.updateWidget(context, appWidgetManager, widgetId);
		}
	}

	public static void completeRemoteViewCell(Context context, RemoteViews cell, InfoLaunchApplication app) throws PackageManager.NameNotFoundException {
		PackageManager pm=context.getPackageManager();
		Drawable icon=context.getResources().getDrawable(R.drawable.ic_launcher);

		icon=pm.getResourcesForApplication(app.getPackageName()).getDrawable(app.getIcon());

		BitmapDrawable bitmapIcon=(BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_launcher);
		if (icon instanceof BitmapDrawable) {
			bitmapIcon = (BitmapDrawable) icon;
		}
		cell.setImageViewBitmap(R.id.ivWidgetCell, bitmapIcon.getBitmap());
		cell.setTextViewText(R.id.tvWidgetName, app.getName());

		Bundle extras=new Bundle();
		//extras.putSerializable(LaunchAppActivity.APP_INFO_KEY,app);
		extras.putString(ListWidgetProvider.LAUNCH_PACKAGE_NAME, app.getPackageName());
		extras.putString(ListWidgetProvider.LAUNCH_ACTIVITY_NAME, app.getActivity());
		extras.putInt("TestInt", 34);
		Intent intent = new Intent();
		intent.putExtras(extras);
		PendingIntent pendingIntent=PendingIntent.getActivity(context,0,intent,0,extras);

		cell.setOnClickPendingIntent(R.id.widgetCell, pendingIntent);
		//cell.setOnClickFillInIntent();

	}
	
	public static void updateWidget(Context context, AppWidgetManager appWidgetManager, int widgetId){
		Log.d(TAG, "updateWidget:"+widgetId);
		//Intent adapterIntent=new Intent(context, ListWidgetService.class);
	      
		//adapterIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        //adapterIntent.putExtra(COMPARATOR, UltraSearchApp.getInstance().getListWidgetOrder(widgetId).toString());
		//adapterIntent.setData(Uri.parse(adapterIntent.toUri(Intent.URI_INTENT_SCHEME)));

		final float scale = context.getResources().getDisplayMetrics().density;

		float cellHeight=(49)*scale;
		float cellWidth=(49)*scale;

		float sizex=cellWidth*3;
		float sizey=cellHeight;
		try{
			Bundle data = appWidgetManager.getAppWidgetOptions(widgetId);
			debugBundle(data);

			sizex=data.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
			sizey=data.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);
		} catch (NoSuchMethodError e){

		}

		UltraSearchApp app = UltraSearchApp.getInstance();

		Scheme.Applications appsInterface = UltraSearchApp.getInstance().getDataBase().getApplications();
		ArrayList<InfoLaunchApplication> listApps=appsInterface.getApplications();
		//comparator=(Comparator<InfoLaunchApplication>) intent.getSerializableExtra(ListWidgetProvider.COMPARATOR);
		Comparator<InfoLaunchApplication> comparator=null;
		switch (app.getListWidgetOrder(widgetId)) {
			case ALPHABETIC:
				comparator=InfoLaunchApplication.getSortByName();
				break;
			case LAST_RUN:
				comparator=InfoLaunchApplication.getSortByLaunch();
				break;
			case RUN_COUNT:
				comparator=InfoLaunchApplication.getSortByLaunchCount();
				break;
		}
		Collections.sort(listApps, comparator);

		RemoteViews widgetView = new RemoteViews(context.getPackageName(), R.layout.widget_list);
		widgetView.removeAllViews(R.id.widget_column);

		int numSizeX= (int) (sizex/cellWidth);
		int numSizeY=(int)(sizey/cellHeight);
		Log.d(TAG, "NumSize:"+numSizeX+","+numSizeY);
		for (int i=0; i<numSizeY; i++){
			Log.d(TAG, "Printing row i"+ i);
			RemoteViews widgetRowView=new RemoteViews(context.getPackageName(), R.layout.widget_list_row);
			widgetView.addView(R.id.widget_column,widgetRowView);
			for (int j=0; j<numSizeX; j++){
				Log.d(TAG, "Printing column j"+ j);
				RemoteViews view=new RemoteViews(context.getPackageName(), R.layout.cell_widget);
				widgetRowView.addView(R.id.widget_row,view);
				int index=i*numSizeX+j;
				boolean completed=false;
				while (!completed){
					try{
						completeRemoteViewCell(context, view, listApps.get(index));
						completed=true;
					} catch (Exception e){
						e.printStackTrace();
						listApps.remove(index);
					}
				}
			}
		}
		
		//widgetView.setRemoteAdapter(R.id.widget_column, adapterIntent);
		
		Intent launchIntent = new Intent(context, ListWidgetProvider.class);
		launchIntent.setAction(LAUNCH_APP);
		launchIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
		launchIntent.setData(Uri.parse(launchIntent.toUri(Intent.URI_INTENT_SCHEME)));	
		
		//PendingIntent pendingIntentTemplate = PendingIntent.getBroadcast(context, 0, launchIntent,  PendingIntent.FLAG_UPDATE_CURRENT);
		PendingIntent pendingIntentTemplate = PendingIntent.getBroadcast(context, 0, launchIntent,  PendingIntent.FLAG_UPDATE_CURRENT);
		
		//widgetView.setPendingIntentTemplate(R.id.widget_column, pendingIntentTemplate);
		
		appWidgetManager.updateAppWidget(widgetId, widgetView);
		/*try{
			appWidgetManager.notifyAppWidgetViewDataChanged(widgetId, R.id.widget_column);
		} catch (Exception e){
			e.printStackTrace();
		}*/
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
       /* Log.d(TAG, "Debug Intent.getExtras:");
		if (intent.getExtras()!=null){
        debugBundle(intent.getExtras());
        if (intent.getExtras().containsKey("appWidgetOptions")){
			Log.d(TAG, "Debug appWidgetOptions!!!!!!!!");
            debugBundle(intent.getExtras().getBundle("appWidgetOptions"));
        }
		}
        Log.d(TAG, "End debug intent.getExtras");*/
		if (intent.getAction().equals(LAUNCH_APP)){
			//Log.i(TAG, "Extra Widget id:"+intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID));
			//Log.i(TAG, "Extra 2:"+ intent.getExtras().getInt("TestInt"));
			
			Log.i(TAG, "Extras:"+intent.getStringExtra(LAUNCH_PACKAGE_NAME)+"/"+intent.getStringExtra(LAUNCH_ACTIVITY_NAME));
			
			InfoLaunchApplication app = UltraSearchApp.getInstance().getDataBase().getApplications().getApplication(intent.getStringExtra(LAUNCH_PACKAGE_NAME), intent.getStringExtra(LAUNCH_ACTIVITY_NAME));
			Intent newIntent=app.getIntentLaunch();
			UltraSearchApp.getInstance().getDataBase().getStatistics().launchApp(app);
			context.startActivity(newIntent);
		} else if (intent.getAction().equals(ListWidgetProvider.ACTION_UPDATE_LIST)){
			
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
			ComponentName cmp=new ComponentName(context, ListWidgetProvider.class);
			this.onUpdate(context, appWidgetManager, appWidgetManager.getAppWidgetIds(cmp));
		} else if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_OPTIONS_CHANGED) || intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)){
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
			updateWidget(context, appWidgetManager, intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID));
		}
	}//*/

	public static void launchUpdate(){
		Intent i=new Intent(ListWidgetProvider.ACTION_UPDATE_LIST);
		UltraSearchApp.getInstance().getBaseContext().sendBroadcast(i);
	}
}
