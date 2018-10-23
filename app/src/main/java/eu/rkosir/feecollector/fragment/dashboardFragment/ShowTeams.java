package eu.rkosir.feecollector.fragment.dashboardFragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;

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
import eu.rkosir.feecollector.adapters.ShowTeamsAdapter;
import eu.rkosir.feecollector.helper.VolleySingleton;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowTeams extends Fragment {
	private ProgressBar mProgressBar;

	private RecyclerView mRecyclerView;
	private ShowTeamsAdapter mAdapter;
	private RecyclerView.LayoutManager mLayoutManager;

	public ShowTeams() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_show_teams, container, false);
		mRecyclerView = view.findViewById(R.id.teamsList);
		mRecyclerView.setHasFixedSize(true);
		mLayoutManager = new LinearLayoutManager(getApplicationContext());
		mProgressBar = getActivity().findViewById(R.id.pb_loading_indicator);
		return view;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		loadTeams();
	}

	/**
	 *
	 * Sending a Volley GET Request to load teams in a RecyclerView using 1 parameter: email -> to access all teams that this user is either admin or a team_member
	 */
	private void loadTeams() {
		String uri = String.format(AppConfig.URL_GET_TEAMS,
				new JsonObjectConverter(SharedPreferencesSaver.getUser(getApplicationContext())).getString("email"));

		ArrayList<Team> teams = new ArrayList<>();
		mProgressBar.setVisibility(View.VISIBLE);
		mProgressBar.bringToFront();
		StringRequest stringRequest = new StringRequest(Request.Method.GET, uri, response -> {
			JSONObject object = null;
			try {
				object = new JSONObject(response);
				JSONArray adminTeamsArray = object.getJSONArray("admin");
				for(int i = 0; i < adminTeamsArray.length(); i++) {
					JSONObject team = adminTeamsArray.getJSONObject(i);
					Team teamObject = new Team(team.getInt("id"),team.getString("team_name"),true, team.getString("connection_number"));
					teamObject.setMembers(team.getInt("count_members"));
					teams.add(teamObject);
				}
				JSONArray teamArray = object.getJSONArray("teams");
				for(int i = 0; i < teamArray.length(); i++) {
					JSONObject team = teamArray.getJSONObject(i);
					teams.add(new Team(team.getInt("id"),team.getString("team_name"),false, team.getString("connection_number")));
				}
				mAdapter = new ShowTeamsAdapter(teams, getApplicationContext());
				mRecyclerView.setLayoutManager(mLayoutManager);
				mRecyclerView.setAdapter(mAdapter);

				mAdapter.setOnItemClickListener(position -> {
					SharedPreferencesSaver.setLastTeamName(getApplicationContext(),teams.get(position).getName());
					SharedPreferencesSaver.setLastTeamId(getApplicationContext(),teams.get(position).getConnection_number());
					if (teams.get(position).isAdmin()) {
						Intent intent = new Intent(getApplicationContext(), TeamActivity.class);
						startActivity(intent);
					} else {
						Toast.makeText(getApplicationContext(), "You are not allowed to enter this team as an admin",Toast.LENGTH_LONG).show();
					}

				});
			} catch (JSONException e) {
				Toast.makeText(getApplicationContext(),R.string.toast_unknown_error,Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
		}, error -> {
			Toast.makeText(getApplicationContext(),R.string.toast_unknown_error,Toast.LENGTH_LONG).show();
		});

		RequestQueue requestQueue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();
		requestQueue.add(stringRequest);
		requestQueue.addRequestFinishedListener((RequestQueue.RequestFinishedListener<String>) request -> {
			if (mProgressBar != null) {
				mProgressBar.setVisibility(View.INVISIBLE);
			}
		});
	}
}
