package es.jaumesingla.ultrasearch.search;

import java.util.ArrayList;

import es.jaumesingla.ultrasearch.Constants;
import es.jaumesingla.ultrasearch.R;
import es.jaumesingla.ultrasearch.UltraSearchApp;
import es.jaumesingla.ultrasearch.Constants.Preferences;
import es.jaumesingla.ultrasearch.R.id;
import es.jaumesingla.ultrasearch.R.layout;
import es.jaumesingla.ultrasearch.R.menu;
import es.jaumesingla.ultrasearch.R.string;
import es.jaumesingla.ultrasearch.settings.SettingsActivity;
import es.jaumesingla.ultrasearch.threads.ChargeInfo;
import es.jaumesingla.ultrasearch.threads.RefreshList;

import junit.framework.Assert;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
//import android.app.Activity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

@SuppressLint("NewApi")
public class MainActivity extends Activity {
	
	
	private final String TAG="MainActivity";
	
	public enum ListMode{LIST, GRID};

    //@SuppressLint("NewApi")
	
	private ResultsViewAdapter listAdapter;
	private ResultsViewAdapter gridAdapter;
	private ArrayList<InfoPackage> listPackages;
	private Object blockRefreshRequire;
	
	private ListView listItems;
	private GridView gridItems;
	
	private Handler handlerView;

	private boolean requireRefresh=false;
	private boolean refreshingOnProgress=false;
	private String filter="";
	
	private EditText searcher;
	
	private ListMode listMode;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewGroup actionBar=(ViewGroup) findViewById(R.id.llActionBar);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
        	ViewGroup parent= (ViewGroup)actionBar.getParent();
        	parent.removeView(actionBar);
        	Assert.assertNotNull(getActionBar());
        	getActionBar().setCustomView(actionBar);
        	getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        } //else {*/
        	
       // }
        actionBar.setVisibility(View.VISIBLE);
        
        this.refreshSettings();
        
        listPackages=new ArrayList<InfoPackage>();
        blockRefreshRequire=new Object();
     
        
        handlerView=new Handler();
        
        listAdapter=new ResultsViewAdapter((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE),  this, R.layout.cell_value);
        gridAdapter=new ResultsViewAdapter((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE),  this, R.layout.cell_grid);
        
        OnItemClickListener launchItemClick = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int item, long group) {
				Log.d(TAG, "OnItemClickListener");
				MainActivity.this.launchApp(listAdapter.getItem(item));
			}
		};
        
        listItems=(ListView)findViewById(R.id.resultSearchList);
        listItems.setAdapter(listAdapter);
        //listItems.setItemsCanFocus(false);
        
        listItems.setOnItemClickListener(launchItemClick);
        
        gridItems=(GridView) findViewById(R.id.resultSearchGrid);
        gridItems.setAdapter(gridAdapter);
        
        gridItems.setOnItemClickListener(launchItemClick);
        
        if (listMode==ListMode.GRID){
        	listItems.setVisibility(View.GONE);
        	gridItems.setVisibility(View.VISIBLE);
        } else {
        	gridItems.setVisibility(View.GONE);
        	listItems.setVisibility(View.VISIBLE);
        }//*/
        
        EditText et=(EditText) findViewById(R.id.inputText);
        //et.setSelectAllOnFocus(true);
        searcher=et;
        et.setMaxLines(1);
        et.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				Log.i(TAG, "action:"+actionId);
				//Assert.assertNotNull(event);
				
				if (EditorInfo.IME_ACTION_GO==actionId) { 
					      MainActivity.this.launchFirst();//match this behavior to your 'Send' (or Confirm) button
					      return true;
				}
				return false;
			}
		});
        et.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				Log.d(TAG, s.toString());
				synchronized (MainActivity.this.blockRefreshRequire) {
					MainActivity.this.filter=s.toString();
					MainActivity.this.setRequireRefresh();
				}
			}
		});
        
        ImageButton shareButton=(ImageButton) findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent share=new Intent(Intent.ACTION_SEND);
				share.setType("text/plain");
				share.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.shareTextTitle));
				share.putExtra(Intent.EXTRA_TEXT, String.format(getString(R.string.shareText), getString(R.string.app_name), getPackageName()));
				
				startActivity(Intent.createChooser(share, getString(R.string.shareTitle)));
			}
		});
        
    }
	
	@Override
	protected void onStart() {
		super.onStart();
		if (this.listPackages.size()==0){
			new Thread(new ChargeInfo(this)).start();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		searcher.setSelection(0, filter.length());
		
		this.refreshSettings();
		
		if (listMode==ListMode.GRID){
        	listItems.setVisibility(View.GONE);
        	gridItems.setVisibility(View.VISIBLE);
        } else {
        	gridItems.setVisibility(View.GONE);
        	listItems.setVisibility(View.VISIBLE);
        }
	}
	
	private void refreshSettings(){
		SharedPreferences preferences = UltraSearchApp.getInstance().getPreferences();
        listMode=ListMode.valueOf(preferences.getString(Constants.Preferences.LIST_MODE_KEY, ListMode.LIST.toString()));
	}
	
	protected void launchFirst(){
		if (listAdapter.getCount()>0)
			this.launchApp(listAdapter.getItem(0));
	}
	
	protected void launchApp(ResolveInfo packageName) {
		//Intent mIntent = getPackageManager().getLaunchIntentForPackage(packageName.resolvePackageName);
		//if (mIntent != null) {
		Intent mIntent=new Intent();
		ComponentName name = new ComponentName(packageName.activityInfo.packageName, packageName.activityInfo.name);
		mIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mIntent.setComponent(name);
		try {
			startActivity(mIntent);
		} catch (ActivityNotFoundException err) {
			Toast t = Toast.makeText(getApplicationContext(),
					R.string.app_not_found, Toast.LENGTH_SHORT);
			t.show();
		}
		//}
	}
	
	

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.menu_settings:
            	Intent settings=new Intent(this, SettingsActivity.class);
            	startActivity(settings);
        }
        return super.onOptionsItemSelected(item);
    }//*/

	/*public ResultsViewAdapter getListAdapter() {
		return listAdapter;
	}//*/

	public void setContentListAdapter(ArrayList<ResolveInfo> data) {
		synchronized (this) {
			this.listAdapter.setData(data);
			this.gridAdapter.setData(data);
		}
	}

	public ArrayList<InfoPackage> getListPackages() {
		return listPackages;
	}

	public void setListPackages(ArrayList<InfoPackage> listPackages) {
		this.listPackages = listPackages;
		this.setRequireRefresh();
	}
	
	public Object getBlockRefreshRequire() {
		return blockRefreshRequire;
	}

	public boolean isRequireRefresh() {
		return requireRefresh;
	}
	
	public void setRequireRefresh(){
		synchronized(blockRefreshRequire){
			if (refreshingOnProgress){
				requireRefresh=true;
			} else {
				new Thread(new RefreshList(this)).start();
			}
		}
	}
	
	public void finishRefresh(){
		synchronized (blockRefreshRequire) {
			refreshingOnProgress=false;
			if (requireRefresh){
				new Thread(new RefreshList(this)).start();
			}
		}
	}
	
	public void refreshOnProgress(){
		synchronized(blockRefreshRequire){
			requireRefresh=false;
			refreshingOnProgress=true;
		}
	}

	public Handler getHandlerView() {
		return handlerView;
	}

	public String getFilter() {
		// TODO Auto-generated method stub
		return filter;
	}
}
