package eu.rkosir.feecollector.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import eu.rkosir.feecollector.AppConfig;
import eu.rkosir.feecollector.R;
import eu.rkosir.feecollector.helper.JsonObjectConverter;
import eu.rkosir.feecollector.helper.SharedPreferencesSaver;
import eu.rkosir.feecollector.helper.VolleySingleton;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ChangePasswordActivity extends AppCompatActivity {
	private Toolbar mToolbar;
	private EditText mInputPassword;
	private EditText mInputPasswordCheck;
	private EditText mInputCurrentPassword;
	private Button mButton;
	private ProgressBar mProgressBar;
	private boolean facebook_login;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_password);

		facebook_login = SharedPreferencesSaver.getLogin(this);
		initialize();
		mButton.setOnClickListener(view -> {
			if(attemptToRegister()) {
				changePassword(facebook_login);
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
		mToolbar.setTitle(R.string.change_password);
		mToolbar.setNavigationOnClickListener(view -> onBackPressed());

		mButton = findViewById(R.id.confirmButton);
		mInputPassword = findViewById(R.id.password_change);
		mInputPasswordCheck = findViewById(R.id.password_change_check);
		mInputCurrentPassword = findViewById(R.id.current_password);
		mProgressBar = findViewById(R.id.pb_loading_indicator);

		if (facebook_login) {
			findViewById(R.id.current_password_label).setVisibility(View.GONE);
			mInputCurrentPassword.setVisibility(View.GONE);
		}
	}

	/**
	 * Field validation and focus the field in case of any problem
	 * @return boolean
	 */
	private boolean attemptToRegister() {
		mInputPasswordCheck.setError(null);
		mInputPassword.setError(null);
		String currentPassword = null;
		if (!facebook_login) {
			mInputCurrentPassword.setError(null);
			currentPassword = mInputCurrentPassword.getText().toString();
		}
		String password = mInputPassword.getText().toString();
		String passwordCheck = mInputPasswordCheck.getText().toString();

		boolean cancel = false;
		View focusView = null;

		if (TextUtils.isEmpty(currentPassword) && !facebook_login) {
			mInputCurrentPassword.setError(getString(R.string.error_field_required),null);
			focusView = mInputCurrentPassword;
			cancel = true;
		} else if (TextUtils.isEmpty(password)) {
			mInputPassword.setError(getString(R.string.error_field_required),null);
			focusView = mInputPassword;
			cancel = true;
		} else if (password.length() < AppConfig.PASSWORD_LENGTH) {
			mInputPassword.setError(getString(R.string.error_pass_too_short),null);
			focusView = mInputPassword;
			cancel = true;
		} else if (TextUtils.isEmpty(passwordCheck)) {
			mInputPasswordCheck.setError(getString(R.string.error_field_required));
			focusView = mInputPasswordCheck;
			cancel = true;
		} else if (!password.equals(passwordCheck)) {
			mInputPasswordCheck.setError(getString(R.string.error_password_not_match));
			focusView = mInputPasswordCheck;
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
					Toast.makeText(this, R.string.successfull_change_of_password,Toast.LENGTH_LONG).show();
					if (facebook_login) {
						SharedPreferencesSaver.setLogin(this,false);
						Intent intent = new Intent(this, DashboardActivity.class);
						this.startActivity(intent);
						this.finish();
					}
				} else {
					mInputCurrentPassword.setError(this.getString(R.string.error_current_password_not_match), null);
					View focusView = mInputCurrentPassword;
					focusView.requestFocus();
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}, error -> {
			Toast.makeText(this,R.string.unknown_error,Toast.LENGTH_LONG).show();
		}){
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String,String> params = new HashMap<>();
				params.put("email", new JsonObjectConverter(SharedPreferencesSaver.getUser(getApplicationContext())).getString("email"));
				params.put("password", mInputPassword.getText().toString());
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
}
