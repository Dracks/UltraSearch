package es.jaumesingla.ultrasearch.search.viewlisteners;

import es.jaumesingla.ultrasearchfree.R;
import es.jaumesingla.ultrasearch.model.InfoLaunchApplication;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;

public class ShareApplication implements OnClickListener {

	private InfoLaunchApplication app;
	private Context mContext;
	
	public ShareApplication(Context c,InfoLaunchApplication app){
		this.app=app;
		this.mContext=c;
	}
	@Override
	public void onClick(View arg0) {
		Intent share=new Intent(Intent.ACTION_SEND);
		share.setType("text/plain");
		share.putExtra(Intent.EXTRA_TEXT, "http://play.google.com/store/apps/details?id="+app.getPackageName());
		mContext.startActivity(Intent.createChooser(share, mContext.getResources().getString(R.string.share)));
	}

}
