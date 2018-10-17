package eu.rkosir.feecollector.fragment.dashboardFragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

import eu.rkosir.feecollector.AppConfig;
import eu.rkosir.feecollector.R;
import eu.rkosir.feecollector.activity.DashboardActivity;
import eu.rkosir.feecollector.activity.teamManagement.TeamActivity;
import eu.rkosir.feecollector.entity.Fee;
import eu.rkosir.feecollector.entity.User;
import eu.rkosir.feecollector.helper.JsonObjectConverter;
import eu.rkosir.feecollector.helper.SharedPreferencesSaver;
import eu.rkosir.feecollector.helper.ShowMembersAdapter;
import eu.rkosir.feecollector.helper.ShowTeamsAdapter;
import eu.rkosir.feecollector.helper.VolleySingleton;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

	private Button joinTeam;
	private ProgressBar progressBar;
	private EditText teamId;
	private RecyclerView mRecyclerView;
	private ShowMembersAdapter mAdapter;
	private RecyclerView.LayoutManager mLayoutManager;


	public MainFragment() {
		// Required empty public constructor
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mLayoutManager = new LinearLayoutManager(getActivity());
		initialize();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_main, container, false);
		mRecyclerView = view.findViewById(R.id.members_list);
		return view;
	}

	private void initialize() {
		progressBar = getActivity().findViewById(R.id.pb_loading_indicator);

		teamId = getView().findViewById(R.id.teamId);

		joinTeam = getView().findViewById(R.id.joinTeam);
		joinTeam.setOnClickListener(view -> {
			selectMember();
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().setTitle(R.string.app_name);
	}

	private void selectMember() {
		progressBar.setVisibility(View.VISIBLE);
		String uri = String.format(AppConfig.URL_GET_MEMBERS,
				teamId.getText().toString(),new JsonObjectConverter(SharedPreferencesSaver.getUser(getApplicationContext())).getString("email"));
		progressBar.setVisibility(View.VISIBLE);
		StringRequest stringRequest = new StringRequest(Request.Method.GET, uri, response -> {
			JSONObject object = null;
			try {
				object = new JSONObject(response);
				if (!object.getBoolean("error")) {
					JSONArray membersArray = object.getJSONArray("members");
					ArrayList<User> membersList = new ArrayList<>();
					for(int i = 0; i < membersArray.length(); i++) {
						JSONObject user = membersArray.getJSONObject(i);
						membersList.add(new User(user.getString("name"),user.getInt("id")));
					}
					mAdapter = new ShowMembersAdapter(membersList, getActivity());
					mRecyclerView.setLayoutManager(mLayoutManager);
					mRecyclerView.setAdapter(mAdapter);

					mAdapter.setOnItemClickListener(position -> {
						Toast.makeText(getActivity(), membersList.get(position).getName(),Toast.LENGTH_LONG).show();
						joinTeam(membersList.get(position));
					});
				} else {
					Toast.makeText(getActivity(),object.getString("error_msg"),Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				Toast.makeText(getActivity(),R.string.unknown_error,Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
		}, error -> {
			Toast.makeText(getActivity(),R.string.unknown_error,Toast.LENGTH_LONG).show();
		});

		RequestQueue requestQueue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();
		requestQueue.add(stringRequest);
		requestQueue.addRequestFinishedListener((RequestQueue.RequestFinishedListener<String>) request -> {
			if (progressBar != null) {
				progressBar.setVisibility(View.INVISIBLE);
			}
		});
	}

	private void joinTeam(User name) {
		StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_JOIN_TEAM, response -> {
			JSONObject object = null;

			try {
				object = new JSONObject(response);
				if (!object.getBoolean("error")) {
					Toast.makeText(getActivity(), R.string.successful_joining,Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getActivity(), object.getString("error_msg"),Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}, error -> {
			Toast.makeText(getActivity(),R.string.unknown_error,Toast.LENGTH_LONG).show();
		}){
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String,String> params = new HashMap<>();
				params.put("email", new JsonObjectConverter(SharedPreferencesSaver.getUser(getApplicationContext())).getString("email"));
				params.put("update_id", ""+name.getId());
				return params;
			}
		};


		RequestQueue requestQueue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();
		requestQueue.add(stringRequest);
		requestQueue.addRequestFinishedListener((RequestQueue.RequestFinishedListener<String>) request -> {
			if (progressBar != null) {
				progressBar.setVisibility(View.INVISIBLE);
			}
		});
	}
}
