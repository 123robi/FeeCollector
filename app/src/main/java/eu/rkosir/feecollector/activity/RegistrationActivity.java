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

public class RegistrationActivity extends AppCompatActivity {

	private EditText inputName;
	private EditText inputEmail;
	private EditText inputPassword;
	private Button createUserbtn;
	private ProgressBar progressBar;
	private TextView signIn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);
		initialize();
		buttonListeners();
	}

	private void initialize() {
		inputName = findViewById(R.id.name);
		inputEmail = findViewById(R.id.email);
		inputPassword = findViewById(R.id.password);

		signIn = findViewById(R.id.signIn);

		createUserbtn = findViewById(R.id.createUser);
		progressBar = findViewById(R.id.pb_loading_indicator);
	}

	private void buttonListeners() {
		createUserbtn.setOnClickListener(v -> {
			User user = new User(inputName.getText().toString(),inputEmail.getText().toString(), inputPassword.getText().toString());
			if(attemptToRegister() && InternetConnection.getInstance(RegistrationActivity.this).isOnline()) {
				createUser(user);
			} else {
				Toast.makeText(RegistrationActivity.this, R.string.connection_warning, Toast.LENGTH_LONG).show();
			}
		});

		signIn.setOnClickListener(v -> {
			Intent intent = new Intent(this, LoginActivity.class);
			this.startActivity(intent);
			this.finish();
		});

	}

	private boolean attemptToRegister() {
		inputName.setError(null);
		inputEmail.setError(null);
		inputPassword.setError(null);

		String name = inputName.getText().toString();
		String email = inputEmail.getText().toString();
		String password = inputPassword.getText().toString();

		boolean cancel = false;
		View focusView = null;

		if (TextUtils.isEmpty(name)) {
			inputName.setError(getString(R.string.error_field_required));
			focusView = inputName;
			cancel = true;
		} else if (TextUtils.isEmpty(email)) {
			inputEmail.setError(getString(R.string.error_field_required),null);
			focusView = inputEmail;
			cancel = true;
		} else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
			inputEmail.setError(getString(R.string.error_email_validation),null);
			focusView = inputEmail;
			cancel = true;
		} else if (TextUtils.isEmpty(password)) {
			inputPassword.setError(getString(R.string.error_field_required),null);
			focusView = inputPassword;
			cancel = true;
		}
		if (cancel) {
			focusView.requestFocus();
			return false;
		}else
			return true;
	}

	private void createUser(User user) {
		progressBar.setVisibility(View.VISIBLE);
		StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_REGISTER, response -> {
			JSONObject object = null;
			try {
				object = new JSONObject(response);
				if(!object.getBoolean("error")) {
					Toast.makeText(this, R.string.successful_registration,Toast.LENGTH_LONG).show();
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
			Toast.makeText(this,R.string.unknown_error,Toast.LENGTH_LONG).show();
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

		RequestQueue requestQueue = Volley.newRequestQueue(this);
		requestQueue.add(stringRequest);
		requestQueue.addRequestFinishedListener((RequestQueue.RequestFinishedListener<String>) request -> {
			if (progressBar != null) {
				progressBar.setVisibility(View.INVISIBLE);
			}
		});
	}
}
