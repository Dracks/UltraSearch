package es.jaumesingla.ultrasearch.search.viewlisteners;

import es.jaumesingla.ultrasearch.search.ResultsViewAdapter;
import android.view.View;
import android.view.View.OnClickListener;

public class ShowOptionsPosition implements OnClickListener {
	
	private ResultsViewAdapter adapter;
	private int position;

	public ShowOptionsPosition(ResultsViewAdapter a, int p){
		this.adapter=a;
		this.position=p;
	}
	@Override
	public void onClick(View v) {
		this.adapter.setSelectedItem(position);
		this.adapter.notifyDataSetChanged();
	}

}
