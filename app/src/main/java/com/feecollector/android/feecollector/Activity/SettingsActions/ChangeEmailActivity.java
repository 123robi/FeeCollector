package com.feecollector.android.feecollector.Activity.SettingsActions;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.feecollector.android.feecollector.R;

public class ChangeEmailActivity extends AppCompatActivity {
	private EditText inputPassword;
	private EditText inputEmail;
	private Button confirmButton;
	private ProgressBar progressBar;
	private Toolbar toolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_email);
		initialize();
	}

	private void initialize() {
		toolbar = findViewById(R.id.back_action_bar);
		toolbar.setTitle(R.string.change_email);
		toolbar.setNavigationOnClickListener(view -> onBackPressed());

		inputPassword = findViewById(R.id.password);
		inputEmail = findViewById(R.id.email);
		confirmButton = findViewById(R.id.confirmButton);
		progressBar = findViewById(R.id.pb_loading_indicator);
	}
}
