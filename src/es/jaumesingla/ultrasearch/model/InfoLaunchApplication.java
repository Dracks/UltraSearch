package es.jaumesingla.ultrasearch.model;

import java.io.Serializable;
import java.util.Comparator;

import android.content.ComponentName;
import android.content.Intent;

import junit.framework.Assert;

public class InfoLaunchApplication implements Serializable {
	private String name;
	private String description;
	private String activity;
	//private String[] splittedDescription;
	private int iconId;
	private String packageName;
	private long lastLaunch;
	private int launchCount;
	
	public static class ComparatorByName implements Comparator<InfoLaunchApplication>{
		@Override
		public int compare(InfoLaunchApplication lhs, InfoLaunchApplication rhs) {
			// TODO Auto-generated method stub
			return lhs.getName().compareTo(rhs.getName());
		}
	}
	
	public static Comparator<InfoLaunchApplication> getSortByName(){
		return new ComparatorByName();
	}
	
	public static Comparator<InfoLaunchApplication> getSortByLaunch(){
		return new Comparator<InfoLaunchApplication>() {

			@Override
			public int compare(InfoLaunchApplication self, InfoLaunchApplication another) {
				return (int) (another.lastLaunch-self.lastLaunch);
			}
		};
	}
	
	public static Comparator<InfoLaunchApplication> getSortByLaunchCount(){
		return new ComparatorByName() {

			@Override
			public int compare(InfoLaunchApplication self, InfoLaunchApplication another) {
				int r = another.launchCount-self.launchCount;
				if (r==0){
					return super.compare(self, another);
				}
				return r;
			}
		};
	}
	
	public InfoLaunchApplication(String name, int icon, String activity, String packageName, String description){
		Assert.assertNotNull(name);
		Assert.assertNotNull(packageName);
		Assert.assertNotNull(description);
		Assert.assertNotNull(activity);
		this.name=name;
		this.iconId=icon;
		this.activity=activity;
		this.packageName=packageName;
		this.description=description;
		this.lastLaunch=0;
		this.launchCount=0;
	}
	
	public boolean contains(String textOriginal){
		String text=textOriginal.toLowerCase();
		return name.toLowerCase().contains(text) || description.toLowerCase().contains(text) || packageName.contains(text);
	}
	
	public void setLaunchInfo(long lastLaunch, int launchCount){
		this.lastLaunch=lastLaunch;
		this.launchCount=launchCount;
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

	public String getActivity() {
		return activity;
	}
	
	public int getIcon(){
		return this.iconId;
	}
	
	@Override
	public String toString() {
		return "InfoLaunchApplication{"+this.name+","+this.activity+","+this.packageName+"}";
	}
	
	public long getLastLaunch(){
		return this.lastLaunch;
	}
	
	public int getLaunchCount(){
		return this.launchCount;
	}
	
	public Intent getIntentLaunch(){
		Intent mIntent=new Intent();
		ComponentName name = new ComponentName(this.packageName, this.activity);
		mIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
		mIntent.setComponent(name);
		return mIntent;
	}
/*
	@Override
	public int compareTo(InfoLaunchApplication another) {
		//return another.launchCount-this.launchCount;
		return (int) (another.lastLaunch-this.lastLaunch);
	}//*/
}