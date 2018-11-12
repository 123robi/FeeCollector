package eu.rkosir.feecollector.fragment.teamManagementFragment;


import android.content.Intent;
import android.os.Bundle;
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
import com.github.clans.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import eu.rkosir.feecollector.AppConfig;
import eu.rkosir.feecollector.R;
import eu.rkosir.feecollector.activity.teamManagement.AddMember;
import eu.rkosir.feecollector.activity.teamManagement.UserDetail;
import eu.rkosir.feecollector.activity.teamManagement.calendar.ShowEvent;
import eu.rkosir.feecollector.adapters.ShowMembersAdapter;
import eu.rkosir.feecollector.entity.User;
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
	private FloatingActionButton mAddMember;

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
		View view = inflater.inflate(R.layout.fragment_members, container, false);
		mRecyclerView = view.findViewById(R.id.members_list);
		mLayoutManager = new LinearLayoutManager(getApplicationContext());
		mProgressBar = getActivity().findViewById(R.id.pb_loading_indicator);
		mProgressBar.setVisibility(View.INVISIBLE);
		mAddMember = view.findViewById(R.id.add_member);
		mAddMember.setOnClickListener(v -> {
			Intent intent = new Intent(getContext(), AddMember.class);
			startActivity(intent);
		});
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
					User addUser = new User(
							admin.getString("name"),
							admin.getString("email"),
							admin.getString("phone_number"),
							admin.getString("address")
					);
					addUser.setRole("Manager");
					membersList.add(addUser);
					JSONArray membersArray = object.getJSONArray("members");
					for(int i = 0; i < membersArray.length(); i++) {
						JSONObject user = membersArray.getJSONObject(i);
						User member = new User(
								user.getString("name"),
								user.getString("email"),
								user.getString("phone_number"),
								user.getString("address")
						);
						member.setRole("Player");
						membersList.add(member);
					}
					mAdapter = new ShowMembersAdapter(membersList, getApplicationContext());
					mRecyclerView.setLayoutManager(mLayoutManager);
					mRecyclerView.setAdapter(mAdapter);

					mAdapter.setOnItemClickListener(position -> {
						Intent intent = new Intent(getApplicationContext(), UserDetail.class);
						intent.putExtra("user", membersList.get(position));
						startActivity(intent);
					});
				} else {
					Toast.makeText(getApplicationContext(),object.getString("error_msg"),Toast.LENGTH_LONG).show();
				}
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
