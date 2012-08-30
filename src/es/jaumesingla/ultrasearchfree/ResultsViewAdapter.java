package es.jaumesingla.ultrasearchfree;

import java.util.ArrayList;

import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ResultsViewAdapter extends BaseAdapter {
	
	private static class ViewHolder {
		public TextView textView;
		public ImageView imageView;
	}

	private ArrayList<ResolveInfo> listContents;
	private LayoutInflater mInflater;
	private PackageManager mPm;
	
	public ResultsViewAdapter(LayoutInflater inf, PackageManager pm){
		super();
		mInflater=inf;
		listContents=new ArrayList<ResolveInfo>();
		mPm=pm;
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
	public View getView(int position, View convertView, ViewGroup parent) {
		System.out.println("getView " + position + " " + convertView);
		Log.d("MyCustomAdapter-getView", "getView " + position + " " + convertView);
		ViewHolder holder = null;
		if (convertView == null) {
			Log.d("MyCustomAdapter-getView", "Layout: " + R.layout.cell_value);
			convertView = mInflater.inflate(R.layout.cell_value, null);
			assert(convertView!=null);
			holder = new ViewHolder();
			holder.textView = (TextView)convertView.findViewById(R.id.txtName);
			holder.imageView = (ImageView)convertView.findViewById(R.id.imgIcon);
			//holder.labelPos = (TextView)convertView.findViewById(R.id.labelText);
			assert(holder.textView!=null);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		ResolveInfo toShow=this.getItem(position);
		holder.textView.setText(toShow.loadLabel(mPm));
		holder.imageView.setImageDrawable(toShow.loadIcon(mPm));
		
		return convertView;

		//String posText="   "+Integer.toString(rCount-position);
		//holder.labelPos.setText(":"+posText.subSequence(posText.length()-3, posText.length()));
	}
	
	public void clear(){
		listContents.clear();
	}

}
