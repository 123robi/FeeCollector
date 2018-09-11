package com.feecollector.android.feecollector.Activity;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.feecollector.android.feecollector.AppConfig;
import com.feecollector.android.feecollector.BackgroundTasks.CheckCredentials;
import com.feecollector.android.feecollector.BackgroundTasks.CreateNewUser;
import com.feecollector.android.feecollector.Helper.InternetConnection;
import com.feecollector.android.feecollector.Helper.TokenSaver;
import com.feecollector.android.feecollector.R;
import com.feecollector.android.feecollector.User.Entity.User;

import org.json.JSONException;

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
		checkIfLogged();
		initialize();
		buttonListeners();
	}


	private void checkIfLogged() {
		AccessToken accessToken = AccessToken.getCurrentAccessToken();
		boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

		if( isLoggedIn || TokenSaver.getToken(LoginActivity.this)) {
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
		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(InternetConnection.getInstance(LoginActivity.this).isOnline()){
					new CheckCredentials(LoginActivity.this,progressBar).execute(email_login.getText().toString(), password_login.getText().toString());
				} else {
					Toast.makeText(LoginActivity.this, R.string.connection_warning, Toast.LENGTH_LONG).show();
				}
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
						user = new User(object.getString("name"),object.getString("email"), generatePassword(20,AppConfig.ALPHA_CAPS + AppConfig.ALPHA + AppConfig.SPECIAL_CHARS));
						user.setFacebook_json(object.toString());
					} catch (JSONException e) {
						e.printStackTrace();
					}
					new CreateNewUser(LoginActivity.this,user, progressBar,true).execute();
				});
		Bundle parameters = new Bundle();
		parameters.putString("fields", "id, name, email, picture.width(120).height(120)");
		request.setParameters(parameters);
		request.executeAsync();
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
