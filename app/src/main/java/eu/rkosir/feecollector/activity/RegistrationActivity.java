package eu.rkosir.feecollector.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import eu.rkosir.feecollector.R;
import eu.rkosir.feecollector.User.entity.User;
import eu.rkosir.feecollector.backgroundTasks.CreateNewUser;
import eu.rkosir.feecollector.helper.InternetConnection;

public class RegistrationActivity extends AppCompatActivity {

	private String TAG = LoginActivity.class.getSimpleName();

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
				new CreateNewUser(RegistrationActivity.this,user, progressBar, false).execute();
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
}
