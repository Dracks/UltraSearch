package es.jaumesingla.ultrasearch.service;

import junit.framework.Assert;
import es.jaumesingla.ultrasearchfree.UltraSearchApp;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class UpdateService extends Service {

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		UltraSearchApp app=UltraSearchApp.getInstance();
		Assert.assertNotNull(app);
		app.launchRefreshDataBase();
		return Service.START_NOT_STICKY;
	}
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}
