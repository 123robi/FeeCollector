package eu.rkosir.feecollector.fragment.dashboardFragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

	private RecyclerView mRecyclerView;
	private ShowTeamsAdapter mAdapter;
	private RecyclerView.LayoutManager mLayoutManager;

	private SwipeRefreshLayout mSwipeRefreshLayout;

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
		mSwipeRefreshLayout = view.findViewById(R.id.swiperefresh);
		return view;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mSwipeRefreshLayout.setOnRefreshListener(this::loadTeams);
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
		mSwipeRefreshLayout.setRefreshing(true);
		StringRequest stringRequest = new StringRequest(Request.Method.GET, uri, response -> {
			JSONObject object = null;
			try {
				object = new JSONObject(response);
				JSONArray adminTeamsArray = object.getJSONArray("admin");
				for(int i = 0; i < adminTeamsArray.length(); i++) {
					JSONObject team = adminTeamsArray.getJSONObject(i);
					Team teamObject = new Team(team.getInt("id"),team.getString("team_name"),team.getString("currency_code"),team.getString("currency_symbol"),true, team.getString("connection_number"),team.getString("ical"));
					teams.add(teamObject);
				}
				JSONArray teamArray = object.getJSONArray("teams");
				for(int i = 0; i < teamArray.length(); i++) {
					JSONObject team = teamArray.getJSONObject(i);
					teams.add(new Team(team.getInt("id"),team.getString("team_name"),team.getString("currency_code"),team.getString("currency_symbol"),false, team.getString("connection_number"),team.getString("ical")));
				}
				mAdapter = new ShowTeamsAdapter(teams, getApplicationContext());
				mRecyclerView.setLayoutManager(mLayoutManager);
				mRecyclerView.setAdapter(mAdapter);

				mAdapter.setOnItemClickListener(position -> {
					SharedPreferencesSaver.setLastTeamName(getApplicationContext(),teams.get(position).getName());
					SharedPreferencesSaver.setLastTeamId(getApplicationContext(),teams.get(position).getConnection_number());
					SharedPreferencesSaver.setCurrencyCode(getApplicationContext(),teams.get(position).getCurrency_code());
					SharedPreferencesSaver.setCurrencySymbol(getApplicationContext(),teams.get(position).getCurrency_symbol());
					if (!teams.get(position).getIcalURL().equals("null") || teams.get(position).getIcalURL() != null) {
						SharedPreferencesSaver.setIcal(getApplicationContext(),teams.get(position).getIcalURL());
					}
					if (teams.get(position).isAdmin()) {
						SharedPreferencesSaver.setAdmin(getApplicationContext(),true);
					} else {
						SharedPreferencesSaver.setAdmin(getApplicationContext(),false);
					}
					Intent intent = new Intent(getApplicationContext(), TeamActivity.class);
					startActivity(intent);

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
			if (mSwipeRefreshLayout.isRefreshing()) {
				mSwipeRefreshLayout.setRefreshing(false);
			}
		});
	}
}
