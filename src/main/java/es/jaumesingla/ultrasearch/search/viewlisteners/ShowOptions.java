package es.jaumesingla.ultrasearch.search.viewlisteners;

import es.jaumesingla.ultrasearch.search.ResultsViewAdapter.ViewHolder;
import android.view.View;
import android.view.View.OnClickListener;

public class ShowOptions implements OnClickListener {

		
	private ViewHolder holder;
	
	public ShowOptions(ViewHolder holder){
		this.holder=holder;
	}

	@Override
	public void onClick(View v) {
		int visibility = holder.optionsView.getVisibility();
		if (visibility==View.GONE){
			holder.optionsView.setVisibility(View.VISIBLE);
		} else {
			holder.optionsView.setVisibility(View.GONE);
		}
	}

}
