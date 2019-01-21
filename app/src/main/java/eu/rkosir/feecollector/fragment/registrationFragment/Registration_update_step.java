package eu.rkosir.feecollector.fragment.registrationFragment;


import android.content.Intent;
import android.os.Bundle;
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
import eu.rkosir.feecollector.activity.ChangePasswordActivity;
import eu.rkosir.feecollector.activity.LoginActivity;
import eu.rkosir.feecollector.helper.SharedPreferencesSaver;
import eu.rkosir.feecollector.helper.VolleySingleton;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class Registration_update_step extends Fragment {

	private EditText mPassword;
	private Button mButton;
	private ProgressBar mProgressBar;
	private String mEmail;

	public Registration_update_step() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_registration_update_step, container, false);
		mPassword = view.findViewById(R.id.password);
		mButton = view.findViewById(R.id.updateUser);
		mProgressBar = getActivity().findViewById(R.id.pb_loading_indicator);
		mEmail = getArguments().getString("email");
		return view;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				updateUser();
			}
		});
	}

	/**
	 * Sending a Volley Post Request to update a user using 3 parameter: name, email, password, real_user
	 */
	private void updateUser() {
		mProgressBar.setVisibility(View.VISIBLE);
		StringRequest stringRequest = new StringRequest(Request.Method.POST,AppConfig.URL_UPDATE_USER, response -> {
			JSONObject object = null;
			try {
				object = new JSONObject(response);
				if(!object.getBoolean("error")) {
					SharedPreferencesSaver.setUser(getApplicationContext(), object.getString("user"));
					Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
					intent.putExtra("email", mEmail);
					Toast.makeText(getApplicationContext(), R.string.toast_user_found, Toast.LENGTH_LONG).show();
					startActivity(intent);
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
				params.put("email", mEmail);
				params.put("password", mPassword.getText().toString());
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
}
