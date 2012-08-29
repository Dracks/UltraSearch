package es.jaumesingla.ultrasearchfree.threads;

import java.util.ArrayList;

import es.jaumesingla.ultrasearchfree.MainActivity;
import es.jaumesingla.ultrasearchfree.ResultsViewAdapter;
import es.jaumesingla.ultrasearchfree.MainActivity.InfoPackage;

public class RefreshList implements Runnable {
	private MainActivity dependence;
	
	public RefreshList(MainActivity base){
		dependence=base;
	}
	
	public class sendRefresh implements Runnable{
		ResultsViewAdapter newAdapter;
		public sendRefresh(ResultsViewAdapter nAdapter){
			newAdapter=nAdapter;
		}
		@Override
		public void run() {
			RefreshList.this.dependence.setListAdapter(newAdapter);
		}
		
	}
	@Override
	public void run() {
		dependence.refreshOnProgress();
		ResultsViewAdapter newAdapter=new ResultsViewAdapter(dependence.getLayoutInflater(), dependence.getPackageManager());
		ArrayList<MainActivity.InfoPackage> listPrograms=null;
		String filter=null;
		synchronized (dependence) {
			listPrograms=dependence.getListPackages();
			filter=dependence.getFilter();
		}
		
		for (InfoPackage ip: listPrograms){
			if (ip.contains(filter)){
				newAdapter.add(ip.getData());
			}
		}
		
		dependence.getHandlerView().post(new sendRefresh(newAdapter));
		dependence.finishRefresh();
	}

}
