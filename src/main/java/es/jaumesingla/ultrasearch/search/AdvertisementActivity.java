package es.jaumesingla.ultrasearch.search;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import es.jaumesingla.ultrasearch.Constants;
import es.jaumesingla.ultrasearch.UltraSearchApp;
import es.jaumesingla.ultrasearch.model.InfoLaunchApplication;
import es.jaumesingla.ultrasearchfree.R;

public class AdvertisementActivity extends ActionBarActivity {

	private Handler h;
	private InfoLaunchApplication app;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertisment);

		LinearLayout row= (LinearLayout) findViewById(R.id.stackCalculatorRow);
		row.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i=new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse("market://details?id=com.example.android"));
				startActivity(i);
			}
		});

		row= (LinearLayout) findViewById(R.id.ultraSearchRow);
		row.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i=new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse("market://details?id=com.example.android"));
				startActivity(i);
			}
		});

		Button share= (Button) findViewById(R.id.shareButton);
		share.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				UltraSearchApp appInstance = UltraSearchApp.getInstance();
				appInstance.shareProgram(AdvertisementActivity.this);
			}
		});



		app=(InfoLaunchApplication) getIntent().getSerializableExtra(Constants.Free.APP_INFO_KEY);

	    h=new Handler();
	    h.postAtTime(new Runnable() {
		    @Override
		    public void run() {
			    UltraSearchApp.getInstance().launchApp(app);
		    }
	    }, Constants.Free.SPAM_ACTIVITY_TIME*1000);
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		UltraSearchApp appInstance = UltraSearchApp.getInstance();
		switch (requestCode){
			case Constants.Free.SHARE_ACTION:
				if (resultCode== Activity.RESULT_OK) {
					appInstance.addDaysFreeAds();
					appInstance.launchApp(app);
					finish();
				}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.advertisment, menu);
        return false;
    }

}
