package es.jaumesingla.ultrasearch.model;

import junit.framework.Assert;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

public class InfoLaunchApplication{
	private String name;
	private String description;
	private String activity;
	//private String[] splittedDescription;
	private int iconId;
	private String packageName;
	
	/*public InfoLaunchApplication(ResolveInfo ai, PackageManager pm){
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
	}*/
	
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
	}
	
	public boolean contains(String textOriginal){
		String text=textOriginal.toLowerCase();
		return name.toLowerCase().contains(text) || description.toLowerCase().contains(text) || packageName.contains(text);
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
}