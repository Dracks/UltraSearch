package es.jaumesingla.ultrasearch.search;

import java.util.ArrayList;

import es.jaumesingla.ultrasearch.R;
import es.jaumesingla.ultrasearch.model.InfoLaunchApplication;
import es.jaumesingla.ultrasearch.search.viewlisteners.ShowOptions;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ResultsViewAdapter extends BaseAdapter {
	
	public static class ViewHolder {
		public TextView textView;
		public ImageView imageView;
		public ImageButton showInfoButton;
		public LinearLayout optionsView;
		
		public ImageButton shareButton;
		public ImageButton infoButton;
		public ImageButton closeButton;
		//public Spinner moreView;
	}

	protected static final String TAG = "ResultsViewAdapter";

	private ArrayList<InfoLaunchApplication> listContents;
	private LayoutInflater mInflater;
	private Context mContext;
	private int layoutId;
	
	public ResultsViewAdapter(LayoutInflater inf, Context c, int layoutId){
		super();
		mInflater=inf;
		listContents=new ArrayList<InfoLaunchApplication>();
		mContext=c;
		this.layoutId=layoutId;
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

		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(layoutId, null);
			assert(convertView!=null);
			holder = new ViewHolder();
			holder.textView = (TextView)convertView.findViewById(R.id.txtName);
			holder.imageView = (ImageView)convertView.findViewById(R.id.imgIcon);
			holder.showInfoButton = (ImageButton) convertView.findViewById(R.id.imbInfo);
			holder.optionsView = (LinearLayout) convertView.findViewById(R.id.llOptions);
			
			holder.shareButton=(ImageButton) convertView.findViewById(R.id.imbShare);
			holder.infoButton=(ImageButton) convertView.findViewById(R.id.imbInfo);
			holder.closeButton=(ImageButton) convertView.findViewById(R.id.imbClose);
			
			//holder.labelPos = (TextView)convertView.findViewById(R.id.labelText);
			convertView.setTag(holder);
			
			
			holder.showInfoButton.setFocusable(false);
			holder.showInfoButton.setFocusableInTouchMode(false);
			
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
		
		holder.optionsView.setVisibility(View.GONE);
		
		holder.showInfoButton.setOnClickListener(new ShowOptions(holder));
		//holder.closeButton.setOnClickListener(new HiddenOptions(holder));
		
		return convertView;
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
