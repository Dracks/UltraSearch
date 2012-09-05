package es.jaumesingla.ultrasearch.settings;

import junit.framework.Assert;
import es.jaumesingla.ultrasearch.Constants;
import es.jaumesingla.ultrasearch.R;
import es.jaumesingla.ultrasearch.UltraSearchApp;
import es.jaumesingla.ultrasearch.search.MainActivity;
import es.jaumesingla.ultrasearch.search.MainActivity.ListMode;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.RadioButton;

public class SettingsActivity extends Activity {
	
	private RadioButton radioGrid;
	private RadioButton radioList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		radioGrid=(RadioButton) findViewById(R.id.rdGrid);
		radioList=(RadioButton) findViewById(R.id.rdList);
		
		SharedPreferences settings = UltraSearchApp.getInstance().getPreferences();
		ListMode listConfig=ListMode.valueOf(settings.getString(Constants.Preferences.LIST_MODE_KEY, ListMode.LIST.toString()));
		if (listConfig==ListMode.LIST){
			radioList.setChecked(true);
			radioGrid.setChecked(false);
		} else {
			radioList.setChecked(false);
			radioGrid.setChecked(true);
		}
		
	}
	
	@Override
	protected void onPause() {
		super.onDestroy();
		Editor editablePreferences = UltraSearchApp.getInstance().getPreferences().edit();
		ListMode listConfig=MainActivity.ListMode.LIST;
		if (radioGrid.isChecked()){
			listConfig=ListMode.GRID;
		} else if (radioList.isChecked()){
			
		} else {
			Assert.assertTrue(false);
		}
			
		editablePreferences.putString(Constants.Preferences.LIST_MODE_KEY, listConfig.toString());
		
		editablePreferences.commit();
		
	}

}
