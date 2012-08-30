package es.jaumesingla.ultrasearchfree.share;

import java.util.ArrayList;
import java.util.List;

import es.jaumesingla.ultrasearchfree.R;

import android.net.Uri;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources.NotFoundException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ShareLimitedActivity extends Activity {
	
	public final static String KEY_APPLICATION_NOT_FOUND="";
	
	public class ShareSelectorAdapter extends ArrayAdapter<ActivityInfo>{
		
		private ArrayList<ActivityInfo> listContents=new ArrayList<ActivityInfo>();
		private PackageManager pm;
		
		public ShareSelectorAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
			pm=getPackageManager();
		}
		
		private class ViewHolder {
			public ImageView icon;
			public TextView name;
		}
		
		@Override
		public int getCount() {
			return listContents.size();
		}

		@Override
		public ActivityInfo getItem(int position) {
			return listContents.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
	        	convertView = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.cell_value, null);
	        	holder = new ViewHolder();
	        	holder.icon=(ImageView) convertView.findViewById(R.id.imgIcon);
				holder.name=(TextView) convertView.findViewById(R.id.txtName);
	        	convertView.setTag(holder);
	        }
	        else{
	        	// Get the ViewHolder back to get fast access to the TextView and the ImageView.
	            holder = (ViewHolder) convertView.getTag();
	        }
			
			ActivityInfo show=getItem(position);
			try{
				
				//ApplicationInfo ai=pm.getApplicationInfo(show.packageName, 0);
				ApplicationInfo ai=show.applicationInfo;
				holder.icon.setImageDrawable(pm.getApplicationIcon(ai));
				holder.name.setText(pm.getApplicationLabel(ai));
			} catch (Exception e){
				e.printStackTrace();
			}
			
			//Log.d(TAG, "Merda Merda Merda:" + cell.getHeight());
			
			return convertView;
		}
		
		public void addApplication(ActivityInfo e){
			listContents.add(e);
			
		}
		
	}
	
    private static final String TAG = "ShareLimitedActivity";

	public static final String FILTER_NAME = "FILTER_NAME";
	public static final String TITLE_WINDOW = "TITLE_WINDOW";
    
    private ShareSelectorAdapter adapter;
    private Intent share;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.share_selector);
        /*
        this.setResult(RESULT_OK);
        this.finish();
        //*/
        adapter=new ShareSelectorAdapter(this, R.layout.share_selector);
        ListView listView=((ListView) findViewById(R.id.LVApps));
        listView.setAdapter(adapter);
        
        share= new Intent(Intent.ACTION_SEND);
		share.setType(getIntent().getType());
		Bundle extras=getIntent().getExtras();
		share.putExtra(Intent.EXTRA_SUBJECT, extras.getString(Intent.EXTRA_SUBJECT));
		share.putExtra(Intent.EXTRA_TEXT, extras.getString(Intent.EXTRA_TEXT));
		//share.putExtra(Intent.EXTRA_TITLE, extras.getString(Intent.EXTRA_TITLE));
		share.putExtra(Intent.EXTRA_STREAM, (Uri) extras.get(Intent.EXTRA_STREAM));
		//share.setData(getIntent().getData());
		//LogApp.i(TAG, extras.get(Intent.EXTRA_STREAM).toString());
		
		this.setTitle(extras.getString(ShareLimitedActivity.TITLE_WINDOW));
		
		PackageManager pm = getPackageManager();
		List<ResolveInfo> activityList = pm.queryIntentActivities(share, 0);
		String methodName=extras.getString(FILTER_NAME);
		String[] options=methodName.split("\\|");
		for (int i=0; i<options.length; i++)
			Log.d(TAG, options[i]);
		for (final ResolveInfo app : activityList) {
			boolean ok=false;
			for (int i=0; i<options.length && !ok; i++){
				if (app.activityInfo.name.contains(options[i]))
					ok=true;
			}
			if (ok){
				adapter.addApplication(app.activityInfo);
			}
		}
		
		adapter.notifyDataSetChanged();
		
		if (adapter.getCount()==0){
			String errorText=extras.getString(ShareLimitedActivity.KEY_APPLICATION_NOT_FOUND);
			if (errorText!=null && errorText.length()>0){
				Toast t=Toast.makeText(this, errorText, Toast.LENGTH_LONG);
				t.show();
			} else {
				Log.w(TAG, "KEY_APPLICATION_NOT_FOUND is nedded");
			}
			this.finish();
		} else if (adapter.getCount()==1){
			onClickItem(0);
		}
	
		listView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				onClickItem(arg2);
			}
			
		});
        
    }
	
	public void onClickItem(int item){
		ActivityInfo activity=adapter.getItem(item);
		final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
		share.addCategory(Intent.CATEGORY_LAUNCHER);
		share.setComponent(name);
		startActivityForResult(share,3);
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		Log.d(TAG, "Data:"+requestCode+" data2:"+resultCode+" Intent:"+data);
		
		if (requestCode==3){
			this.setResult(resultCode, getIntent());
			finish();
		}
	}

}
