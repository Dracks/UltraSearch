package es.jaumesingla.ultrasearch.widgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import junit.framework.Assert;

import es.jaumesingla.ultrasearch.Constants;
import es.jaumesingla.ultrasearchfree.R;
import es.jaumesingla.ultrasearch.UltraSearchApp;
import es.jaumesingla.ultrasearch.database.Scheme.Applications;
import es.jaumesingla.ultrasearch.model.InfoLaunchApplication;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

public class ListWidgetFactory implements RemoteViewsService.RemoteViewsFactory {

	private static final String TAG = "ListWidgetFactory";
	private ArrayList<InfoLaunchApplication> listApps;
	private Context context;
    private Comparator<InfoLaunchApplication> comparator;
	

	public ListWidgetFactory(Context ctxt, Intent intent) {
		Log.d(TAG, "ListWidgetFactory");
		Applications appsInterface = UltraSearchApp.getInstance().getDataBase().getApplications();
		listApps=appsInterface.getApplications();
		this.context=ctxt;
        //comparator=(Comparator<InfoLaunchApplication>) intent.getSerializableExtra(ListWidgetProvider.COMPARATOR);
		Assert.assertNotNull(intent.getStringExtra(ListWidgetProvider.COMPARATOR));
        switch (Constants.ListOrder.valueOf(intent.getStringExtra(ListWidgetProvider.COMPARATOR))) {
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
	}
	
	@Override
	public int getCount() {
		return listApps.size();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public RemoteViews getLoadingView() {
		Log.i(TAG, "getLoadingView");
		RemoteViews loading=new RemoteViews(this.context.getPackageName(), R.layout.widget_message);
		return loading;
	}

	@Override
	public RemoteViews getViewAt(int position) {
		Log.i(TAG, "getViewAt:"+position);
		RemoteViews view=new RemoteViews(this.context.getPackageName(), R.layout.cell_widget);
		InfoLaunchApplication app=listApps.get(position);
		//view.setImageViewResource(R.id.ivWidgetCell, app.getIcon());
		PackageManager pm=this.context.getPackageManager();
		Drawable icon=this.context.getResources().getDrawable(R.drawable.ic_launcher);
		try{
			icon=pm.getResourcesForApplication(app.getPackageName()).getDrawable(app.getIcon());
		} catch (Exception e){
			e.printStackTrace();
			listApps.remove(position);
		}
		BitmapDrawable bitmapIcon=(BitmapDrawable) this.context.getResources().getDrawable(R.drawable.ic_launcher);
		if (icon instanceof BitmapDrawable) {
			bitmapIcon = (BitmapDrawable) icon;
		}
		view.setImageViewBitmap(R.id.ivWidgetCell, bitmapIcon.getBitmap());
		view.setTextViewText(R.id.tvWidgetName, app.getName());
		

		Bundle extras=new Bundle();
		//extras.putSerializable(LaunchAppActivity.APP_INFO_KEY,app);
		extras.putString(ListWidgetProvider.LAUNCH_PACKAGE_NAME, app.getPackageName());
		extras.putString(ListWidgetProvider.LAUNCH_ACTIVITY_NAME, app.getActivity());
		extras.putInt("TestInt", 34);
        Intent intent = new Intent();
        intent.putExtras(extras);

        view.setOnClickFillInIntent(R.id.widgetCell, intent);
		return view;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDataSetChanged() {
        Applications appsInterface = UltraSearchApp.getInstance().getDataBase().getApplications();
        listApps=appsInterface.getApplications();
        Collections.sort(listApps, comparator);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		
	}

}
