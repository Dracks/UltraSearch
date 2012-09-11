package es.jaumesingla.ultrasearch.search.viewlisteners;

import es.jaumesingla.ultrasearch.model.InfoLaunchApplication;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class InfoApplication implements OnClickListener {

	private Context mContext;
	private InfoLaunchApplication app;
	public InfoApplication(Context mContext, InfoLaunchApplication app) {
		this.mContext=mContext;
		this.app=app;
	}

	@Override
	public void onClick(View v) {
		Log.d("InfoApplication", "onClick");
		String mCurrentPkgName=app.getPackageName();
		Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,Uri.fromParts("package", mCurrentPkgName, null));
		// start new activity to display extended information
		mContext.startActivity(intent);
	}

}
