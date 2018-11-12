package eu.rkosir.feecollector.fragment.teamManagementFragment;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.github.clans.fab.FloatingActionButton;

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

	private FloatingActionButton mAddFee;
	private ProgressBar mProgressBar;
	private AutoCompleteTextView mAutoCompletePlayer;
	private AutoCompleteTextView mAutoCompleteFee;
	private Button mAddFeeToMember;

	public AddFeeToMember() {
		// Required empty public constructor
	}


	@SuppressLint("ClickableViewAccessibility")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment

		View view = inflater.inflate(R.layout.fragment_add_fee_to_member, container, false);
		mProgressBar = getActivity().findViewById(R.id.pb_loading_indicator);
		mProgressBar.setVisibility(View.INVISIBLE);
		mAutoCompletePlayer = view.findViewById(R.id.choose_player);
		mAutoCompleteFee = view.findViewById(R.id.choose_fee);
		mAddFeeToMember = view.findViewById(R.id.add_fee_to_member);
		mAutoCompletePlayer.setOnTouchListener((arg0, arg1) -> {
			loadMembersAndFees();
			mAutoCompletePlayer.showDropDown();
			return false;
		});
		mAutoCompleteFee.setOnTouchListener((arg0, arg1) -> {
			loadMembersAndFees();
			mAutoCompleteFee.showDropDown();
			return false;
		});
		mAddFeeToMember.setOnClickListener(v -> storeFeeToMember());
		mAddFee = view.findViewById(R.id.add_fee);
		mAddFee.setOnClickListener(v -> {
			Intent intent = new Intent(getActivity(), AddFee.class);
			startActivity(intent);
		});
		loadMembersAndFees();
		return view;
	}

	private void storeFeeToMember() {
		Toast.makeText(getApplicationContext(), mAutoCompleteFee.getText().toString()+ " " +mAutoCompletePlayer.getText().toString(),Toast.LENGTH_LONG).show();
	}

	/**
	 * Sending a Volley GET Request to gather Team_members and fees using 1 URL parameter: team_id
	 */
	private void loadMembersAndFees() {

		String uri = String.format(AppConfig.URL_GET_TEAM_MEMEBERS_AND_FEES,
				SharedPreferencesSaver.getLastTeamID(getApplicationContext()));
		mProgressBar.setVisibility(View.VISIBLE);
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
					membersList.add(new User(
							user.getString("name"),
							user.getString("email"),
							user.getString("phone_number"),
							user.getString("address"))
					);
				}
				for(int i = 0; i < feesArray.length(); i++) {
					JSONObject fee = feesArray.getJSONObject(i);
					feesList.add(new Fee(fee.getInt("id"),fee.getString("name")));
				}
				if (membersList.size() > 0) {
					ArrayAdapter<User> adapter = new ArrayAdapter<User>(getApplicationContext(),
							R.layout.auto_complete_text_view_layout, membersList);
					mAutoCompletePlayer.setAdapter(adapter);
				}
				if (feesList.size() > 0) {
					ArrayAdapter<Fee> adapter1 = new ArrayAdapter<Fee>(getApplicationContext(),
							R.layout.auto_complete_text_view_layout, feesList);
					mAutoCompleteFee.setAdapter(adapter1);
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
