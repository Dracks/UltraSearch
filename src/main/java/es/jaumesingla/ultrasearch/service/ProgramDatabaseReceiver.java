package es.jaumesingla.ultrasearch.service;


import junit.framework.Assert;

import es.jaumesingla.ultrasearchfree.UltraSearchApp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ProgramDatabaseReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		UltraSearchApp app = UltraSearchApp.getInstance();
		Assert.assertNotNull(app);
		app.launchAutoUpdate(2);
	}

}
