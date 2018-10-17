package eu.rkosir.feecollector.fragment.teamManagementFragment;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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
import eu.rkosir.feecollector.entity.Fee;
import eu.rkosir.feecollector.entity.User;
import eu.rkosir.feecollector.activity.teamManagement.AddFee;
import eu.rkosir.feecollector.helper.SharedPreferencesSaver;
import eu.rkosir.feecollector.helper.VolleySingleton;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddFeeToMember extends Fragment {

	private FloatingActionButton addFee;
	private ProgressBar progressBar;
	private AutoCompleteTextView autoCompletePlayer;
	private AutoCompleteTextView autoCompleteFee;
	private Button add_fee_to_member;

	public AddFeeToMember() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_add_fee_to_member, container, false);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		progressBar = getActivity().findViewById(R.id.pb_loading_indicator);
		autoCompletePlayer = getActivity().findViewById(R.id.choose_player);
		autoCompleteFee = getActivity().findViewById(R.id.choose_fee);
		add_fee_to_member = getActivity().findViewById(R.id.add_fee_to_member);
		autoCompletePlayer.setOnTouchListener((arg0, arg1) -> {
			loadMembersAndFees();
			autoCompletePlayer.showDropDown();
			return false;
		});
		autoCompleteFee.setOnTouchListener((arg0, arg1) -> {
			autoCompleteFee.showDropDown();
			return false;
		});
		add_fee_to_member.setOnClickListener(v -> storeFeeToMember());
		addFee = view.findViewById(R.id.add_fee);
		addFee.setOnClickListener(v -> {
			Intent intent = new Intent(getActivity(), AddFee.class);
			startActivity(intent);
		});
		loadMembersAndFees();
	}

	private void storeFeeToMember() {
		Toast.makeText(getActivity(), autoCompleteFee.getText().toString()+ " " +autoCompletePlayer.getText().toString(),Toast.LENGTH_LONG).show();
	}

	private void loadMembersAndFees() {

		String uri = String.format(AppConfig.URL_GET_TEAM_MEMEBERS,
				SharedPreferencesSaver.getLastTeamID(getActivity()));
		progressBar.setVisibility(View.VISIBLE);
		StringRequest stringRequest = new StringRequest(Request.Method.GET, uri, response -> {
			JSONObject object = null;
			try {
				object = new JSONObject(response);
				JSONArray membersArray = object.getJSONArray("members");
				JSONArray feesArray = object.getJSONArray("fees");
				ArrayList<User> membersList = new ArrayList<>();
				ArrayList<Fee> feesList = new ArrayList<>();
				for(int i = 0; i < membersArray.length(); i++) {
					JSONObject user = membersArray.getJSONObject(i);
					membersList.add(new User(user.getString("name"),user.getInt("id")));
				}
				for(int i = 0; i < feesArray.length(); i++) {
					JSONObject fee = feesArray.getJSONObject(i);
					feesList.add(new Fee(fee.getInt("id"),fee.getString("name")));
				}
				ArrayAdapter<User> adapter = new ArrayAdapter<User>(getActivity(),
						android.R.layout.simple_dropdown_item_1line, membersList);
				autoCompletePlayer.setAdapter(adapter);
				ArrayAdapter<Fee> adapter1 = new ArrayAdapter<Fee>(getActivity(),
						android.R.layout.simple_dropdown_item_1line, feesList);
				autoCompleteFee.setAdapter(adapter1);
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
}
