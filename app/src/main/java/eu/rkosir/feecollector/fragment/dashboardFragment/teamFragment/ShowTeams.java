package eu.rkosir.feecollector.fragment.dashboardFragment.teamFragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import eu.rkosir.feecollector.AppConfig;
import eu.rkosir.feecollector.R;
import eu.rkosir.feecollector.activity.teamManagement.TeamActivity;
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
				teamsAdapter = new ShowTeamsAdapter(getActivity(), teams);
				lv.setAdapter(teamsAdapter);
				lv.setOnItemClickListener((adapterView, view1, position, l) -> {
					SharedPreferencesSaver.setLastTeamName(getActivity(),teams.get(position));
					SharedPreferencesSaver.setLastTeamId(getActivity(),ids.get(position));
					Intent intent = new Intent(getActivity(), TeamActivity.class);
					startActivity(intent);
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
