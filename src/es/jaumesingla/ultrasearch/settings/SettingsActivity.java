package es.jaumesingla.ultrasearch.settings;

import junit.framework.Assert;
import es.jaumesingla.ultrasearch.Constants;
import es.jaumesingla.ultrasearch.Constants.ListServiceUpdate;
import es.jaumesingla.ultrasearch.R;
import es.jaumesingla.ultrasearch.UltraSearchApp;
import es.jaumesingla.ultrasearch.search.MainActivity;
import es.jaumesingla.ultrasearch.Constants.ListMode;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;

public class SettingsActivity extends Activity {
	
	private RadioButton radioGrid;
	private RadioButton radioList;
	
	private Spinner spinnerUpdateService;
	private ListServiceUpdate[] listServiceUpdateValues=ListServiceUpdate.values();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		radioGrid=(RadioButton) findViewById(R.id.rdGrid);
		radioList=(RadioButton) findViewById(R.id.rdList);
		spinnerUpdateService=(Spinner) findViewById(R.id.spUpdateService);
		
		SharedPreferences settings = UltraSearchApp.getInstance().getPreferences();
		
		ListMode listConfig=ListMode.valueOf(settings.getString(Constants.Preferences.LIST_MODE_KEY, ListMode.LIST.toString()));
		if (listConfig==ListMode.LIST){
			radioList.setChecked(true);
			radioGrid.setChecked(false);
		} else {
			radioList.setChecked(false);
			radioGrid.setChecked(true);
		}
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,  R.array.service_update, android.R.layout.simple_spinner_item );
		
		Assert.assertEquals(adapter.getCount(), listServiceUpdateValues.length);
		
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		spinnerUpdateService.setAdapter(adapter);
		
		ListServiceUpdate option=ListServiceUpdate.valueOf(settings.getString(Constants.Preferences.UPDATE_SERVICE_KEY, ListServiceUpdate.TWO_DAYS.toString()));
		
		spinnerUpdateService.setSelection(getServiceUpdateIndex(option));
		
		
	}
	
	@Override
	protected void onPause() {
		super.onDestroy();
		Editor editablePreferences = UltraSearchApp.getInstance().getPreferences().edit();
		ListMode listConfig=ListMode.LIST;
		if (radioGrid.isChecked()){
			listConfig=ListMode.GRID;
		} else if (radioList.isChecked()){
			
		} else {
			Assert.assertTrue(false);
		}
			
		editablePreferences.putString(Constants.Preferences.LIST_MODE_KEY, listConfig.toString());
		editablePreferences.putString(Constants.Preferences.UPDATE_SERVICE_KEY, listServiceUpdateValues[spinnerUpdateService.getSelectedItemPosition()].toString());
		
		editablePreferences.commit();
	}
	
	private int getServiceUpdateIndex(ListServiceUpdate elem){
		for (int i=0; i<listServiceUpdateValues.length; i++){
			if (listServiceUpdateValues[i]==elem){
				return i;
			}
		}
		Assert.assertTrue(false);
		return 0;
		
	}

}
