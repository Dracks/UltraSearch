package es.jaumesingla.ultrasearch.search.viewlisteners;

import es.jaumesingla.ultrasearch.UltraSearchApp;
import es.jaumesingla.ultrasearch.model.InfoLaunchApplication;
import es.jaumesingla.ultrasearch.search.ResultsViewAdapter.ViewHolder;
import android.view.View;
import android.view.View.OnClickListener;

public class ShowOptions implements OnClickListener {


	private final InfoLaunchApplication item;

	public ShowOptions(InfoLaunchApplication item){
		this.item=item;
	}
	@Override
	public void onClick(View v) {
		UltraSearchApp.getInstance().launchOptionsApp(this.item);
	}

}
