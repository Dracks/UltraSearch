package es.jaumesingla.ultrasearch.search;

import java.util.ArrayList;

import es.jaumesingla.ultrasearch.R;


import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ResultsViewAdapter extends BaseAdapter {
	
	private static class ViewHolder {
		public TextView textView;
		public ImageView imageView;
		public Button infoView;
	}

	protected static final String TAG = "ResultsViewAdapter";

	private ArrayList<ResolveInfo> listContents;
	private LayoutInflater mInflater;
	private Context mContext;
	private int layoutId;
	
	public ResultsViewAdapter(LayoutInflater inf, Context c, int layoutId){
		super();
		mInflater=inf;
		listContents=new ArrayList<ResolveInfo>();
		mContext=c;
		this.layoutId=layoutId;
	}
	
	public void add(ResolveInfo e){
		listContents.add(e);
		notifyDataSetChanged();
	}
	
	public void setData(ArrayList<ResolveInfo> data){
		listContents=data;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return listContents.size();
	}

	@Override
	public ResolveInfo getItem(int position) {
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
			holder.infoView = (Button) convertView.findViewById(R.id.btInfo);
			//holder.labelPos = (TextView)convertView.findViewById(R.id.labelText);
			assert(holder.textView!=null);
			convertView.setTag(holder);
			holder.infoView.setFocusable(false);
			holder.infoView.setFocusableInTouchMode(false);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		ResolveInfo toShow=this.getItem(position);
		PackageManager pm=mContext.getPackageManager();
		holder.textView.setText(toShow.loadLabel(pm));
		holder.imageView.setImageDrawable(toShow.loadIcon(pm));
		holder.infoView.setOnClickListener(new OnClickListener() {
			int e=position;
			@Override
			public void onClick(View v) {
				ResultsViewAdapter.this.viewInfo(e);
			}
		});
		
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
		String mCurrentPkgName=this.getItem(element).activityInfo.packageName;
		 Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,Uri.fromParts("package", mCurrentPkgName, null));
		 // start new activity to display extended information
		 mContext.startActivity(intent);
	}

}