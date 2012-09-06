package es.jaumesingla.ultrasearch.threads;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
//import android.util.Log;

import es.jaumesingla.ultrasearch.model.InfoLaunchApplication;
import es.jaumesingla.ultrasearch.search.MainActivity;

public class ChargeInfo implements Runnable {
//	private static final String TAG = "ChargeInfo";
	private ArrayList<InfoLaunchApplication> listPackages;
	private MainActivity dependences;
	private PackageManager pm;
	
	
	public ChargeInfo(MainActivity base){
		listPackages=new ArrayList<InfoLaunchApplication>();
		dependences=base;
		pm=base.getPackageManager();
	}
	@Override
	public void run() {
		Intent i=new Intent(Intent.ACTION_MAIN);
		

		i.addCategory(Intent.CATEGORY_LAUNCHER);

		List<ResolveInfo> listQuery=pm.queryIntentActivities(i, 0);
		for (ResolveInfo rinfo: listQuery){
			//Log.d(TAG, rinfo.activityInfo.name);
			String name=rinfo.loadLabel(pm).toString();
			CharSequence d=rinfo.activityInfo.applicationInfo.loadDescription(pm);
			String description;
			if (d!=null){
				description=d.toString();
			} else {
				description="";
			}
			String packageName=rinfo.activityInfo.packageName;
			String activity=rinfo.activityInfo.name;
			
			int ic=rinfo.activityInfo.icon;
			if (ic==0)
				ic=rinfo.activityInfo.applicationInfo.icon;
			Assert.assertTrue(ic!=0);
			
			listPackages.add(new InfoLaunchApplication(name, ic,activity, packageName, description));
		}
		synchronized(dependences){
			dependences.setListPackages(listPackages);
		}
	}
	
}
