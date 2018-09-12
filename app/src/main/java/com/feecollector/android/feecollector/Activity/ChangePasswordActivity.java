package com.feecollector.android.feecollector.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.feecollector.android.feecollector.AppConfig;
import com.feecollector.android.feecollector.BackgroundTasks.ChangePassword;
import com.feecollector.android.feecollector.R;

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

		facebook_login = getIntent().getBooleanExtra("facebook_registration", false);
		initialize();
		button.setOnClickListener(view -> {
			if(attemptToRegister()) {
				if (facebook_login) {
					new ChangePassword(this, progressBar, inputCurrentPassword, getIntent().getBooleanExtra("facebook_registration", false)).execute(inputPassword.getText().toString(), inputCurrentPassword.getText().toString());
				} else {
					new ChangePassword(this, progressBar, null, getIntent().getBooleanExtra("facebook_registration", false)).execute(inputPassword.getText().toString(), null);
				}
			}
		});
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
}
