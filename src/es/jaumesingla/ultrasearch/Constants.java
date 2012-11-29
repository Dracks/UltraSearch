package es.jaumesingla.ultrasearch;

public class Constants {
	public enum ListMode{LIST, GRID};
	public enum ListServiceUpdate{ DAY, TWO_DAYS, WEEKLY, MONTHLY, NEVER };
	public enum ListOrder { ALPHABETIC, LAST_RUN, RUN_COUNT };
	public static class Preferences{
		public static final String 	LIST_MODE_KEY="list_mode";
		public static final String 	UPDATE_SERVICE_KEY="update_service";
		public static final ListServiceUpdate 
									UPDATE_SERVICE_DEFAULT=ListServiceUpdate.TWO_DAYS;
		public static final String 	VERSION_KEY="version";
		public static final int 	VERSION=2;
		public static final String UPDATE_DB_ON_START = "update_db_on_start";

        public static final String LIST_WIDGET_PREFIX = "list_widget_";
		public static final String LIST_ORDER = "list_order";
    }

	public static class Database {

		public static final String 	NAME = "documentationIndexed.db";
		public static final int 	VERSION = 2;
		
	}
	
	public static class Time{
		public static final int DAYS=1000*3600*24;
		public static final int DAYS_STATISTICS=33;
	}
}
