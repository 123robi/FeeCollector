package eu.rkosir.feecollector.fragment.teamManagementFragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import eu.rkosir.feecollector.AppConfig;
import eu.rkosir.feecollector.R;
import eu.rkosir.feecollector.helper.SharedPreferencesSaver;
import eu.rkosir.feecollector.helper.VolleySingleton;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddMember extends Fragment {

	private EditText mName;
	private Button mButton;
	private ProgressBar mProgressBar;

	public AddMember() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_add_member, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mName = view.findViewById(R.id.name);
		mProgressBar = getActivity().findViewById(R.id.pb_loading_indicator);
		mButton = view.findViewById(R.id.add_user);
		mButton.setOnClickListener(v -> saveUserToTeam());
	}

	/**
	 * Saving random data to a database so any user joining to a team can select this member and therefore see his fees that has already been made to his name
	 * Sending a Volley Post Request to save user to a team using 2 parameters: name, connection_number
	 */
	private void saveUserToTeam() {
		mProgressBar.setVisibility(View.VISIBLE);
		StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_ADD_MEMBER_TO_TEAM, response -> {
			JSONObject object = null;
			try {
				object = new JSONObject(response);
				if(!object.getBoolean("error")) {
					Toast.makeText(getContext(), R.string.toast_successful_adding_member_to_team,Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getActivity(), object.getString("error_msg"),Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}, error -> {
			Toast.makeText(getActivity(),R.string.toast_unknown_error,Toast.LENGTH_LONG).show();
		}){
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String,String> params = new HashMap<>();
				params.put("name", mName.getText().toString());
				params.put("connection_number",SharedPreferencesSaver.getLastTeamID(getContext()));
				return params;
			}
		};

		RequestQueue requestQueue = VolleySingleton.getInstance(getContext()).getRequestQueue();
		requestQueue.add(stringRequest);
		requestQueue.addRequestFinishedListener((RequestQueue.RequestFinishedListener<String>) request -> {
			if (mProgressBar != null) {
				mProgressBar.setVisibility(View.INVISIBLE);
			}
		});
	}
}
