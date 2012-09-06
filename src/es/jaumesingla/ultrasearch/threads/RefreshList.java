package es.jaumesingla.ultrasearch.threads;

import java.util.ArrayList;

import junit.framework.Assert;

import android.content.pm.PackageManager;
import android.util.Log;

import es.jaumesingla.ultrasearch.model.InfoLaunchApplication;
import es.jaumesingla.ultrasearch.search.MainActivity;

public class RefreshList implements Runnable {
	private static final String TAG = "RefreshList";
	private MainActivity dependence;
	
	public RefreshList(MainActivity base){
		dependence=base;
	}
	
	public class sendRefresh implements Runnable{
		ArrayList<InfoLaunchApplication> newData;
		public sendRefresh(ArrayList<InfoLaunchApplication> nData){
			newData=nData;
		}
		@Override
		public void run() {
			RefreshList.this.dependence.setContentListAdapter(newData);
		}
		
	}
	@Override
	public void run() {
		dependence.refreshOnProgress();
		//ResultsViewAdapter newAdapter=new ResultsViewAdapter(dependence.getLayoutInflater(), dependence.getPackageManager());
		ArrayList<InfoLaunchApplication> newData=new ArrayList<InfoLaunchApplication>();
		ArrayList<InfoLaunchApplication> listPrograms=null;
		String filter=null;
		synchronized (dependence) {
			listPrograms=dependence.getListPackages();
			filter=dependence.getFilter();
		}
		Assert.assertNotNull(filter);
		Assert.assertNotNull(listPrograms);
		//Log.d(TAG, filter+ "vs"+ listPrograms.size());
		//PackageManager pm=dependence.getPackageManager();
		
		for (InfoLaunchApplication ip: listPrograms){
			if (ip.contains(filter)){
				//Log.d(TAG, "filter-ok:"+ip);
				newData.add(ip);
			}
		}
		
		dependence.getHandlerView().post(new sendRefresh(newData));
		dependence.finishRefresh();
	}

}
