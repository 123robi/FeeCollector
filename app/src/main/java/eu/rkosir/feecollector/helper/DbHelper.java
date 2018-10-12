package eu.rkosir.feecollector.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper{

	private static final int DATABASE_VERSION = 1;
	private static final String CREATE_TABLE_TEAMS = "CREATE TABLE " + DbContract.TEAMS + "(id integer primary key autoincrement," + DbContract.NAME + "varchar," + DbContract.SYNC_STATUS + " integer);";
	private static final String DROP_TABLE_TEAMS = "DROP TABLE IF EXISTS " + DbContract.TEAMS;
	private static final String CREATE_TABLE_FEES = "CREATE TABLE " + DbContract.FEES +
			"(id integer primary key autoincrement," +
			DbContract.FEE_NAME + "varchar," +
			DbContract.FEE_COST + " integer," +
			DbContract.SYNC_STATUS +" integer," +
			DbContract.TEAM_ID + " integer," +
			"foreign key(team_id) references teams(id));";

	private static final String DROP_TABLE_FEES = "DROP TABLE IF EXISTS " + DbContract.FEES;

	public DbHelper(Context context) {
		super(context, DbContract.DATABASE_NAME,null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {
		sqLiteDatabase.execSQL(CREATE_TABLE_TEAMS);
		sqLiteDatabase.execSQL(CREATE_TABLE_FEES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
		sqLiteDatabase.execSQL(DROP_TABLE_TEAMS);
		sqLiteDatabase.execSQL(DROP_TABLE_FEES);
		onCreate(sqLiteDatabase);
	}
	public void saveToLocalDatabaseTeams(String name,int i, SQLiteDatabase database){
		ContentValues contentValues = new ContentValues();
		contentValues.put(DbContract.NAME,name);
		contentValues.put(DbContract.SYNC_STATUS,0);
		database.insert(DbContract.TEAMS,null,contentValues);
	}
	public void saveToLocalDatabaseFees(String name,int cost,int i, SQLiteDatabase database){
		ContentValues contentValues = new ContentValues();
		contentValues.put(DbContract.FEE_NAME,name);
		contentValues.put(DbContract.FEE_COST,cost);
		contentValues.put(DbContract.SYNC_STATUS,0);
		database.insert(DbContract.FEES,null,contentValues);
	}

	public Cursor readFromLocalDatabase(SQLiteDatabase database,String table,String[] fields) {
		return (database.query(table,fields,null,null,null,null,null));
	}

	public void updateLocalDatabaseTeams(String name,int sync_status, SQLiteDatabase database) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(DbContract.SYNC_STATUS,sync_status);
		String selection = DbContract.NAME + " LIKE ?";
		String [] selection_args = {name};
		database.update(DbContract.TEAMS,contentValues,selection,selection_args);
	}

	public void updateLocalDatabaseFees(String name,int sync_status, SQLiteDatabase database) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(DbContract.SYNC_STATUS,sync_status);
		String selection = DbContract.FEE_NAME + " LIKE ?";
		String [] selection_args = {name};
		database.update(DbContract.FEES,contentValues,selection,selection_args);
	}
}
