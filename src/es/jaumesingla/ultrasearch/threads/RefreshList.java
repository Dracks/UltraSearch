package es.jaumesingla.ultrasearch.threads;

import java.util.ArrayList;

import android.content.pm.ResolveInfo;

import es.jaumesingla.ultrasearch.search.MainActivity;
import es.jaumesingla.ultrasearch.search.InfoPackage;

public class RefreshList implements Runnable {
	private MainActivity dependence;
	
	public RefreshList(MainActivity base){
		dependence=base;
	}
	
	public class sendRefresh implements Runnable{
		ArrayList<ResolveInfo> newData;
		public sendRefresh(ArrayList<ResolveInfo> nData){
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
		ArrayList<ResolveInfo> newData=new ArrayList<ResolveInfo>();
		ArrayList<InfoPackage> listPrograms=null;
		String filter=null;
		synchronized (dependence) {
			listPrograms=dependence.getListPackages();
			filter=dependence.getFilter();
		}
		
		for (InfoPackage ip: listPrograms){
			if (ip.contains(filter)){
				newData.add(ip.getData());
			}
		}
		
		dependence.getHandlerView().post(new sendRefresh(newData));
		dependence.finishRefresh();
	}

}
