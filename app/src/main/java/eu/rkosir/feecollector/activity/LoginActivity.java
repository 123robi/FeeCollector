package eu.rkosir.feecollector.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.crashlytics.android.Crashlytics;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import eu.rkosir.feecollector.AppConfig;
import eu.rkosir.feecollector.R;
import eu.rkosir.feecollector.activity.teamManagement.TeamActivity;
import eu.rkosir.feecollector.entity.User;
import eu.rkosir.feecollector.helper.InternetConnection;
import eu.rkosir.feecollector.helper.JsonObjectConverter;
import eu.rkosir.feecollector.helper.SharedPreferencesSaver;
import eu.rkosir.feecollector.helper.VolleySingleton;


public class LoginActivity extends AppCompatActivity {

	private EditText mEmail_login;
	private EditText mPassword_login;

	private Button mLoginButton;
	private LoginButton mLoginButton_facebook;
	private TextView mSignUp;
	private ProgressBar mProgressBar;

	private CallbackManager mCallbackManager;
	private String firebaseToken;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mCallbackManager.onActivityResult(requestCode,resultCode,data);
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		checkIfLogged();
		initialize();
		buttonListeners();
	}

	/**
	 * check if logged -> if yes display Dashboard activity
	 */
	private void checkIfLogged() {
		AccessToken accessToken = AccessToken.getCurrentAccessToken();
		boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

		if( isLoggedIn || SharedPreferencesSaver.getToken(LoginActivity.this)) {
			Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
			LoginActivity.this.startActivity(intent);
			LoginActivity.this.finish();
		}
	}

	/**
	 * if not logged initialize all fields
	 */
	private void initialize() {
		mEmail_login = findViewById(R.id.email_login);
		mPassword_login = findViewById(R.id.password_login);
		if(getIntent().getStringExtra("email") != null) {
			mEmail_login.setText(getIntent().getStringExtra("email"));
			mPassword_login.requestFocus();
		}
		mLoginButton = findViewById(R.id.login_button);
		mLoginButton_facebook = findViewById(R.id.login_button_facebook);

		float fbIconScale = 1.45F;
		Drawable drawable = this.getResources().getDrawable(
				com.facebook.R.drawable.com_facebook_button_icon);
		drawable.setBounds(0, 0, (int)(drawable.getIntrinsicWidth()*fbIconScale),
				(int)(drawable.getIntrinsicHeight()*fbIconScale));
		mLoginButton_facebook.setCompoundDrawables(drawable, null, null, null);
		mLoginButton_facebook.setCompoundDrawablePadding(this.getResources().
				getDimensionPixelSize(R.dimen.fb_margin_override_textpadding));
		mLoginButton_facebook.setPadding(
				this.getResources().getDimensionPixelSize(
						R.dimen.fb_margin_override_lr),
				this.getResources().getDimensionPixelSize(
						R.dimen.fb_margin_override_top),
				this.getResources().getDimensionPixelSize(
						R.dimen.fb_margin_override_lr),
				this.getResources().getDimensionPixelSize(
						R.dimen.fb_margin_override_bottom));

		mLoginButton_facebook.setReadPermissions(Arrays.asList(
				"public_profile", "email"));
		mCallbackManager = CallbackManager.Factory.create();

		mSignUp = findViewById(R.id.signUp);

		mProgressBar = findViewById(R.id.pb_loading_indicator);

		FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( this, instanceIdResult -> {
			firebaseToken = instanceIdResult.getToken();
			SharedPreferencesSaver.setFcmToken(this,firebaseToken);
		});
	}

	/**
	 * assign click listeners to buttons, e.g. loginButton, registration page button and facebook_button
	 */
	private void buttonListeners() {
		mLoginButton.setOnClickListener(v -> {
			if(InternetConnection.getInstance(LoginActivity.this).isOnline()){
				login();
			} else {
				Toast.makeText(LoginActivity.this, R.string.toast_connection_warning, Toast.LENGTH_LONG).show();
			}
		});

		mSignUp.setOnClickListener(v -> {
			Intent intent = new Intent(this, RegistrationActivity.class);
			this.startActivity(intent);
		});

		LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
			@Override
			public void onSuccess(LoginResult loginResult) {
				getUserInfo();
			}

			@Override
			public void onCancel() {

			}

			@Override
			public void onError(FacebookException error) {
				Toast.makeText(LoginActivity.this, R.string.toast_connection_warning, Toast.LENGTH_LONG).show();
			}
		});
	}

	/**
	 * get user info from facebook Button
	 */
	private void getUserInfo() {
		AccessToken accessToken = AccessToken.getCurrentAccessToken();
		GraphRequest request = GraphRequest.newMeRequest(
				accessToken,
				(object, response) -> {
					User user = null;
					try {
						String name = object.getString("name");
						String email = "";
						if (name != null && email != null && !name.isEmpty() && !email.isEmpty()) {
							user = new User(name,email, "", "",generatePassword(20, AppConfig.ALPHA_CAPS + AppConfig.ALPHA + AppConfig.SPECIAL_CHARS));
							user.setFacebook_json(object.toString());
							facebookLogin(user);
						} else {
							AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
							View mView = getLayoutInflater().inflate(R.layout.dialog_add_email,null);
							TextInputEditText mEmail = mView.findViewById(R.id.email);
							Button button = mView.findViewById(R.id.add_facebook);

							mBuilder.setView(mView);
							AlertDialog dialog = mBuilder.create();
							dialog.show();
							button.setOnClickListener(v -> {
								if (checkForEmail(mEmail,mView)) {
									User userFacebook =new User(name,mEmail.getText().toString(), "", "",generatePassword(20, AppConfig.ALPHA_CAPS + AppConfig.ALPHA + AppConfig.SPECIAL_CHARS));
									userFacebook.setFacebook_json(object.toString());
									dialog.hide();
									facebookLogin(userFacebook);
								}
							});
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
				});
		Bundle parameters = new Bundle();
		parameters.putString("fields", "id, name, email, picture.width(120).height(120)");
		request.setParameters(parameters);
		request.executeAsync();
	}

	/**
	 * Sending a Volley Post Request to obtain weather the user entered the correct credentials, using 2 parameter: email, password
	 */
	private void login() {
		mProgressBar.setVisibility(View.VISIBLE);
		StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_LOGIN, response -> {
			JSONObject object = null;

			try {
				object = new JSONObject(response);
				if (!object.getBoolean("error")) {
					Toast.makeText(this, R.string.toast_successful_login,Toast.LENGTH_LONG).show();

					SharedPreferencesSaver.setToken(this,true);

					Intent intent = new Intent(this, DashboardActivity.class);
					SharedPreferencesSaver.setUser(this,object.getString("user"));
					this.startActivity(intent);
					this.finish();

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
				params.put("email", mEmail_login.getText().toString());
				params.put("password", mPassword_login.getText().toString());
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
	 * Sending a Volley Post Request to register a user in the database or update facebook_json using 4 parameter: name, email, password, facebook_json
	 * @param user
	 */
	private void facebookLogin(User user) {
		mProgressBar.setVisibility(View.VISIBLE);
		StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_REGISTER, response -> {
			JSONObject object = null;
			try {
				object = new JSONObject(response);
				JsonObjectConverter converter = new JsonObjectConverter(object.getString("user"));
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
					Intent intent = new Intent(this, DashboardActivity.class);
					SharedPreferencesSaver.setUser(this, object.getString("user"));
					this.startActivity(intent);
					this.finish();
					Toast.makeText(this, R.string.toast_successful_login,Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}, error -> {
			Crashlytics.logException(error);
			Toast.makeText(this,R.string.toast_unknown_error,Toast.LENGTH_LONG).show();
		}){
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String,String> params = new HashMap<>();
				params.put("name", user.getName());
				params.put("email", user.getEmail());
				if(user.getAddress() != null) {
					params.put("address", user.getAddress());
				}
				if(user.getPhoneNumber() != null) {
					params.put("phone_number", user.getPhoneNumber());
				}
				params.put("password", user.getPassword());
				params.put("facebook_json", user.getFacebook_json());
				params.put("real_user", Integer.toString(1));
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

	private boolean checkForEmail(TextInputEditText mEmail, View mView) {
		TextInputLayout layout = mView.findViewById(R.id.email_layout);
		layout.setError(null);

		boolean cancel = false;
		View focusView = null;

		String email = mEmail.getText().toString();
		if (TextUtils.isEmpty(email)) {
			layout.setError(getString(R.string.error_field_required));
			focusView = layout;
			cancel = true;
		} else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
			layout.setError(getString(R.string.error_email_validation));
			focusView = layout;
			cancel = true;
		}
		if (cancel) {
			focusView.requestFocus();
			return false;
		} else
			return true;
	}
	/**
	 * generetaing random passsowrd for facebook users -> then can change the password without the current password
	 * @param len
	 * @param dic
	 * @return
	 */
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
