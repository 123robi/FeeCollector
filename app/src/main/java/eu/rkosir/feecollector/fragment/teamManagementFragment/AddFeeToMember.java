package eu.rkosir.feecollector.fragment.teamManagementFragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import eu.rkosir.feecollector.activity.teamManagement.AddFee;
import eu.rkosir.feecollector.activity.teamManagement.TeamActivity;
import eu.rkosir.feecollector.helper.JsonObjectConverter;
import eu.rkosir.feecollector.helper.SharedPreferencesSaver;
import eu.rkosir.feecollector.helper.ShowTeamsAdapter;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddFeeToMember extends Fragment {

	private FloatingActionButton addFee;
	private ProgressBar progressBar;
	private TextView members;

	public AddFeeToMember() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_add_fee_to_member, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		progressBar = getActivity().findViewById(R.id.pb_loading_indicator);
		members = getActivity().findViewById(R.id.members);
		addFee = view.findViewById(R.id.add_fee);
		addFee.setOnClickListener(v -> {
			Intent intent = new Intent(getActivity(), AddFee.class);
			startActivity(intent);
		});
		loadMembers();
	}
	private void loadMembers() {
		String uri = String.format(AppConfig.URL_GET_TEAM_MEMEBERS,
				SharedPreferencesSaver.getLastTeamID(getActivity()));
		Log.d("URL", uri + "");
		progressBar.setVisibility(View.VISIBLE);
		StringRequest stringRequest = new StringRequest(Request.Method.GET, uri, response -> {
			JSONObject object = null;
			try {
				object = new JSONObject(response);
				JSONArray teamArray = object.getJSONArray("members");
				for(int i = 0; i < teamArray.length(); i++) {
					JSONObject user = teamArray.getJSONObject(i);
					members.append(user.getString("name") + "\n");
				}
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
