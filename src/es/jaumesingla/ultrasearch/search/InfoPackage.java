package es.jaumesingla.ultrasearch.search;

import junit.framework.Assert;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

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