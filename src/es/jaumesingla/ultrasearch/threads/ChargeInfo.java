package es.jaumesingla.ultrasearch.threads;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
//import android.util.Log;

import es.jaumesingla.ultrasearch.MainActivity;
import es.jaumesingla.ultrasearch.MainActivity.InfoPackage;

public class ChargeInfo implements Runnable {
//	private static final String TAG = "ChargeInfo";
	private ArrayList<InfoPackage> listPackages;
	private MainActivity dependences;
	private PackageManager pm;
	
	
	public ChargeInfo(MainActivity base){
		listPackages=new ArrayList<MainActivity.InfoPackage>();
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
			listPackages.add(dependences.new InfoPackage(rinfo, pm));
		}
		synchronized(dependences){
			dependences.setListPackages(listPackages);
		}
	}
	
}
