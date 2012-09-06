package es.jaumesingla.ultrasearch.model;

public class ResultLaunchApplication implements Comparable<ResultLaunchApplication>{
	private InfoLaunchApplication appInfo;
	private float concordance;
	
	public ResultLaunchApplication(InfoLaunchApplication appInfo, float concordance){
		this.appInfo=appInfo;
		this.concordance=concordance;
	}
	
	public InfoLaunchApplication getData(){
		return appInfo;
	}
	
	public float getConcordance(){
		return concordance;
	}

	@Override
	public int compareTo(ResultLaunchApplication other) {
		float comparation=this.concordance-other.concordance;
		if (comparation<0){
			return -1;
		} else{
			return 1;
		}
	}
}
