package eu.rkosir.feecollector.activity;

import android.content.Intent;
import android.os.Parcelable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
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
import eu.rkosir.feecollector.helper.JsonObjectConverter;
import eu.rkosir.feecollector.helper.SharedPreferencesSaver;
import eu.rkosir.feecollector.helper.VolleySingleton;

import static eu.rkosir.feecollector.AppConfig.URL_CHANGE_DETAILS;


public class ChangePasswordActivity extends AppCompatActivity {
	private Toolbar mToolbar;
	private TextInputLayout mInputNameLayout, mInputAddressLayout, mInputPhoneNumberLayout, mInputCurrentPasswordLayout,mInputPasswordLayout, mInputPasswordCheckLayout;
	private TextInputEditText mInputName, mInputAddress, mInputPhoneNumber, mInputCurrentPassword, mInputPassword, mInputPasswordCheck;

	private Button mButton;
	private ProgressBar mProgressBar;

	private String name, address, phoneNumber;
	private boolean facebook_login;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_password);

		facebook_login = SharedPreferencesSaver.getLogin(this);
		initialize();
		mButton.setOnClickListener(view -> {
			if(mInputCurrentPassword.getText().toString().length() > 0 || mInputPassword.getText().toString().length() > 0 || mInputPasswordCheck.getText().toString().length() > 0) {
				if (attemptToChangePassword()) {
					changePassword(facebook_login);
				}
			}
			else if (!address.equals(mInputAddress.getText().toString()) || !address.equals(mInputAddress.getText().toString()) || !address.equals(mInputAddress.getText().toString())){
				if (attemptToChangeDetails()) {
					changeDetails();
				}
			}
		});
	}

	/**
	 * If facebook login than return back to dashboard activity, else go bak to settings
	 */
	@Override
	public void onBackPressed() {
		if (facebook_login) {
			Intent intent = new Intent(this, DashboardActivity.class);
			startActivity(intent);
			finish();
		} else {
			super.onBackPressed();
		}
	}

	private void initialize() {
		mToolbar = findViewById(R.id.back_action_bar);
		mToolbar.setTitle(R.string.settings_change_password);
		mToolbar.setNavigationOnClickListener(view -> onBackPressed());

		mButton = findViewById(R.id.confirmButton);

		mInputName = findViewById(R.id.name);
		name = new JsonObjectConverter(SharedPreferencesSaver.getUser(getApplicationContext())).getString("name");
		mInputName.setText(name);
		mInputNameLayout = findViewById(R.id.name_layout);

		mInputAddress = findViewById(R.id.address);
		address = new JsonObjectConverter(SharedPreferencesSaver.getUser(getApplicationContext())).getString("address");
		if (!address.equals("null")) {
			mInputAddress.setText(address);
		}
		mInputAddressLayout = findViewById(R.id.address_layout);

		mInputPhoneNumber = findViewById(R.id.phone_number);
		phoneNumber = new JsonObjectConverter(SharedPreferencesSaver.getUser(getApplicationContext())).getString("phone_number");
		if (!phoneNumber.equals("null")) {
			mInputPhoneNumber.setText(phoneNumber);
		}
		mInputPhoneNumberLayout = findViewById(R.id.phone_number_layout);

		mInputPassword = findViewById(R.id.password);
		mInputPasswordLayout = findViewById(R.id.password_layout);

		mInputPasswordCheck = findViewById(R.id.password_check);
		mInputPasswordCheckLayout = findViewById(R.id.password_check_layout);

		mInputCurrentPassword = findViewById(R.id.current_password);
		mInputCurrentPasswordLayout = findViewById(R.id.current_password_layout);

		mProgressBar = findViewById(R.id.pb_loading_indicator);

		if (facebook_login) {
			mInputCurrentPasswordLayout.setVisibility(View.GONE);
		}
	}

	/**
	 * Field validation and focus the field in case of any problem
	 * @return boolean
	 */
	private boolean attemptToChangePassword() {
		mInputPasswordCheckLayout.setError(null);
		mInputPasswordLayout.setError(null);
		String currentPassword = null;
		if (!facebook_login) {
			mInputCurrentPasswordLayout.setError(null);
			currentPassword = mInputCurrentPassword.getText().toString();
		}
		String password = mInputPassword.getText().toString();
		String passwordCheck = mInputPasswordCheck.getText().toString();

		boolean cancel = false;
		View focusView = null;

		if (TextUtils.isEmpty(currentPassword) && !facebook_login) {
			mInputCurrentPasswordLayout.setError(getString(R.string.error_field_required));
			focusView = mInputCurrentPasswordLayout;
			cancel = true;
		} else if (TextUtils.isEmpty(password)) {
			mInputPasswordLayout.setError(getString(R.string.error_field_required));
			focusView = mInputPasswordLayout;
			cancel = true;
		} else if (password.length() < AppConfig.PASSWORD_LENGTH) {
			mInputPasswordLayout.setError(getString(R.string.error_pass_too_short));
			focusView = mInputPasswordLayout;
			cancel = true;
		} else if (TextUtils.isEmpty(passwordCheck)) {
			mInputPasswordCheckLayout.setError(getString(R.string.error_field_required));
			focusView = mInputPasswordCheckLayout;
			cancel = true;
		} else if (!password.equals(passwordCheck)) {
			mInputPasswordCheckLayout.setError(getString(R.string.error_password_not_match));
			focusView = mInputPasswordCheckLayout;
			cancel = true;
		}

		if (cancel) {
			focusView.requestFocus();
			return false;
		} else
			return true;
	}
	/**
	 * Field validation and focus the field in case of any problem for DETAILS
	 * @return boolean
	 */
	private boolean attemptToChangeDetails() {
		mInputNameLayout.setError(null);
		mInputAddressLayout.setError(null);
		mInputPhoneNumberLayout.setError(null);

		String name = mInputName.getText().toString();
		String address = mInputAddress.getText().toString();
		String phoneNumber = mInputPhoneNumber.getText().toString();

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
		}  else if (TextUtils.isEmpty(phoneNumber)) {
			mInputPhoneNumberLayout.setError(getString(R.string.error_field_required));
			focusView = mInputPhoneNumberLayout;
			cancel = true;
		}

		if (cancel) {
			focusView.requestFocus();
			return false;
		} else
			return true;
	}

	/**
	 * Sending a Volley Post Request to change a password using 2 or 3 parameter: email, password, (current_password)
	 * @param facebook_login
	 */
	private void changePassword(boolean facebook_login) {
		mProgressBar.setVisibility(View.VISIBLE);
		StringRequest stringRequest = new StringRequest(Request.Method.POST, facebook_login?AppConfig.URL_CHANGE_PASSWORD_FACEBOOK : AppConfig.URL_CHANGE_PASSWORD, response -> {
			JSONObject object = null;

			try {
				object = new JSONObject(response);

				if (!object.getBoolean("error")) {
					Toast.makeText(this, R.string.toast_successful_change_of_password,Toast.LENGTH_LONG).show();
					if (facebook_login) {
						SharedPreferencesSaver.setLogin(this,false);
					}
					Intent intent = new Intent(this, DashboardActivity.class);
					this.startActivity(intent);
					this.finish();

				} else {
					mInputCurrentPassword.setError(this.getString(R.string.error_current_password_not_match), null);
					View focusView = mInputCurrentPassword;
					focusView.requestFocus();
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}, error -> {
			Toast.makeText(this,R.string.toast_unknown_error,Toast.LENGTH_LONG).show();
		}){
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String,String> params = new HashMap<>();
				params.put("email", new JsonObjectConverter(SharedPreferencesSaver.getUser(getApplicationContext())).getString("email"));
				params.put("password", mInputPassword.getText().toString());
				params.put("address", mInputAddress.getText().toString());
				params.put("phone_number", mInputPhoneNumber.getText().toString());
				if (!facebook_login) {
					params.put("current_password", mInputCurrentPassword.getText().toString());
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
	 * Sending a Volley Post Request to change a details using 3 parameter: name, address, phone_number
	 */
	private void changeDetails() {
		mProgressBar.setVisibility(View.VISIBLE);
		StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_CHANGE_DETAILS, response -> {
			JSONObject object = null;

			try {
				object = new JSONObject(response);

				if (!object.getBoolean("error")) {
					SharedPreferencesSaver.setUser(this,object.getString("user"));
					Toast.makeText(this, R.string.toast_successful_user_update,Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(this, R.string.toast_unsuccessful_user_update,Toast.LENGTH_LONG).show();
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}, error -> {
			Toast.makeText(this,R.string.toast_unknown_error,Toast.LENGTH_LONG).show();
		}){
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String,String> params = new HashMap<>();
				params.put("email", new JsonObjectConverter(SharedPreferencesSaver.getUser(getApplicationContext())).getString("email"));
				params.put("name", mInputName.getText().toString());
				params.put("address", mInputAddress.getText().toString());
				params.put("phone_number", mInputPhoneNumber.getText().toString());
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
