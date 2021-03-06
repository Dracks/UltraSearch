package es.jaumesingla.ultrasearch;

import java.util.ArrayList;
import java.util.List;

import es.jaumesingla.ultrasearch.threads.ChargeInfo;
import es.jaumesingla.ultrasearch.threads.RefreshList;

import junit.framework.Assert;

import android.os.Bundle;
import android.os.Handler;
//import android.app.Activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import android.support.v4.app.NavUtils;

public class MainActivity extends Activity {
	
	
	private final String TAG="MainActivity";
	
	public class InfoPackage{
		private ResolveInfo data;
		private String name;
		private String description;
		private String packageName;
		
		public InfoPackage(ResolveInfo ai, PackageManager pm){
			data=ai;
			name=ai.loadLabel(pm).toString();
			CharSequence d=ai.activityInfo.applicationInfo.loadDescription(pm);
			if (d!=null){
				description=d.toString();
			} else {
				description="";
			}
			packageName=ai.activityInfo.packageName;
			Assert.assertNotNull(name);
			Assert.assertNotNull(packageName);
			Assert.assertNotNull(description);
		}
		
		public boolean contains(String textOriginal){
			String text=textOriginal.toLowerCase();
			return name.toLowerCase().contains(text) || description.toLowerCase().contains(text) || packageName.contains(text);
		}

		public ResolveInfo getData() {
			return data;
		}

		public String getName() {
			return name;
		}

		public String getDescription() {
			return description;
		}

		public String getPackageName() {
			return packageName;
		}
	}

    //@SuppressLint("NewApi")
	
	private ResultsViewAdapter listAdapter;
	private ArrayList<InfoPackage> listPackages;
	private Object blockRefreshRequire;
	private ListView listItems;
	
	private Handler handlerView;

	private boolean requireRefresh=false;
	private boolean refreshingOnProgress=false;
	private String filter="";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //if (Build.VERSION.SDK_INT<Build.VERSION_CODES.HONEYCOMB){
        	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //}
        setContentView(R.layout.activity_main);
        ViewGroup actionBar=(ViewGroup) findViewById(R.id.llActionBar);
        /*if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
        	ViewGroup parent= (ViewGroup)actionBar.getParent();
        	parent.removeView(actionBar);
        	Assert.assertNotNull(getActionBar());
        	getActionBar().setCustomView(actionBar);
        	getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        } //else {*/
        	
       // }
        actionBar.setVisibility(View.VISIBLE);
        
        listPackages=new ArrayList<InfoPackage>();
        blockRefreshRequire=new Object();
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        //getActionBar().
     
        
        handlerView=new Handler();
        
        listAdapter=new ResultsViewAdapter((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE), getPackageManager());
		
		Log.i(TAG, "Old");
		
		new Thread(new ChargeInfo(this)).start();
        
        listItems=(ListView)findViewById(R.id.resultSearch);
        listItems.setAdapter(listAdapter);
        
        
        listItems.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int item,
					long group) {
				MainActivity.this.launchApp(listAdapter.getItem(item));
				
			}
		});
        
        EditText et=(EditText) findViewById(R.id.inputText);
        et.setMaxLines(1);
        et.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode()==KeyEvent.KEYCODE_ENTER) { 
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


    /*@Override
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
        }
        return super.onOptionsItemSelected(item);
    }*/

	public ResultsViewAdapter getListAdapter() {
		return listAdapter;
	}

	public void setListAdapter(ResultsViewAdapter listAdapter) {
		synchronized (this) {
			this.listAdapter = listAdapter;
			listItems.setAdapter(listAdapter);
		}
		listAdapter.notifyDataSetChanged();
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
