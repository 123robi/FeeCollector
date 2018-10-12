package eu.rkosir.feecollector.fragment.dashboardFragment.teamFragment;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
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
import eu.rkosir.feecollector.helper.JsonObjectConverter;
import eu.rkosir.feecollector.helper.SharedPreferencesSaver;
import eu.rkosir.feecollector.helper.ShowTeamsAdapter;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowTeams extends Fragment {
	ListView lv;
	private ProgressBar progressBar;
	private ShowTeamsAdapter teamsAdapter;

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

		return view;
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
}
