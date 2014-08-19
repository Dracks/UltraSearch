package es.jaumesingla.ultrasearch.widgets.activities;

import android.widget.*;
import es.jaumesingla.ultrasearch.Constants;
import es.jaumesingla.ultrasearch.R;
import es.jaumesingla.ultrasearch.UltraSearchApp;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import es.jaumesingla.ultrasearch.Constants.ListOrder;
import es.jaumesingla.ultrasearch.widgets.ListWidgetProvider;

public class ListWidgetSettings extends Activity {

	private static final String TAG = "ListWidgetSettings";
    private Spinner listOptionsSpinner;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		
		setContentView(R.layout.widget_config);

        listOptionsSpinner=((Spinner) findViewById(R.id.spOrderList));
        
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.widget_order, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        listOptionsSpinner.setAdapter(adapter);
        
        listOptionsSpinner.setSelection(0);

        ((Button) findViewById(R.id.save)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int widgetId = getIntent().getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);

                ListOrder optionSelected=ListOrder.values()[listOptionsSpinner.getSelectedItemPosition()];
				UltraSearchApp app = UltraSearchApp.getInstance();
                app.setListWidgetConfiguration(widgetId, optionSelected);
                app.setListWidget(widgetId, 3, 1);

                ListWidgetProvider.updateWidget(getBaseContext(), AppWidgetManager.getInstance(getBaseContext()), widgetId);

                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
                setResult(RESULT_OK, resultValue);
                finish();
            }
        });


	}
}
