package com.feecollector.android.feecollector.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.feecollector.android.feecollector.BackgroundTasks.CreateNewUser;
import com.feecollector.android.feecollector.R;
import com.feecollector.android.feecollector.User.Entity.User;

public class RegistrationActivity extends AppCompatActivity {

	private String TAG = LoginActivity.class.getSimpleName();

	private EditText inputName;
	private EditText inputSurname;
	private Button createUserbtn;
	private ProgressBar progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);

		inputName = findViewById(R.id.name);
		inputSurname = findViewById(R.id.surname);
		createUserbtn = findViewById(R.id.createUser);
		progressBar = findViewById(R.id.pb_loading_indicator);

		createUserbtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				User user = new User(inputName.getText().toString(),inputSurname.getText().toString());
				if(attemptToRegister()) {
					new CreateNewUser(RegistrationActivity.this,user, progressBar).execute();
				}
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
