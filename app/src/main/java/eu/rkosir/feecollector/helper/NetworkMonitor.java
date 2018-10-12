package eu.rkosir.feecollector.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;
/*
public class NetworkMonitor extends BroadcastReceiver{
	@Override
	public void onReceive(Context context, Intent intent) {
		if(InternetConnection.getInstance(context).isOnline()) {
			DbHelper dbHelper = new DbHelper(context);
			SQLiteDatabase database = dbHelper.getWritableDatabase();

			Cursor cursor = dbHelper.readFromLocalDatabase(database);
			while (cursor.moveToNext()) {
				int status = cursor.getInt(cursor.getColumnIndex("sync_status"));
				if (status == 0) {
					String name = cursor.getString(cursor.getColumnIndex("name"));
					StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://rkosir.eu/testsApi/add", response -> {
						dbHelper.updateLocalDatabase(name,1,database);
						context.sendBroadcast(new Intent(DbContract.UI_UPDATE_BROADCAST));
					}, error -> {
					}){
						@Override
						protected Map<String, String> getParams() throws AuthFailureError {
							Map<String,String> params = new HashMap<>();
							params.put("name", name);
							return params;
						}
					};
					VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
				}
			}
			dbHelper.close();
		}
	}
}*/
