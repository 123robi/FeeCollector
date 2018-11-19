package eu.rkosir.feecollector.fragment.dashboardFragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mynameismidori.currencypicker.CurrencyPicker;
import com.mynameismidori.currencypicker.CurrencyPickerListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import eu.rkosir.feecollector.AppConfig;
import eu.rkosir.feecollector.R;
import eu.rkosir.feecollector.activity.DashboardActivity;
import eu.rkosir.feecollector.activity.LoginActivity;
import eu.rkosir.feecollector.helper.JsonObjectConverter;
import eu.rkosir.feecollector.helper.SharedPreferencesSaver;
import eu.rkosir.feecollector.helper.VolleySingleton;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateTeam extends Fragment {

	private EditText mTeam_name;
	private Button mCreate_team_button;
	private ProgressBar mProgressBar;
	private AutoCompleteTextView mCurrency;
	private ImageView imageView;
	private String mCurrencyCode, mCurrencySymbol;

	public CreateTeam() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().setTitle(R.string.create_team_title);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_create_team, container, false);
		mProgressBar = view.findViewById(R.id.pb_loading_indicator);
		mTeam_name = view.findViewById(R.id.team_name);
		mCreate_team_button = view.findViewById(R.id.create_team);
		mCurrency = view.findViewById(R.id.choose_currency);
		imageView = view.findViewById(R.id.imageView);

		mCurrency.setOnClickListener(view12 -> {
			CurrencyPicker picker = CurrencyPicker.newInstance("Select Currency");  // dialog title
			picker.setListener((name, code, symbol, flagDrawableResID) -> {
				mCurrencyCode = code;
				mCurrencySymbol = symbol;
				mCurrency.setText(name);
				imageView.setImageResource(flagDrawableResID);
				getActivity().getSupportFragmentManager().beginTransaction().remove(getActivity().getSupportFragmentManager().findFragmentByTag("CURRENCY_PICKER")).commit();
			});
			picker.show(getActivity().getSupportFragmentManager(), "CURRENCY_PICKER");
		});
		mCreate_team_button.setOnClickListener(view1 -> {
			if (attemptToCreateTeam()) {
				createTeam();
			}
		});
		return view;
	}

	/**
	 * Sending a Volley Post Request to create a team using 3 parameter: team_name, email
	 */
	private void createTeam() {
		if (mProgressBar != null) {
			mProgressBar.bringToFront();
			mProgressBar.setVisibility(View.VISIBLE);
		}

		StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_SAVE_TEAM, response -> {
			JSONObject object = null;

			try {
				object = new JSONObject(response);
				if (!object.getBoolean("error")) {
					Toast.makeText(getApplicationContext(), R.string.toast_successful_team_creation,Toast.LENGTH_LONG).show();

					Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
					getActivity().startActivity(intent);
					getActivity().finish();

				} else {
					Toast.makeText(getApplicationContext(), object.getString("error_msg"),Toast.LENGTH_LONG).show();
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}, error -> {
			Toast.makeText(getApplicationContext(),R.string.toast_unknown_error,Toast.LENGTH_LONG).show();
		}){
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String,String> params = new HashMap<>();
				params.put("team_name", mTeam_name.getText().toString());
				params.put("currency_code", mCurrencyCode);
				params.put("currency_symbol", mCurrencySymbol);
				params.put("email", new JsonObjectConverter(SharedPreferencesSaver.getUser(getApplicationContext())).getString("email"));
				return params;
			}
		};

		RequestQueue requestQueue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();
		requestQueue.add(stringRequest);
		requestQueue.addRequestFinishedListener((RequestQueue.RequestFinishedListener<String>) request -> {
			if (mProgressBar != null) {
				mProgressBar.setVisibility(View.INVISIBLE);
			}
		});
	}
	/**
	 * Field validation and focus the field in case of any problem for DETAILS
	 * @return boolean
	 */
	private boolean attemptToCreateTeam() {
		mCurrency.setError(null);
		mTeam_name.setError(null);

		String name = mTeam_name.getText().toString();
		String currency = mCurrency.getText().toString();

		boolean cancel = false;
		View focusView = null;

		if (TextUtils.isEmpty(name)) {
			mTeam_name.setError(getString(R.string.error_field_required));
			focusView = mTeam_name;
			cancel = true;
		} else if (TextUtils.isEmpty(currency)) {
			mCurrency.setError(getString(R.string.error_field_required));
			focusView = mCurrency;
			cancel = true;
		}

		if (cancel) {
			focusView.requestFocus();
			return false;
		} else
			return true;
	}
}
