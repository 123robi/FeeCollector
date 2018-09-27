package eu.rkosir.feecollector.activity;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import eu.rkosir.feecollector.AppConfig;
import eu.rkosir.feecollector.R;
import eu.rkosir.feecollector.User.entity.User;
import eu.rkosir.feecollector.helper.InternetConnection;
import eu.rkosir.feecollector.helper.JsonObjectConverter;
import eu.rkosir.feecollector.helper.SharedPreferencesSaver;

public class LoginActivity extends AppCompatActivity {

	private String TAG = LoginActivity.class.getSimpleName();

	private EditText email_login;
	private EditText password_login;

	private Button loginButton;
	private LoginButton loginButton_facebook;
	private TextView signUp;
	private ProgressBar progressBar;

	private CallbackManager callbackManager;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		callbackManager.onActivityResult(requestCode,resultCode,data);
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		checkIfLogged();
		initialize();
		buttonListeners();
	}


	private void checkIfLogged() {
		AccessToken accessToken = AccessToken.getCurrentAccessToken();
		boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

		if( isLoggedIn || SharedPreferencesSaver.getToken(LoginActivity.this)) {
			Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
			LoginActivity.this.startActivity(intent);
			LoginActivity.this.finish();
		}
	}

	private void initialize() {
		email_login = findViewById(R.id.email_login);
		password_login = findViewById(R.id.password_login);

		loginButton = findViewById(R.id.login_button);
		loginButton_facebook = findViewById(R.id.login_button_facebook);
		loginButton_facebook.setReadPermissions(Arrays.asList(
				"public_profile", "email", "user_birthday", "user_friends"));
		callbackManager = CallbackManager.Factory.create();

		signUp = findViewById(R.id.signUp);

		progressBar = findViewById(R.id.pb_loading_indicator);
	}

	private void buttonListeners() {
		loginButton.setOnClickListener(v -> {
			if(InternetConnection.getInstance(LoginActivity.this).isOnline()){
				login();
			} else {
				Toast.makeText(LoginActivity.this, R.string.connection_warning, Toast.LENGTH_LONG).show();
			}
		});

		signUp.setOnClickListener(v -> {
			Intent intent = new Intent(this, RegistrationActivity.class);
			this.startActivity(intent);
		});

		loginButton_facebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
			@Override
			public void onSuccess(LoginResult loginResult) {
				//Remove button so noone can click on it anymore
				if(loginButton_facebook.getVisibility() == View.VISIBLE) {
					loginButton_facebook.setVisibility(View.INVISIBLE);
				}
				getUserInfo(loginResult);
			}

			@Override
			public void onCancel() {

			}

			@Override
			public void onError(FacebookException error) {
				Toast.makeText(LoginActivity.this, R.string.connection_warning, Toast.LENGTH_LONG).show();
			}
		});
	}

	private void getUserInfo(LoginResult loginResult) {
		AccessToken accessToken = AccessToken.getCurrentAccessToken();
		GraphRequest request = GraphRequest.newMeRequest(
				accessToken,
				(object, response) -> {
					User user = null;
					try {
						user = new User(object.getString("name"),object.getString("email"), generatePassword(20, AppConfig.ALPHA_CAPS + AppConfig.ALPHA + AppConfig.SPECIAL_CHARS));
						user.setFacebook_json(object.toString());
					} catch (JSONException e) {
						e.printStackTrace();
					}
					facebookLogin(user);
				});
		Bundle parameters = new Bundle();
		parameters.putString("fields", "id, name, email, picture.width(120).height(120)");
		request.setParameters(parameters);
		request.executeAsync();
	}

	private void login() {
		StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_LOGIN, response -> {
			JSONObject object = null;

			try {
				object = new JSONObject(response);
				Log.d("JSON", object + "JSON");
				if (!object.getBoolean("error")) {
					Toast.makeText(this, R.string.successful_login,Toast.LENGTH_LONG).show();

					SharedPreferencesSaver.setToken(this,true);

					Intent intent = new Intent(this, DashboardActivity.class);
					SharedPreferencesSaver.setUser(this,object.getString("user"));
					intent.putExtra("email",email_login.getText().toString());
					this.startActivity(intent);
					this.finish();

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
				params.put("email", email_login.getText().toString());
				params.put("password", password_login.getText().toString());
				return params;
			}
		};

		RequestQueue requestQueue = Volley.newRequestQueue(this);
		requestQueue.add(stringRequest);
	}

	private void facebookLogin(User user) {
		StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_REGISTER, response -> {
			JSONObject object = null;
			try {
				object = new JSONObject(response);
				JsonObjectConverter converter = new JsonObjectConverter(object.getString("user"));
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
					Intent intent = new Intent(this, DashboardActivity.class);
					SharedPreferencesSaver.setUser(this, object.getString("user"));
					this.startActivity(intent);
					Toast.makeText(this, R.string.successful_login,Toast.LENGTH_LONG).show();
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
				params.put("facebook_json", user.getFacebook_json());
				return params;
			}
		};

		RequestQueue requestQueue = Volley.newRequestQueue(this);
		requestQueue.add(stringRequest);
	}

	private String generatePassword(int len, String dic) {
		SecureRandom random = new SecureRandom();
		String result = "";
		for (int i = 0; i < len; i++) {
			int index = random.nextInt(dic.length());
			result += dic.charAt(index);
		}
		return result;
	}
}