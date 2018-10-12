package eu.rkosir.feecollector.fragment.dashboardFragment.teamFragment;


import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import eu.rkosir.feecollector.AppConfig;
import eu.rkosir.feecollector.R;
import eu.rkosir.feecollector.helper.DbContract;
import eu.rkosir.feecollector.helper.DbHelper;
import eu.rkosir.feecollector.helper.InternetConnection;
import eu.rkosir.feecollector.helper.JsonObjectConverter;
import eu.rkosir.feecollector.helper.SharedPreferencesSaver;
import eu.rkosir.feecollector.helper.ShowTeamsAdapter;
import eu.rkosir.feecollector.helper.VolleySingleton;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowTeams extends Fragment {
	ListView lv;
	private ProgressBar progressBar;
	private ShowTeamsAdapter teamsAdapter;
	private EditText editText;
	private Button button;
	private BroadcastReceiver broadcastReceiver;
	private ArrayList<String> names;

	public ShowTeams() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment

		progressBar = getActivity().findViewById(R.id.pb_loading_indicator);
		View view = inflater.inflate(R.layout.fragment_show_teams, container, false);
		lv = view.findViewById(R.id.teamsList);
		loadTeams();
		readFromLocalStorage();

		return view;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		button = getActivity().findViewById(R.id.btnAdd);
		editText = getActivity().findViewById(R.id.add_test_text);

		names =new ArrayList<>();

		button.setOnClickListener(view1 -> {
			saveToAppServer(editText.getText().toString());
			editText.setText("");
		});
	}

	private void readFromLocalStorage() {
		DbHelper dbHelper = new DbHelper(getActivity());
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		String[] fields = {"name"};
		Cursor cursor = dbHelper.readFromLocalDatabase(database,DbContract.TEAMS, fields);
		while (cursor.moveToNext()) {
			String name = cursor.getString(cursor.getColumnIndex("name"));
			names.add(name);
			Toast.makeText(getActivity(),names.toString(),Toast.LENGTH_LONG).show();
		}
		cursor.close();
		dbHelper.close();
	}

	private void saveToAppServer(String name) {
		if (InternetConnection.getInstance(getActivity()).isOnline()) {
			StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_SAVE_TEAM, response -> {
				Toast.makeText(getActivity(), "Created",Toast.LENGTH_LONG).show();
				saveToLocalStorage(name);
			}, error -> {
				Toast.makeText(getActivity(), "FAILED",Toast.LENGTH_LONG).show();
			}){
				@Override
				protected Map<String, String> getParams() throws AuthFailureError {
					Map<String,String> params = new HashMap<>();
					params.put("name", name);
					return params;
				}
			};
			VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
		} else {
			saveToLocalStorage(name);
		}
	}


	private void loadTeams() {
		String uri = String.format(AppConfig.URL_GET_TEAMS,
				new JsonObjectConverter(SharedPreferencesSaver.getUser(getApplicationContext())).getString("email"));

		ArrayList<String> teams = new ArrayList<>();
		ArrayList<String> ids = new ArrayList<>();
		ClipboardManager clipboard = (ClipboardManager)
				getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
		progressBar.setVisibility(View.VISIBLE);
		StringRequest stringRequest = new StringRequest(Request.Method.GET, uri, response -> {
			JSONObject object = null;
			try {
				object = new JSONObject(response);
				JSONArray teamArray = object.getJSONArray("teams");
				for(int i = 0; i < teamArray.length(); i++) {
					JSONObject team = teamArray.getJSONObject(i);
					teams.add(team.getString("team_name"));
					ids.add(team.getString("connection_number"));
				}
				teamsAdapter = new ShowTeamsAdapter(getActivity(), teams,ids);
				lv.setAdapter(teamsAdapter);
				lv.setOnItemClickListener((adapterView, view1, position, l) -> {
					Toast.makeText(getActivity(), "Your team_id " + ids.get(position) + " has been copied to clipboard",Toast.LENGTH_LONG).show();
					ClipData clip = ClipData.newPlainText("id",ids.get(position));
					clipboard.setPrimaryClip(clip);
				});
			} catch (JSONException e) {
				Toast.makeText(getActivity(),R.string.unknown_error,Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
		}, error -> {
			Toast.makeText(getActivity(),R.string.unknown_error,Toast.LENGTH_LONG).show();
		});

		RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
		requestQueue.add(stringRequest);
		requestQueue.addRequestFinishedListener((RequestQueue.RequestFinishedListener<String>) request -> {
			if (progressBar != null) {
				progressBar.setVisibility(View.INVISIBLE);
			}
		});
	}
	private void saveToLocalStorage(String name) {
		DbHelper dbHelper = new DbHelper(getActivity());
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		dbHelper.saveToLocalDatabase(name,0,database);

		readFromLocalStorage();
		dbHelper.close();
	}
}
