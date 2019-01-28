package eu.rkosir.feecollector.fragment.teamManagementFragment;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.clans.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import eu.rkosir.feecollector.AppConfig;
import eu.rkosir.feecollector.R;
import eu.rkosir.feecollector.activity.teamManagement.calendar.AddEvent;
import eu.rkosir.feecollector.entity.Fee;
import eu.rkosir.feecollector.entity.User;
import eu.rkosir.feecollector.activity.teamManagement.AddFee;
import eu.rkosir.feecollector.helper.JsonObjectConverter;
import eu.rkosir.feecollector.helper.SharedPreferencesSaver;
import eu.rkosir.feecollector.helper.VolleySingleton;

import static com.facebook.FacebookSdk.getApplicationContext;
import static eu.rkosir.feecollector.AppConfig.URL_CHANGE_DETAILS;
import static eu.rkosir.feecollector.AppConfig.URL_SAVE_FEE_TO_USER;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddFeeToMember extends Fragment implements DatePickerDialog.OnDateSetListener {

	private FloatingActionButton mAddFee;
	private AutoCompleteTextView mAutoCompletePlayer;
	private AutoCompleteTextView mAutoCompleteFee;
	private AutoCompleteTextView mAutoCompleteDate;
	private Button mAddFeeToMember;
	private ArrayAdapter<User> adapter;
	private ArrayAdapter<Fee> adapter1;
	private User mSavingUser;
	private Fee mSavingFee;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private Calendar cFeeDate;
	private int mYear, mMonth, mDay;

	public AddFeeToMember() {
		// Required empty public constructor
	}


	@SuppressLint("ClickableViewAccessibility")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment

		View view = inflater.inflate(R.layout.fragment_add_fee_to_member, container, false);
		mAutoCompletePlayer = view.findViewById(R.id.choose_player);
		mSwipeRefreshLayout = view.findViewById(R.id.swiperefresh);
		mSwipeRefreshLayout.setEnabled(false);
		mAutoCompleteFee = view.findViewById(R.id.choose_fee);
		mAddFeeToMember = view.findViewById(R.id.add_fee_to_member);
		mAutoCompleteDate = view.findViewById(R.id.choose_date);
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
		mAddFeeToMember.setOnClickListener(v -> {
			if (attemptToSaveFee()) {
				storeFeeToMember();
			}
		});
		cFeeDate = Calendar.getInstance();
		mYear = cFeeDate.get(Calendar.YEAR);
		mMonth = cFeeDate.get(Calendar.MONTH);
		mDay = cFeeDate.get(Calendar.DAY_OF_MONTH);
		mAutoCompleteDate.setText(getDateFormat(cFeeDate));
		mAutoCompleteDate.setOnClickListener(view13 -> new DatePickerDialog(getActivity(), AddFeeToMember.this, mYear, mMonth, mDay).show());
		mAddFee = view.findViewById(R.id.add_fee);
		mAddFee.setOnClickListener(v -> {
			Intent intent = new Intent(getActivity(), AddFee.class);
			startActivity(intent);
		});

		mAutoCompletePlayer.setOnItemClickListener((adapterView, view1, i, l) -> mSavingUser = adapter.getItem(i));
		mAutoCompleteFee.setOnItemClickListener((adapterView, view12, i, l) -> mSavingFee = adapter1.getItem(i));
		loadMembersAndFees();

		return view;
	}

	private void storeFeeToMember() {
		mSwipeRefreshLayout.setRefreshing(true);
		StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SAVE_FEE_TO_USER, response -> {
			JSONObject object = null;

			try {
				object = new JSONObject(response);
				if (!object.getBoolean("error")) {
					Toast.makeText(getApplicationContext(), R.string.toast_successful_saving_fee_to_user,Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getApplicationContext(), R.string.toast_unsuccessful_saving_fee_to_user,Toast.LENGTH_LONG).show();
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}, error -> {
			Toast.makeText(getApplicationContext(),R.string.toast_unknown_error,Toast.LENGTH_LONG).show();
		}){
			@Override
			protected Map<String, String> getParams() {
				Map<String,String> params = new HashMap<>();
				params.put("email", mSavingUser.getEmail());
				params.put("date", String.valueOf(AppConfig.dateOfFeeConverter.format(cFeeDate.getTime())));
				params.put("connection_number", SharedPreferencesSaver.getLastTeamID(getApplicationContext()));
				params.put("id", String.valueOf(mSavingFee.getId()));
				return params;
			}
		};

		RequestQueue requestQueue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();
		requestQueue.add(stringRequest);
		requestQueue.addRequestFinishedListener((RequestQueue.RequestFinishedListener<String>) request -> {
			mSwipeRefreshLayout.setRefreshing(false);
		});
	}

	/**
	 * Sending a Volley GET Request to gather Team_members and fees using 1 URL parameter: team_id
	 */
	private void loadMembersAndFees() {

		mSwipeRefreshLayout.setRefreshing(true);
		String uri = String.format(AppConfig.URL_GET_TEAM_MEMEBERS_AND_FEES,
				SharedPreferencesSaver.getLastTeamID(getApplicationContext()));
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
							user.getInt("id"),
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
					adapter = new ArrayAdapter<User>(getApplicationContext(),
							R.layout.auto_complete_text_view_layout, membersList);
					mAutoCompletePlayer.setAdapter(adapter);
				}
				if (feesList.size() > 0) {
					adapter1 = new ArrayAdapter<Fee>(getApplicationContext(),
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
			mSwipeRefreshLayout.setRefreshing(false);
		});
	}

	private boolean attemptToSaveFee() {
		mAutoCompleteFee.setError(null);
		mAutoCompletePlayer.setError(null);

		String fee = mAutoCompleteFee.getText().toString();
		String player = mAutoCompletePlayer.getText().toString();

		boolean cancel = false;
		View focusView = null;

		if (TextUtils.isEmpty(fee) ) {
			mAutoCompleteFee.setError(getString(R.string.error_field_required));
			focusView = mAutoCompleteFee;
			cancel = true;
		} else if (TextUtils.isEmpty(player)) {
			mAutoCompletePlayer.setError(getString(R.string.error_field_required));
			focusView = mAutoCompletePlayer;
			cancel = true;
		}
		if (cancel) {
			focusView.requestFocus();
			return false;
		} else
			return true;
	}

	@Override
	public void onDateSet(DatePicker datePicker, int year, int month, int day) {
		cFeeDate.set(year, month, day);
		mAutoCompleteDate.setText(getDateFormat(cFeeDate));
	}

	private String getDateFormat(Calendar c) {
		String comma = ", ";
		Date date = c.getTime();
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%ta", date))
				.append(comma)
				.append(String.format("%tb", date))
				.append(" ")
				.append(c.get(Calendar.DAY_OF_MONTH))
				.append(comma)
				.append(c.get(Calendar.YEAR));
		return sb.toString();
	}
}
