package es.jaumesingla.ultrasearch.service;

import junit.framework.Assert;
import es.jaumesingla.ultrasearch.UltraSearchApp;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DatabaseUpdateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.v("DataBaseUpdateReceiver", "onReceive");
		/*Intent service = new Intent(context, UpdateService.class);
	    context.startService(service);//*/
		UltraSearchApp app=UltraSearchApp.getInstance();
		Assert.assertNotNull(app);
		app.launchRefreshDataBase();
	}

}
