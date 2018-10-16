package eu.rkosir.feecollector.fragment.dashboardFragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import eu.rkosir.feecollector.entity.Team;
import eu.rkosir.feecollector.activity.teamManagement.TeamActivity;
import eu.rkosir.feecollector.helper.JsonObjectConverter;
import eu.rkosir.feecollector.helper.SharedPreferencesSaver;
import eu.rkosir.feecollector.helper.ShowTeamAdapter;
import eu.rkosir.feecollector.helper.ShowTeamsAdapter;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowTeams extends Fragment {
	private ProgressBar progressBar;
	private ShowTeamsAdapter teamsAdapter;

	private RecyclerView mRecyclerView;
	private ShowTeamAdapter mAdapter;
	private RecyclerView.LayoutManager mLayoutManager;

	public ShowTeams() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment

		progressBar = getActivity().findViewById(R.id.pb_loading_indicator);
		View view = inflater.inflate(R.layout.fragment_show_teams, container, false);
		mRecyclerView = view.findViewById(R.id.teamsList);
		mRecyclerView.setHasFixedSize(true);
		mLayoutManager = new LinearLayoutManager(getActivity());
		loadTeams();

		return view;
	}


	private void loadTeams() {
		String uri = String.format(AppConfig.URL_GET_TEAMS,
				new JsonObjectConverter(SharedPreferencesSaver.getUser(getApplicationContext())).getString("email"));

		ArrayList<Team> teams = new ArrayList<>();
		progressBar.setVisibility(View.VISIBLE);
		StringRequest stringRequest = new StringRequest(Request.Method.GET, uri, response -> {
			JSONObject object = null;
			try {
				object = new JSONObject(response);
				JSONArray teamArray = object.getJSONArray("teams");
				for(int i = 0; i < teamArray.length(); i++) {
					JSONObject team = teamArray.getJSONObject(i);
					teams.add(new Team(team.getInt("id"),team.getString("team_name"),false, team.getString("connection_number")));
				}
				JSONArray adminTeamsArray = object.getJSONArray("admin");
				for(int i = 0; i < adminTeamsArray.length(); i++) {
					JSONObject team = adminTeamsArray.getJSONObject(i);
					teams.add(new Team(team.getInt("id"),team.getString("team_name"),true, team.getString("connection_number")));
				}
				mAdapter = new ShowTeamAdapter(teams);
				teamsAdapter = new ShowTeamsAdapter(getActivity(), teams);
				mRecyclerView.setLayoutManager(mLayoutManager);
				mRecyclerView.setAdapter(mAdapter);

				mAdapter.setOnItemClickListener(position -> {
					SharedPreferencesSaver.setLastTeamName(getActivity(),teams.get(position).getName());
					SharedPreferencesSaver.setLastTeamId(getActivity(),teams.get(position).getConnection_number());
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