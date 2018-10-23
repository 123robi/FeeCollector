package eu.rkosir.feecollector.activity.teamManagement;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
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
import eu.rkosir.feecollector.helper.SharedPreferencesSaver;
import eu.rkosir.feecollector.helper.VolleySingleton;

public class AddMember extends AppCompatActivity {

	private EditText mName;
	private Button mButton;
	private ProgressBar mProgressBar;
	private Toolbar mToolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_member);

		mToolbar = findViewById(R.id.back_action_bar);
		mToolbar.setTitle(R.string.add_member_title);

		mName = findViewById(R.id.name);
		mProgressBar = findViewById(R.id.pb_loading_indicator);
		mButton = findViewById(R.id.add_user);
		mButton.setOnClickListener(v -> saveUserToTeam());

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
				params.put("connection_number", SharedPreferencesSaver.getLastTeamID(AddMember.this));
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
}
