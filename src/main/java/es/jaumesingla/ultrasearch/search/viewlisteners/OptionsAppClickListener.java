package es.jaumesingla.ultrasearch.search.viewlisteners;

import android.app.Dialog;
import android.content.DialogInterface;
import android.util.Log;

import es.jaumesingla.ultrasearch.UltraSearchApp;
import es.jaumesingla.ultrasearch.model.InfoLaunchApplication;

/**
 * Created by dracks on 19/08/14.
 */
public class OptionsAppClickListener implements Dialog.OnClickListener {

	private static final String TAG = "OptionsAppClickListener";
	private final InfoLaunchApplication appInfo;

	public OptionsAppClickListener(InfoLaunchApplication app) {
		this.appInfo=app;
	}

	@Override
	public void onClick(DialogInterface dialogInterface, int i) {
		UltraSearchApp app=UltraSearchApp.getInstance();
		switch (i){
			case 0:
				app.launchApp(appInfo);
				break;
			case 1:
				app.shareApplicationInfo(appInfo);
				break;
			case 2:
				app.launchApplicationInfo(appInfo);
				break;
			default:
				Log.w(TAG, "No option found for Application ("+appInfo+") options");
		}
	}
}
