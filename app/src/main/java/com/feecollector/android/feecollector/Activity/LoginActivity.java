package com.feecollector.android.feecollector.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.feecollector.android.feecollector.AppConfig;
import com.feecollector.android.feecollector.BackgroundTasks.CheckCredentials;
import com.feecollector.android.feecollector.BackgroundTasks.CreateNewUser;
import com.feecollector.android.feecollector.Helper.TokenSaver;
import com.feecollector.android.feecollector.R;
import com.feecollector.android.feecollector.User.Entity.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.util.Arrays;

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

		AccessToken accessToken = AccessToken.getCurrentAccessToken();
		boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

		if( isLoggedIn || TokenSaver.getToken(LoginActivity.this)) {
			Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
			LoginActivity.this.startActivity(intent);
			LoginActivity.this.finish();
		}
		email_login = findViewById(R.id.email_login);
		password_login = findViewById(R.id.password_login);

		loginButton = findViewById(R.id.login_button);
		loginButton_facebook = findViewById(R.id.login_button_facebook);
		loginButton_facebook.setReadPermissions(Arrays.asList(
				"public_profile", "email", "user_birthday", "user_friends"));
		signUp = findViewById(R.id.signUp);
		progressBar = findViewById(R.id.pb_loading_indicator);

		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new CheckCredentials(LoginActivity.this,progressBar).execute(email_login.getText().toString(), password_login.getText().toString());
			}
		});

		signUp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
				LoginActivity.this.startActivity(intent);
				LoginActivity.this.finish();
			}
		});

		callbackManager = CallbackManager.Factory.create();

		loginButton_facebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
			@Override
			public void onSuccess(LoginResult loginResult) {
				getUserInfo(loginResult);
			}

			@Override
			public void onCancel() {

			}

			@Override
			public void onError(FacebookException error) {

			}
		});
	}

	private void getUserInfo(LoginResult loginResult) {
		AccessToken accessToken = AccessToken.getCurrentAccessToken();
		GraphRequest request = GraphRequest.newMeRequest(
				accessToken,
				new GraphRequest.GraphJSONObjectCallback() {
					@Override
					public void onCompleted(JSONObject object, GraphResponse response) {
						User user = null;
						try {
							user = new User(object.getString("name"),object.getString("email"), generatePassword(20,AppConfig.ALPHA_CAPS + AppConfig.ALPHA + AppConfig.SPECIAL_CHARS));
							user.setFacebook_json(object.toString());
							Log.d("USER", user.toString());
						} catch (JSONException e) {
							e.printStackTrace();
						}
						new CreateNewUser(LoginActivity.this,user, progressBar,true).execute();
						Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
						intent.putExtra(AppConfig.FACEBOOK_DETAILS,object.toString());
						LoginActivity.this.startActivity(intent);
						LoginActivity.this.finish();
					}
				});
		Bundle parameters = new Bundle();
		parameters.putString("fields", "id, name, email, picture.width(120).height(120)");
		request.setParameters(parameters);
		request.executeAsync();
	}

	public static String generatePassword(int len, String dic) {
		SecureRandom random = new SecureRandom();
		String result = "";
		for (int i = 0; i < len; i++) {
			int index = random.nextInt(dic.length());
			result += dic.charAt(index);
		}
		return result;
	}
}
