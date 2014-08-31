package es.jaumesingla.ultrasearch.settings;

import junit.framework.Assert;

import es.jaumesingla.ultrasearch.Constants;
import es.jaumesingla.ultrasearch.Constants.ListServiceUpdate;
import es.jaumesingla.ultrasearchfree.R;
import es.jaumesingla.ultrasearch.UltraSearchApp;
import es.jaumesingla.ultrasearch.search.MainActivity;
import es.jaumesingla.ultrasearch.Constants.ListMode;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Spinner;

public class SettingsActivity extends Activity {
	
	private RadioButton radioGrid;
	private RadioButton radioList;
	private Spinner spinnerUpdateService;
	private CheckBox updateOnStart;
	
	private ListServiceUpdate[] listServiceUpdateValues=ListServiceUpdate.values();
	private Constants.ListOrder[]	listOrderListValues=Constants.ListOrder.values();
	private Spinner spinnerOrderList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		radioGrid=(RadioButton) findViewById(R.id.rdGrid);
		radioList=(RadioButton) findViewById(R.id.rdList);
		spinnerUpdateService=(Spinner) findViewById(R.id.spUpdateService);
		spinnerOrderList=(Spinner) findViewById(R.id.spOrderList);
		updateOnStart=(CheckBox) findViewById(R.id.chbUpdateOnStart);
		
		SharedPreferences settings = UltraSearchApp.getInstance().getPreferences();
		
		ListMode listConfig=ListMode.valueOf(settings.getString(Constants.Preferences.LIST_MODE_KEY, ListMode.LIST.toString()));
		if (listConfig==ListMode.LIST){
			radioList.setChecked(true);
			radioGrid.setChecked(false);
		} else {
			radioList.setChecked(false);
			radioGrid.setChecked(true);
		}
		
		ArrayAdapter<CharSequence> adapter;
		
		adapter = ArrayAdapter.createFromResource(this, R.array.widget_order, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOrderList.setAdapter(adapter);
        
        Constants.ListOrder orderOption= Constants.ListOrder.valueOf(settings.getString(Constants.Preferences.LIST_ORDER, Constants.ListOrder.ALPHABETIC.toString()));
        
        spinnerOrderList.setSelection(getListOrderIndex(orderOption));
		
		adapter= ArrayAdapter.createFromResource(this,  R.array.service_update, android.R.layout.simple_spinner_item );
		Assert.assertEquals(adapter.getCount(), listServiceUpdateValues.length);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		spinnerUpdateService.setAdapter(adapter);
		
		ListServiceUpdate option=ListServiceUpdate.valueOf(settings.getString(Constants.Preferences.UPDATE_SERVICE_KEY, ListServiceUpdate.TWO_DAYS.toString()));
		
		spinnerUpdateService.setSelection(getServiceUpdateIndex(option));
		
		updateOnStart.setChecked(settings.getBoolean(Constants.Preferences.UPDATE_DB_ON_START, false));
		
		
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
		editablePreferences.putString(Constants.Preferences.LIST_ORDER,  listOrderListValues[spinnerOrderList.getSelectedItemPosition()].toString());
		editablePreferences.putBoolean(Constants.Preferences.UPDATE_DB_ON_START, updateOnStart.isChecked());
		
		editablePreferences.commit();
		
		setResult(Activity.RESULT_OK);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		UltraSearchApp.getInstance().launchAutoUpdate(1);
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
	
	private int getListOrderIndex(Constants.ListOrder orderOption) {
		for (int i=0; i<listOrderListValues.length; i++){
			if (listOrderListValues[i]==orderOption){
				return i;
			}
		}
		Assert.assertTrue(false);
		return 0;
	}

}
//*/