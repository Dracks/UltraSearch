package es.jaumesingla.ultrasearch.search;

import java.util.ArrayList;

import es.jaumesingla.ultrasearch.R;
import es.jaumesingla.ultrasearch.model.InfoLaunchApplication;
import es.jaumesingla.ultrasearch.search.viewlisteners.InfoApplication;
import es.jaumesingla.ultrasearch.search.viewlisteners.ShareApplication;
import es.jaumesingla.ultrasearch.search.viewlisteners.ShowOptionsPosition;


import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
		//public ImageButton closeButton;
		//public Spinner moreView;
	}

	protected static final String TAG = "ResultsViewAdapter";

	private ArrayList<InfoLaunchApplication> listContents;
	private LayoutInflater mInflater;
	private Context mContext;
	private int layoutId;
	private int selected;
	private int endSelected;
	private int numColums;
	
	public ResultsViewAdapter(LayoutInflater inf, Context c, int layoutId){
		super();
		mInflater=inf;
		selected=-1;
		endSelected=-1;
		listContents=new ArrayList<InfoLaunchApplication>();
		mContext=c;
		this.layoutId=layoutId;
		this.numColums=1;
	}
	
	/*public void add(InfoLaunchApplication e){
		listContents.add(e);
		notifyDataSetChanged();
	}//*/
	
	public void setData(ArrayList<InfoLaunchApplication> data){
		listContents=data;
		//notifyDataSetChanged();
	}
	
	public void setNumColums(int c){
		//Assert.assertTrue(c>0);
		this.numColums=c;
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
		if (this.numColums<0){
			((MainActivity) mContext).setNumColumns();
		}
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(layoutId, null);
			assert(convertView!=null);
			holder = new ViewHolder();
			holder.textView = (TextView)convertView.findViewById(R.id.txtName);
			holder.imageView = (ImageView)convertView.findViewById(R.id.imgIcon);
			holder.showInfoButton = (ImageButton) convertView.findViewById(R.id.imbShowInfo);
			holder.optionsView = (LinearLayout) convertView.findViewById(R.id.llOptions);
			
			holder.shareButton=(ImageButton) convertView.findViewById(R.id.imbShare);
			holder.infoButton=(ImageButton) convertView.findViewById(R.id.imbInfo);
			//holder.closeButton=(ImageButton) convertView.findViewById(R.id.imbClose);
			
			//holder.labelPos = (TextView)convertView.findViewById(R.id.labelText);
			convertView.setTag(holder);
			
			
			holder.showInfoButton.setFocusable(false);
			holder.showInfoButton.setFocusableInTouchMode(false);
			
			holder.shareButton.setFocusable(false);
			holder.shareButton.setFocusableInTouchMode(false);
			
			holder.infoButton.setFocusable(false);
			holder.infoButton.setFocusableInTouchMode(false);
			
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
		
		holder.showInfoButton.setOnClickListener(new ShowOptionsPosition(this, position));
		
		if (selected>position || endSelected<=position){
			holder.optionsView.setVisibility(View.GONE);
		} else if (position>selected && position<endSelected){
			holder.optionsView.setVisibility(View.INVISIBLE);
		} else {
			holder.optionsView.setVisibility(View.VISIBLE);

			holder.shareButton.setOnClickListener(new ShareApplication(mContext, toShow));
			holder.infoButton.setOnClickListener(new InfoApplication(mContext, toShow));
		}
		
		return convertView;
	}
	
	public void clear(){
		//this.listContents.clear();
		this.selected=-1;
		this.endSelected=-1;
	}
	
	public void setSelectedItem(int i){
		Log.d(TAG, "setSelectedItem:"+i+" / "+this.selected+ " / "+this.numColums);
		if (this.numColums==0){
			((MainActivity) mContext).setNumColumns();
		}
		if (this.selected!=i){
			this.selected=i;
			this.endSelected=((this.selected/this.numColums)+1)*this.numColums;
		} else {
			this.selected=-1;
			this.endSelected=-1;
		}
		Log.d(TAG, "endSelected:"+this.endSelected);
	}

}
