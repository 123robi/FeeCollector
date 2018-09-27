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

public class ChangePasswordActivity extends AppCompatActivity {
	private Toolbar toolbar;
	private EditText inputPassword;
	private EditText inputPasswordCheck;
	private EditText inputCurrentPassword;
	private TextView viewCurrentPassword;
	private Button button;
	private ProgressBar progressBar;
	private boolean facebook_login;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_password);

		facebook_login = SharedPreferencesSaver.getLogin(this);
		initialize();
		button.setOnClickListener(view -> {
			if(attemptToRegister()) {
				changePassword(facebook_login);
			}
		});
	}

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
		toolbar = findViewById(R.id.back_action_bar);
		toolbar.setTitle(R.string.change_password);
		toolbar.setNavigationOnClickListener(view -> onBackPressed());

		button = findViewById(R.id.confirmButton);
		inputPassword = findViewById(R.id.password_change);
		inputPasswordCheck = findViewById(R.id.password_change_check);
		inputCurrentPassword = findViewById(R.id.current_password);
		progressBar = findViewById(R.id.pb_loading_indicator);

		if (facebook_login) {
			findViewById(R.id.current_password_label).setVisibility(View.GONE);
			inputCurrentPassword.setVisibility(View.GONE);
		}
	}

	private boolean attemptToRegister() {
		inputPasswordCheck.setError(null);
		inputPassword.setError(null);
		String currentPassword = null;
		if (!facebook_login) {
			inputCurrentPassword.setError(null);
			currentPassword = inputCurrentPassword.getText().toString();
		}
		String password = inputPassword.getText().toString();
		String passwordCheck = inputPasswordCheck.getText().toString();

		boolean cancel = false;
		View focusView = null;

		if (TextUtils.isEmpty(currentPassword) && !facebook_login) {
			inputCurrentPassword.setError(getString(R.string.error_field_required),null);
			focusView = inputCurrentPassword;
			cancel = true;
		} else if (TextUtils.isEmpty(password)) {
			inputPassword.setError(getString(R.string.error_field_required),null);
			focusView = inputPassword;
			cancel = true;
		} else if (password.length() < AppConfig.PASSWORD_LENGTH) {
			inputPassword.setError(getString(R.string.error_pass_too_short),null);
			focusView = inputPassword;
			cancel = true;
		} else if (TextUtils.isEmpty(passwordCheck)) {
			inputPasswordCheck.setError(getString(R.string.error_field_required));
			focusView = inputPasswordCheck;
			cancel = true;
		} else if (!password.equals(passwordCheck)) {
			inputPasswordCheck.setError(getString(R.string.error_password_not_match));
			focusView = inputPasswordCheck;
			cancel = true;
		}

		if (cancel) {
			focusView.requestFocus();
			return false;
		} else
			return true;
	}

	private void changePassword(boolean facebook_login) {
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
					inputCurrentPassword.setError(this.getString(R.string.error_current_password_not_match), null);
					View focusView = inputCurrentPassword;
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
				params.put("password", inputPassword.getText().toString());
				if (!facebook_login) {
					params.put("current_password", inputCurrentPassword.getText().toString());
				}
				return params;
			}
		};

		RequestQueue requestQueue = Volley.newRequestQueue(this);
		requestQueue.add(stringRequest);
	}
}
