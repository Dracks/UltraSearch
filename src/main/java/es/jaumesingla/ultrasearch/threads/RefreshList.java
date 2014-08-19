package es.jaumesingla.ultrasearch.threads;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import junit.framework.Assert;

import es.jaumesingla.ultrasearch.Constants;
import es.jaumesingla.ultrasearch.Constants.ListOrder;
import es.jaumesingla.ultrasearch.UltraSearchApp;
import es.jaumesingla.ultrasearch.model.InfoLaunchApplication;
import es.jaumesingla.ultrasearch.search.MainActivity;

public class RefreshList implements Runnable {
	private static final String TAG = "RefreshList";
	private MainActivity dependence;
	private Comparator<InfoLaunchApplication> comparator;
	
	public RefreshList(MainActivity base){
		dependence=base;
		ListOrder order = ListOrder.valueOf(UltraSearchApp.getInstance().getPreferences().getString(Constants.Preferences.LIST_ORDER, ListOrder.ALPHABETIC.toString()));
		
		switch(order){
		case ALPHABETIC:
			comparator=InfoLaunchApplication.getSortByName();
			break;
		case LAST_RUN:
			comparator=InfoLaunchApplication.getSortByLaunch();
			break;
		case RUN_COUNT:
			comparator=InfoLaunchApplication.getSortByLaunchCount();
			break;
		default:
			break;
		}
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
		
		Collections.sort(newData, comparator);
		
		dependence.getHandlerView().post(new sendRefresh(newData));
		dependence.finishRefresh();
	}

}
