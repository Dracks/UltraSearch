package es.jaumesingla.ultrasearch.search;

import java.util.ArrayList;
import java.util.Collections;

import es.jaumesingla.ultrasearch.Constants;
import es.jaumesingla.ultrasearch.Constants.ListMode;
import es.jaumesingla.ultrasearch.R;
import es.jaumesingla.ultrasearch.UltraSearchApp;
import es.jaumesingla.ultrasearch.UltraSearchApp.DataBaseChanged;
import es.jaumesingla.ultrasearch.about.AboutActivity;
import es.jaumesingla.ultrasearch.model.InfoLaunchApplication;
import es.jaumesingla.ultrasearch.settings.SettingsActivity;
import es.jaumesingla.ultrasearch.threads.RefreshList;

import junit.framework.Assert;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
//import android.app.Activity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

@SuppressLint("NewApi")
public class MainActivity extends Activity implements DataBaseChanged{
	
	
	private final String TAG="MainActivity";

    //@SuppressLint("NewApi")
	
	private ResultsViewAdapter listAdapter;
	private ResultsViewAdapter gridAdapter;
	private ArrayList<InfoLaunchApplication> listPackages;
	private Object blockRefreshRequire;
	
	private ListView listItems;
	private GridView gridItems;
	
	private Handler handlerView;

	private boolean requireRefresh=false;
	private boolean refreshingOnProgress=false;
	private String filter="";
	
	private EditText searcher;
	
	private ListMode listMode;
	

	private LinearLayout searchBar;

	private TextView searchText;

	private int cellWidth;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
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
        
        listPackages=new ArrayList<InfoLaunchApplication>();
        blockRefreshRequire=new Object();
     
        
        handlerView=new Handler();
        
        listAdapter=new ResultsViewAdapter(inflater,  this, R.layout.cell_program_list);
        gridAdapter=new ResultsViewAdapter(inflater,  this, R.layout.cell_program_grid);
        
        
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
        //((ExpandableGridView) gridItems).setExpanded(true);
        gridItems.setAdapter(gridAdapter);
        
        View cellGrid = inflater.inflate(R.layout.cell_program_grid, null);
        cellWidth=measureCellWidth(this, cellGrid);
        gridItems.setColumnWidth(cellWidth);
        
        gridItems.setOnItemClickListener(launchItemClick);
        
