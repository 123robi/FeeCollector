package eu.rkosir.feecollector.fragment.registrationFragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
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
import com.hbb20.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import eu.rkosir.feecollector.AppConfig;
import eu.rkosir.feecollector.R;
import eu.rkosir.feecollector.activity.LoginActivity;
import eu.rkosir.feecollector.entity.User;
import eu.rkosir.feecollector.helper.InternetConnection;
import eu.rkosir.feecollector.helper.SharedPreferencesSaver;
import eu.rkosir.feecollector.helper.VolleySingleton;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class Registration_2nd_step extends Fragment {

	private TextInputEditText mInputName, mInputAddress, mInputPassword;
	private TextInputLayout mInputNameLayout, mInputAddressLayout, mInputPhoneLayout, mInputPasswordLayout;
	private EditText mInputPhone;
	private Button mCreateUserbtn;
	private ProgressBar mProgressBar;
	private String mEmail;
	private CountryCodePicker ccp;
	private TextView errorMessage;

	public Registration_2nd_step() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_registration_2nd_step, container, false);
		mInputName = view.findViewById(R.id.name);
		mInputNameLayout = view.findViewById(R.id.name_layout);
		mInputAddress = view.findViewById(R.id.address);
		mInputAddressLayout = view.findViewById(R.id.address_layout);
		errorMessage = view.findViewById(R.id.error_message_number);
		ccp = view.findViewById(R.id.cpp);
		mInputPhone = view.findViewById(R.id.phone);
		mInputPhoneLayout = view.findViewById(R.id.phone_layout);
		ccp.registerCarrierNumberEditText(mInputPhone);
		mInputPassword = view.findViewById(R.id.password);
		mInputPasswordLayout = view.findViewById(R.id.passwordLayout);
		mProgressBar = getActivity().findViewById(R.id.pb_loading_indicator);
		mCreateUserbtn = view.findViewById(R.id.createUser);
		mEmail = getArguments().getString("email");
		return view;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mCreateUserbtn.setOnClickListener(v -> {
			if(attemptToRegister()) {
				if(InternetConnection.getInstance(getApplicationContext()).isOnline()) {
					User user = new User(
							mInputName.getText().toString(),
							mEmail,
							ccp.getFullNumberWithPlus(),
							mInputAddress.getText().toString(),
							mInputPassword.getText().toString()
					);
					createUser(user);
				} else {
					Toast.makeText(getApplicationContext(), R.string.toast_connection_warning, Toast.LENGTH_LONG).show();
				}
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
					Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
					this.startActivity(intent);
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
				params.put("name", user.getName());
				params.put("email", user.getEmail());
				params.put("phone_number", user.getPhoneNumber());
				params.put("address", user.getAddress());
				params.put("password", user.getPassword());
				params.put("real_user",Integer.toString(1));
				if(SharedPreferencesSaver.getFcmToken(getApplicationContext()) != null) {
					params.put("fcm",SharedPreferencesSaver.getFcmToken(getApplicationContext()));
				}
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
		mInputNameLayout.setError(null);
		mInputAddressLayout.setError(null);
		mInputPasswordLayout.setError(null);
		mInputPhoneLayout.setError(null);
		errorMessage.setVisibility(View.INVISIBLE);

		String name = mInputName.getText().toString();
		String address = mInputAddress.getText().toString();
		String password = mInputPassword.getText().toString();
		String phone = mInputPhone.getText().toString();
		boolean cancel = false;
		View focusView = null;

		if (TextUtils.isEmpty(name)) {
			mInputNameLayout.setError(getString(R.string.error_field_required));
			focusView = mInputNameLayout;
			cancel = true;
		} else if (TextUtils.isEmpty(address)) {
			mInputAddressLayout.setError(getString(R.string.error_field_required));
			focusView = mInputAddressLayout;
			cancel = true;
		} else if (TextUtils.isEmpty(phone)) {
			errorMessage.setText(getString(R.string.error_field_required));
			errorMessage.setVisibility(View.VISIBLE);
			focusView = mInputPhoneLayout;
			cancel = true;
		} else if (TextUtils.isEmpty(password)) {
			mInputPasswordLayout.setError(getString(R.string.error_field_required));
			focusView = mInputPasswordLayout;
			cancel = true;
		} else if(!ccp.isValidFullNumber()) {
			errorMessage.setText(getString(R.string.error_wrong_format));
			errorMessage.setVisibility(View.VISIBLE);
			focusView = mInputPhoneLayout;
			cancel = true;
		}


		if (cancel) {
			focusView.requestFocus();
			return false;
		} else
			return true;
	}
}
