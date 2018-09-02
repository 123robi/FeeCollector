package com.feecollector.android.feecollector;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.feecollector.android.feecollector.BackgroundTasks.CreateNewUser;
import com.feecollector.android.feecollector.User.Entity.User;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

	private String TAG = MainActivity.class.getSimpleName();

	private EditText inputName;
	private EditText inputSurname;
	private Button createUserbtn;
	private Context context;

	private LoginButton loginButton;
	private CallbackManager callbackManager;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		callbackManager.onActivityResult(requestCode,resultCode,data);
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = this;

		AccessToken accessToken = AccessToken.getCurrentAccessToken();
		boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
		if(isLoggedIn) {
			Log.d("LOGED IN!!!" ,"ASDASD");
		}
		inputName = findViewById(R.id.name);
		inputSurname = findViewById(R.id.surname);
		createUserbtn = findViewById(R.id.createUser);

		createUserbtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				User user = new User(inputName.getText().toString(),inputSurname.getText().toString());
				if(attemptToRegister()) {
					new CreateNewUser(context,user).execute();
				}
			}
		});

		loginButton = findViewById(R.id.login_button);

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

	private boolean attemptToRegister() {
		inputName.setError(null);
		inputSurname.setError(null);

		String username = inputName.getText().toString();
		String password = inputSurname.getText().toString();

		boolean cancel = false;
		View focusView = null;

		if (TextUtils.isEmpty(username)) {
			inputName.setError(getString(R.string.error_field_required));
			focusView = inputName;
			cancel = true;
		} else if (TextUtils.isEmpty(password)) {
			inputSurname.setError(getString(R.string.error_field_required),null);
			focusView = inputSurname;
			cancel = true;
		}

		if (cancel) {
			focusView.requestFocus();
			return false;
		}else
			return true;
	}
}
