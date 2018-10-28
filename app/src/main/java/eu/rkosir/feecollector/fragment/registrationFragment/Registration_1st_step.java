package eu.rkosir.feecollector.fragment.registrationFragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import eu.rkosir.feecollector.activity.LoginActivity;
import eu.rkosir.feecollector.helper.VolleySingleton;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class Registration_1st_step extends Fragment {
	private EditText mInputEmail;
	private Button mNext;
	private ProgressBar mProgressBar;
	private TextView mSignIn;

	public Registration_1st_step() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_registration_1st_step, container, false);
		mInputEmail = view.findViewById(R.id.email);
		mSignIn = view.findViewById(R.id.signIn);
		mNext = view.findViewById(R.id.next);
		mProgressBar = getActivity().findViewById(R.id.pb_loading_indicator);
		buttonListeners();
		return view;
	}

	/**
	 * append button listeners to all buttons
	 */
	private void buttonListeners() {
		mNext.setOnClickListener(v -> {
			if (attemptToRegister()) {
				checkUserExistence();
			}
		});

		mSignIn.setOnClickListener(v -> {
			Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
			this.startActivity(intent);
			getActivity().finish();
		});

	}

	/**
	 * validate fields and request focus if any trouble
	 *
	 * @return true|false
	 */
	private boolean attemptToRegister() {
		mInputEmail.setError(null);
		String email = mInputEmail.getText().toString();

		boolean cancel = false;
		View focusView = null;
		if (TextUtils.isEmpty(email)) {
			mInputEmail.setError(getString(R.string.error_field_required), null);
			focusView = mInputEmail;
			cancel = true;
		} else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
			mInputEmail.setError(getString(R.string.error_email_validation), null);
			focusView = mInputEmail;
			cancel = true;
		}
		if (cancel) {
			focusView.requestFocus();
			return false;
		} else
			return true;
	}

	/**
	 * Checks for users existance, if exists go to login page, else create user
	 *
	 * @return true|false
	 */
	private void checkUserExistence() {
		mProgressBar.setVisibility(View.VISIBLE);
		StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_EXISTS, response -> {
			JSONObject object = null;

			try {
				object = new JSONObject(response);

				if (object.getBoolean("exists") && object.getBoolean("real_user")) {
					Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
					intent.putExtra("email", mInputEmail.getText().toString());
					Toast.makeText(getApplicationContext(), R.string.toast_user_found, Toast.LENGTH_LONG).show();
					startActivity(intent);
					getActivity().finish();
				} else if(object.getBoolean("exists") && !(object.getBoolean("real_user"))){
					Bundle bundle = new Bundle();
					bundle.putString("email", mInputEmail.getText().toString());
					Registration_update_step registration = new Registration_update_step();
					registration.setArguments(bundle);
					getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, registration, "registation_2_step").addToBackStack(null).commit();
				} else {
					Bundle bundle = new Bundle();
					bundle.putString("email", mInputEmail.getText().toString());
					Registration_2nd_step registration = new Registration_2nd_step();
					registration.setArguments(bundle);
					getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, registration, "registation_2_step").addToBackStack(null).commit();
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}, error -> {
			Toast.makeText(getApplicationContext(), R.string.toast_unknown_error, Toast.LENGTH_LONG).show();
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<>();
				params.put("email", mInputEmail.getText().toString());
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
}

