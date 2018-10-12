package eu.rkosir.feecollector.helper;

public class DbContract {
	public static final int SYNC_STATUS_OK = 0;
	public static final int SYNC_STATUS_FAILED = 1;

	public static final String DATABASE_NAME = "FeeCollector";

	public static final String SYNC_STATUS = "syncstatus";
	//teams
	public static final String TEAMS = "teams";
	public static final String NAME = "team_name";
	//fees
	public static final String FEES = "fees";
	public static final String FEE_NAME = "name";
	public static final String FEE_COST = "cost";
	public static final String TEAM_ID = "team_id";
}