        showListByConfig();
        
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
        searchBar= (LinearLayout) findViewById(R.id.llSearchBar);
        searchText= (TextView) findViewById(R.id.txtSearch);
        searchBar.setVisibility(View.GONE);
        searchBar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				launchMarket();
			}
		});
        
    }
	
	
	private void showListByConfig() {
		//Log.d(TAG, "showListByConfig: "+gridItems.getWidth()+ " vs "+ cellWidth);
		if (listMode==ListMode.GRID){
        	listItems.setVisibility(View.GONE);
        	gridItems.setVisibility(View.VISIBLE);
        	gridAdapter.notifyDataSetChanged();
        	//if (gridItems.getWidth()>0)
        	this.gridAdapter.setNumColums(-1);
        } else {
        	gridItems.setVisibility(View.GONE);
        	listItems.setVisibility(View.VISIBLE);
        	listAdapter.notifyDataSetChanged();
        }
	}


	@Override
	protected void onResume() {
		super.onResume();
		searcher.setSelection(0, filter.length());
		
		this.refreshSettings();
		
		showListByConfig();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		UltraSearchApp.getInstance().registerOnDataBaseChanged(this);
		this.onDataBaseChanged();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		UltraSearchApp.getInstance().unregisterOnDataBaseChanged(this);
	}
	
	public int measureCellWidth( Context context, View cell )
	{
	    // We need a fake parent
	    FrameLayout buffer = new FrameLayout( context );
	    android.widget.AbsListView.LayoutParams layoutParams = new  android.widget.AbsListView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    buffer.addView( cell, layoutParams);

	    cell.forceLayout();
	    cell.measure(1000, 1000);

	    int width = cell.getMeasuredWidth();

	    buffer.removeAllViews();

	    return width;
	}
	
	private void refreshSettings(){
		SharedPreferences preferences = UltraSearchApp.getInstance().getPreferences();
        listMode=ListMode.valueOf(preferences.getString(Constants.Preferences.LIST_MODE_KEY, ListMode.LIST.toString()));
	}
	
	protected void launchFirst(){
		if (listAdapter.getCount()>0)
			this.launchApp(listAdapter.getItem(0));
	}
	
	protected void launchApp(InfoLaunchApplication app) {
		//Intent mIntent = getPackageManager().getLaunchIntentForPackage(packageName.resolvePackageName);
		//if (mIntent != null) {
		UltraSearchApp.getInstance().getDataBase().getStatistics().launchApp(app);
		Intent mIntent=app.getIntentLaunch();
		try {
			startActivity(mIntent);
		} catch (ActivityNotFoundException err) {
			Toast t = Toast.makeText(getApplicationContext(),
					R.string.app_not_found, Toast.LENGTH_SHORT);
			t.show();
		}
	}
	
	protected void launchMarket(){
		Intent share=new Intent(Intent.ACTION_VIEW);
    	share.setData(Uri.parse("market://search?q="+filter));
    	startActivity(Intent.createChooser(share, "-- Search in market"));
	}
	
	

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Intent share;
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.menu_settings:
            	Intent settings=new Intent(this, SettingsActivity.class);
            	startActivity(settings);
            	return true;
            case R.id.menu_share:
            	share=new Intent(Intent.ACTION_SEND);
				share.setType("text/plain");
				share.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.shareTextTitle));
				share.putExtra(Intent.EXTRA_TEXT, String.format(getString(R.string.shareText), getString(R.string.app_name), getPackageName()));
				
				startActivity(Intent.createChooser(share, getString(R.string.shareTitle)));
            	return true;
            case R.id.menu_contact:
            	share=new Intent(Intent.ACTION_SENDTO);
            	share.setData(Uri.parse("mailto:mail@jaumesingla.es"));
            	share.putExtra(Intent.EXTRA_SUBJECT, "[UltraSearchApp]");
            	startActivity(share);
            	return true;
            case R.id.menu_about:
            	share=new Intent(this, AboutActivity.class);
            	startActivity(share);
            	return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    //*/

	/*public ResultsViewAdapter getListAdapter() {
		return listAdapter;
	}//*/

	public void setContentListAdapter(ArrayList<InfoLaunchApplication> data) {
		synchronized (this) {
			if (data.size()>0){
				searchBar.setVisibility(View.GONE);
			} else {
				if (this.listPackages.size()>0){
					searchBar.setVisibility(View.VISIBLE);
					searchText.setText(getResources().getString(R.string.search_in_market, filter));
				}
			}
			this.listAdapter.setData(data);
			this.gridAdapter.setData(data);
			
			if (listMode==ListMode.LIST)
				this.listAdapter.notifyDataSetChanged();
			else
				this.gridAdapter.notifyDataSetChanged();
			
			this.listAdapter.clear();
			this.gridAdapter.clear();
		}
	}

	public ArrayList<InfoLaunchApplication> getListPackages() {
		return listPackages;
	}

	public void setListPackages(ArrayList<InfoLaunchApplication> listPackages) {
		Log.d(TAG, "listPackages.size:"+listPackages.size());
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
		//Log.d(TAG, "setRequireRefresh:"+refreshingOnProgress+","+requireRefresh);
		synchronized(blockRefreshRequire){
			if (refreshingOnProgress){
				requireRefresh=true;
			} else {
				new Thread(new RefreshList(this)).start();
			}
		}
	}
	
	public void finishRefresh(){
		//Log.d(TAG, "finishRefresh:"+refreshingOnProgress+","+requireRefresh);
		synchronized (blockRefreshRequire) {
			refreshingOnProgress=false;
			if (requireRefresh){
				new Thread(new RefreshList(this)).start();
			}
		}
	}
	
	public void refreshOnProgress(){
		//Log.d(TAG, "refreshOnProgress:"+refreshingOnProgress+","+requireRefresh);
		synchronized(blockRefreshRequire){
			requireRefresh=false;
			refreshingOnProgress=true;
		}
	}

	public Handler getHandlerView() {
		return handlerView;
	}

	public String getFilter() {
		return filter;
	}

	@Override
	public void onDataBaseChanged() {
		(new AsyncTask<Integer, Integer, ArrayList<InfoLaunchApplication>>() {

			@Override
			protected ArrayList<InfoLaunchApplication> doInBackground(Integer... params) {
				return UltraSearchApp.getInstance().getDataBase().getApplications().getApplications();
			}
			
			@Override
			protected void onPostExecute(ArrayList<InfoLaunchApplication> result) {
				super.onPostExecute(result);
				//Log.d(TAG, "onPostExecute- size:"+result.size());
				MainActivity.this.setListPackages(result);
				MainActivity.this.setRequireRefresh();
			}
		}).execute();
		
	}


	public void setNumColumns() {
		Log.d(TAG, "setNumColumns"+gridItems.getWidth()+" / "+cellWidth);
		this.gridAdapter.setNumColums(gridItems.getWidth()/cellWidth);
	}
}
