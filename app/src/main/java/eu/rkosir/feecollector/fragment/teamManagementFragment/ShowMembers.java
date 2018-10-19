package eu.rkosir.feecollector.fragment.teamManagementFragment;


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
import eu.rkosir.feecollector.adapters.ShowMembersAdapter;
import eu.rkosir.feecollector.entity.User;
import eu.rkosir.feecollector.helper.JsonObjectConverter;
import eu.rkosir.feecollector.helper.SharedPreferencesSaver;
import eu.rkosir.feecollector.helper.VolleySingleton;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowMembers extends Fragment {

	private ProgressBar mProgressBar;
	private RecyclerView mRecyclerView;
	private ShowMembersAdapter mAdapter;
	private RecyclerView.LayoutManager mLayoutManager;

	public ShowMembers() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_add_member, container, false);
		mRecyclerView = view.findViewById(R.id.members_list);
		mLayoutManager = new LinearLayoutManager(getActivity());
		mProgressBar = getActivity().findViewById(R.id.pb_loading_indicator);
		mProgressBar.setVisibility(View.INVISIBLE);

		getMembers();
		return view;
	}

	/**
	 * Sending a Volley GET Request to find users to a specific teams that you can join, using 2 url parameter: email, team_id
	 */
	private void getMembers() {
		String uri = String.format(AppConfig.URL_GET_All_MEMBERS,
				SharedPreferencesSaver.getLastTeamID(getApplicationContext()));
		StringRequest stringRequest = new StringRequest(Request.Method.GET, uri, response -> {
			JSONObject object = null;
			try {
				object = new JSONObject(response);
				if (!object.getBoolean("error")) {
					ArrayList<User> membersList = new ArrayList<>();
					JSONObject admin = object.getJSONArray("admin").getJSONObject(0);
					User addUser = new User(admin.getString("name"),admin.getInt("id"));
					addUser.setRole("Manager");
					membersList.add(addUser);
					JSONArray membersArray = object.getJSONArray("members");
					for(int i = 0; i < membersArray.length(); i++) {
						JSONObject user = membersArray.getJSONObject(i);
						User member = new User(user.getString("name"),user.getInt("id"));
						member.setRole("Player");
						membersList.add(member);
					}
					mAdapter = new ShowMembersAdapter(membersList, getActivity());
					mRecyclerView.setLayoutManager(mLayoutManager);
					mRecyclerView.setAdapter(mAdapter);

					mAdapter.setOnItemClickListener(position -> {
						Toast.makeText(getActivity(), membersList.get(position).getName(),Toast.LENGTH_LONG).show();
					});
				} else {
					Toast.makeText(getActivity(),object.getString("error_msg"),Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				Toast.makeText(getActivity(),R.string.toast_unknown_error,Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
		}, error -> {
			Toast.makeText(getActivity(),R.string.toast_unknown_error,Toast.LENGTH_LONG).show();
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
