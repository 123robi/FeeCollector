package eu.rkosir.feecollector.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
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
import eu.rkosir.feecollector.entity.User;
import eu.rkosir.feecollector.helper.InternetConnection;
import eu.rkosir.feecollector.helper.SharedPreferencesSaver;
import eu.rkosir.feecollector.helper.VolleySingleton;

import static com.facebook.FacebookSdk.getApplicationContext;

public class RegistrationActivity extends AppCompatActivity {

	private EditText mInputName;
	private EditText mInputEmail;
	private EditText mInputPassword;
	private Button mCreateUserbtn;
	private ProgressBar mProgressBar;
	private TextView mSignIn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);
		initialize();
		buttonListeners();
	}

	/**
	 * initialize fields
	 */
	private void initialize() {
		mInputName = findViewById(R.id.name);
		mInputEmail = findViewById(R.id.email);
		mInputPassword = findViewById(R.id.password);

		mSignIn = findViewById(R.id.signIn);

		mCreateUserbtn = findViewById(R.id.createUser);
		mProgressBar = findViewById(R.id.pb_loading_indicator);
	}

	/**
	 * append button listeners to all buttons
	 */
	private void buttonListeners() {
		mCreateUserbtn.setOnClickListener(v -> {
			User user = new User(mInputName.getText().toString(),mInputEmail.getText().toString(), mInputPassword.getText().toString());
			if(attemptToRegister() && InternetConnection.getInstance(getApplicationContext()).isOnline()) {
				createUser(user);
			} else {
				Toast.makeText(RegistrationActivity.this, R.string.toast_connection_warning, Toast.LENGTH_LONG).show();
			}
		});

		mSignIn.setOnClickListener(v -> {
			Intent intent = new Intent(this, LoginActivity.class);
			this.startActivity(intent);
			this.finish();
		});

	}

	/**
	 * validate fields and request focus if any trouble
	 * @return true|false
	 */
	private boolean attemptToRegister() {
		mInputName.setError(null);
		mInputEmail.setError(null);
		mInputPassword.setError(null);

		String name = mInputName.getText().toString();
		String email = mInputEmail.getText().toString();
		String password = mInputPassword.getText().toString();

		boolean cancel = false;
		View focusView = null;

		if (TextUtils.isEmpty(name)) {
			mInputName.setError(getString(R.string.error_field_required));
			focusView = mInputName;
			cancel = true;
		} else if (TextUtils.isEmpty(email)) {
			mInputEmail.setError(getString(R.string.error_field_required),null);
			focusView = mInputEmail;
			cancel = true;
		} else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
			mInputEmail.setError(getString(R.string.error_email_validation),null);
			focusView = mInputEmail;
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
					Toast.makeText(this, R.string.toast_successful_registration,Toast.LENGTH_LONG).show();
					SharedPreferencesSaver.setUser(this, object.getString("user"));
					if(object.getJSONObject("user").isNull("facebook_json")) {
						Intent intent = new Intent(this, LoginActivity.class);
						this.startActivity(intent);
						this.finish();
					} else {
						Intent intent = new Intent(this, ChangePasswordActivity.class);
						SharedPreferencesSaver.setLogin(this,true);
						this.startActivity(intent);
						this.finish();
					}

				} else {
					Toast.makeText(this, object.getString("error_msg"),Toast.LENGTH_LONG).show();
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
				params.put("name", user.getName());
				params.put("email", user.getEmail());
				params.put("password", user.getPassword());
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
