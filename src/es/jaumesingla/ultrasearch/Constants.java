package es.jaumesingla.ultrasearch;

public class Constants {
	public enum ListMode{LIST, GRID};
	public enum ListServiceUpdate{ DAY, TWO_DAYS, WEEKLY, MONTHLY, NEVER }
	public static class Preferences{
		public static final String 	LIST_MODE_KEY="list_mode";
		public static final String 	UPDATE_SERVICE_KEY="update_service";
		public static final ListServiceUpdate 
									UPDATE_SERVICE_DEFAULT=ListServiceUpdate.TWO_DAYS;
		public static final String 	VERSION_KEY="version";
		public static final int 	VERSION=1;
	}

	public static class Database {

		public static final String 	NAME = "documentationIndexed.db";
		public static final int 	VERSION = 1;
		
	}
}
