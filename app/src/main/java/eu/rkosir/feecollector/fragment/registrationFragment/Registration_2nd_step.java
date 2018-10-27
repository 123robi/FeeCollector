package eu.rkosir.feecollector.fragment.registrationFragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Patterns;
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
import eu.rkosir.feecollector.activity.ChangePasswordActivity;
import eu.rkosir.feecollector.activity.LoginActivity;
import eu.rkosir.feecollector.activity.RegistrationActivity;
import eu.rkosir.feecollector.entity.User;
import eu.rkosir.feecollector.helper.InternetConnection;
import eu.rkosir.feecollector.helper.SharedPreferencesSaver;
import eu.rkosir.feecollector.helper.VolleySingleton;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class Registration_2nd_step extends Fragment {

	private EditText mInputName;
	private EditText mInputPassword;
	private Button mCreateUserbtn;
	private ProgressBar mProgressBar;
	private String mEmail;

	public Registration_2nd_step() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_registration_2nd_step, container, false);
		mInputName = view.findViewById(R.id.name);
		mInputPassword = view.findViewById(R.id.password);
		mProgressBar = getActivity().findViewById(R.id.pb_loading_indicator);
		mCreateUserbtn = view.findViewById(R.id.createUser);
		mEmail = getArguments().getString("email");
		return view;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mCreateUserbtn.setOnClickListener(v -> {
			User user = new User(mInputName.getText().toString(),mEmail.toString(), mInputPassword.getText().toString());
			if(attemptToRegister() && InternetConnection.getInstance(getApplicationContext()).isOnline()) {
				createUser(user);
			} else {
				Toast.makeText(getApplicationContext(), R.string.toast_connection_warning, Toast.LENGTH_LONG).show();
			}
		});
	}

	/**
	 * Sending a Volley Post Request to create a user using 3 parameter: name, email, password
	 * @param user
	 */
	private void createUser(User user) {
		mProgressBar.setVisibility(View.VISIBLE);
		StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_REGISTER, response -> {
			JSONObject object = null;
			try {
				object = new JSONObject(response);
				if(!object.getBoolean("error")) {
					Toast.makeText(getApplicationContext(), R.string.toast_successful_registration,Toast.LENGTH_LONG).show();
					SharedPreferencesSaver.setUser(getApplicationContext(), object.getString("user"));
					if(object.getJSONObject("user").isNull("facebook_json")) {
						Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
						this.startActivity(intent);
						getActivity().finish();
					} else {
						Intent intent = new Intent(getApplicationContext(), ChangePasswordActivity.class);
						SharedPreferencesSaver.setLogin(getApplicationContext(),true);
						this.startActivity(intent);
						getActivity().finish();
					}

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
				params.put("name", user.getName());
				params.put("email", user.getEmail());
				params.put("password", user.getPassword());
				params.put("real_user",Integer.toString(1));
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
	 * validate fields and request focus if any trouble
	 * @return true|false
	 */
	private boolean attemptToRegister() {
		mInputName.setError(null);
		mInputPassword.setError(null);

		String name = mInputName.getText().toString();
		String password = mInputPassword.getText().toString();

		boolean cancel = false;
		View focusView = null;

		if (TextUtils.isEmpty(name)) {
			mInputName.setError(getString(R.string.error_field_required));
			focusView = mInputName;
			cancel = true;
		} else if (TextUtils.isEmpty(password)) {
			mInputPassword.setError(getString(R.string.error_field_required),null);
			focusView = mInputPassword;
			cancel = true;
		}
		if (cancel) {
			focusView.requestFocus();
			return false;
		}else
			return true;
	}
}