package eu.rkosir.feecollector.activity.teamManagement;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import eu.rkosir.feecollector.AppConfig;
import eu.rkosir.feecollector.R;
import eu.rkosir.feecollector.activity.RegistrationActivity;
import eu.rkosir.feecollector.helper.InternetConnection;
import eu.rkosir.feecollector.helper.SharedPreferencesSaver;
import eu.rkosir.feecollector.helper.VolleySingleton;

public class AddMember extends AppCompatActivity {

	private EditText mName;
	private EditText mEMail;
	private Button mButton;
	private ProgressBar mProgressBar;
	private Toolbar mToolbar;
	private CheckBox mCheckBox;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_member);

		mToolbar = findViewById(R.id.back_action_bar);
		mToolbar.setTitle(R.string.add_member_title);

		mName = findViewById(R.id.name);
		mEMail = findViewById(R.id.email);
		mProgressBar = findViewById(R.id.pb_loading_indicator);
		mCheckBox = findViewById(R.id.isAdmin);
		mButton = findViewById(R.id.add_user);
		mButton.setOnClickListener(v -> {
			if(attemptToRegister() && InternetConnection.getInstance(getApplicationContext()).isOnline()) {
				saveUserToTeam();
			} else {
				Toast.makeText(AddMember.this, R.string.toast_connection_warning, Toast.LENGTH_LONG).show();
			}
		});

		mToolbar.setNavigationOnClickListener(view -> onBackPressed());
	}
	/**
	 * Saving random data to a database so any user joining to a team can select this member and therefore see his fees that has already been made to his name
	 * Sending a Volley Post Request to save user to a team using 2 parameters: name, connection_number
	 */
	private void saveUserToTeam() {
		mProgressBar.setVisibility(View.VISIBLE);
		StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_ADD_MEMBER_TO_TEAM, response -> {
			JSONObject object = null;
			try {
				object = new JSONObject(response);
				if(!object.getBoolean("error")) {
					Toast.makeText(AddMember.this, R.string.toast_successful_adding_member_to_team,Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(AddMember.this, object.getString("error_msg"),Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}, error -> {
			Toast.makeText(AddMember.this,R.string.toast_unknown_error,Toast.LENGTH_LONG).show();
		}){
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String,String> params = new HashMap<>();
				params.put("name", mName.getText().toString());
				params.put("email", mEMail.getText().toString());
				params.put("real_user", Integer.toString(0));
				params.put("connection_number", SharedPreferencesSaver.getLastTeamID(AddMember.this));
				if (mCheckBox.isChecked()) {
					params.put("is_admin", Integer.toString(1));
				} else {
					params.put("is_admin", Integer.toString(0));
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

	/**
	 * validate fields and request focus if any trouble
	 * @return true|false
	 */
	private boolean attemptToRegister() {
		mName.setError(null);
		mEMail.setError(null);

		String name = mName.getText().toString();
		String email = mEMail.getText().toString();

		boolean cancel = false;
		View focusView = null;

		if (TextUtils.isEmpty(name)) {
			mName.setError(getString(R.string.error_field_required));
			focusView = mName;
			cancel = true;
		} else if (TextUtils.isEmpty(email)) {
			mEMail.setError(getString(R.string.error_field_required),null);
			focusView = mEMail;
			cancel = true;
		} else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
			mEMail.setError(getString(R.string.error_email_validation),null);
			focusView = mEMail;
			cancel = true;
		}
		if (cancel) {
			focusView.requestFocus();
			return false;
		}else
			return true;
	}
}
