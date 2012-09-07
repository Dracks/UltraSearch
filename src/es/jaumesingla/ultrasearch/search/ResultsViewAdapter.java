package es.jaumesingla.ultrasearch.search;

import java.util.ArrayList;

import es.jaumesingla.ultrasearch.R;
import es.jaumesingla.ultrasearch.model.InfoLaunchApplication;


import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class ResultsViewAdapter extends BaseAdapter {
	
	private static class ViewHolder {
		public TextView textView;
		public ImageView imageView;
		//public Button infoView;
		public Spinner moreView;
	}

	protected static final String TAG = "ResultsViewAdapter";

	private ArrayList<InfoLaunchApplication> listContents;
	private LayoutInflater mInflater;
	private Context mContext;
	private int layoutId;

	private SpinnerAdapter spinerAdapter;
	
	public ResultsViewAdapter(LayoutInflater inf, Context c, int layoutId){
		super();
		mInflater=inf;
		listContents=new ArrayList<InfoLaunchApplication>();
		mContext=c;
		this.layoutId=layoutId;
		
		spinerAdapter=new BaseAdapter() {
			private String[] data=mContext.getResources().getStringArray(R.array.app_options);
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				//Log.d(TAG, "getView");
				ImageView v=new ImageView(mContext);
				v.setImageResource(android.R.drawable.ic_menu_info_details);
				return v;
			}
			
			@Override
			public long getItemId(int position) {
				return position;
			}
			
			@Override
			public String getItem(int position) {
				return data[position];
			}
			
			@Override
			public int getCount() {
				return data.length;
			}
			
			@Override
			public View getDropDownView(int position, View convertView,ViewGroup parent) {
				if (convertView==null){
					convertView=mInflater.inflate(android.R.layout.simple_spinner_dropdown_item, null);
				}
				((CheckedTextView) convertView).setText(data[position]);
				return convertView;
				//return super.getDropDownView(position, convertView, parent);
			}
		};//*/ ArrayAdapter.createFromResource(mContext,  R.array.app_options, android.R.layout.simple_spinner_item );
	}
	
	public void add(InfoLaunchApplication e){
		listContents.add(e);
		notifyDataSetChanged();
	}
	
	public void setData(ArrayList<InfoLaunchApplication> data){
		listContents=data;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return listContents.size();
	}

	@Override
	public InfoLaunchApplication getItem(int position) {
		return listContents.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		//System.out.println("getView " + position + " " + convertView);
		//Log.d("MyCustomAdapter-getView", "getView " + position + " " + convertView);
		ViewHolder holder = null;
		if (convertView == null) {
			//Log.d("MyCustomAdapter-getView", "Layout: " + layoutId);
			convertView = mInflater.inflate(layoutId, null);
			assert(convertView!=null);
			holder = new ViewHolder();
			holder.textView = (TextView)convertView.findViewById(R.id.txtName);
			holder.imageView = (ImageView)convertView.findViewById(R.id.imgIcon);
			holder.moreView = (Spinner) convertView.findViewById(R.id.spOptions);
			//holder.infoView = (Button) convertView.findViewById(R.id.btInfo);
			//holder.labelPos = (TextView)convertView.findViewById(R.id.labelText);
			convertView.setTag(holder);
			//holder.infoView.setFocusable(false);
			//holder.infoView.setFocusableInTouchMode(false);
			
			holder.moreView.setAdapter(spinerAdapter);
			holder.moreView.setFocusable(false);
			holder.moreView.setFocusableInTouchMode(false);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		InfoLaunchApplication toShow=this.getItem(position);
		PackageManager pm=mContext.getPackageManager();
		Drawable icon=mContext.getResources().getDrawable(R.drawable.ic_launcher);
		try{
			icon=pm.getResourcesForApplication(toShow.getPackageName()).getDrawable(toShow.getIcon());
		} catch (Exception e){
			e.printStackTrace();
			this.listContents.remove(position);
			this.notifyDataSetChanged();
		}
		holder.textView.setText(toShow.getName());
		holder.imageView.setImageDrawable(icon);
		
		//ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext,  R.array.app_options, android.R.layout.simple_spinner_item );
		// Specify the layout to use when the list of choices appears
		//adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);//*/
		
		
		
		holder.moreView.setOnItemSelectedListener(new OnItemSelectedListener() {
			
			int element=position;
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Log.d("SpinnerItemSelected", "click:"+arg0+","+arg1+","+arg2+","+arg3);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		/*holder.infoView.setOnClickListener(new OnClickListener() {
			int e=position;
			@Override
			public void onClick(View v) {
				ResultsViewAdapter.this.viewInfo(e);
			}
		});*/
		
		/*convertView.setOnClickListener(new OnClickListener() {
			int e=position;
			@Override
			public void onClick(View v) {
				Log.d(TAG, "onClickLister");
			}
		});*/
		
		return convertView;

		//String posText="   "+Integer.toString(rCount-position);
		//holder.labelPos.setText(":"+posText.subSequence(posText.length()-3, posText.length()));
	}
	
	public void clear(){
		listContents.clear();
	}
	
	public void viewInfo(int element){
		String mCurrentPkgName=this.getItem(element).getPackageName();
		 Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,Uri.fromParts("package", mCurrentPkgName, null));
		 // start new activity to display extended information
		 mContext.startActivity(intent);
	}

}
