package com.feecollector.android.feecollector.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.feecollector.android.feecollector.R;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

	private String TAG = LoginActivity.class.getSimpleName();



	private LoginButton loginButton;
	private TextView signUp;


	private Context context;
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
		context = this;

		AccessToken accessToken = AccessToken.getCurrentAccessToken();
		boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

		if(isLoggedIn) {
			Log.d("LOGED IN!!!" ,"ASDASD");
		}

		loginButton = findViewById(R.id.login_button);
		signUp = findViewById(R.id.signUp);

		signUp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
				LoginActivity.this.startActivity(intent);
				LoginActivity.this.finish();
			}
		});

		callbackManager = CallbackManager.Factory.create();

		loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
			@Override
			public void onSuccess(LoginResult loginResult) {
				AccessToken accessToken = AccessToken.getCurrentAccessToken();
				GraphRequest request = GraphRequest.newMeRequest(
						accessToken,
						new GraphRequest.GraphJSONObjectCallback() {
							@Override
							public void onCompleted(JSONObject object, GraphResponse response) {
									Log.v("LoginActivity", object.toString());
							}
						});
				Bundle parameters = new Bundle();
				parameters.putString("fields", "first_name");
				request.setParameters(parameters);
				request.executeAsync();
			}

			@Override
			public void onCancel() {

			}

			@Override
			public void onError(FacebookException error) {

			}
		});
	}
}
