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


public class AddFee extends AppCompatActivity {

	private EditText mFeeName;
	private EditText mFeeCost;
	private Button mAddFee;
	private Toolbar mToolbar;
	private ProgressBar mProgressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_fee);
		initialize();
	}

	/**
	 * initializie all required fields in the Activity
	 */
	private void initialize() {
		mToolbar = findViewById(R.id.back_action_bar);
		mToolbar.setTitle(R.string.add_fee_title);

		mFeeName = findViewById(R.id.fee_name);
		mFeeCost = findViewById(R.id.fee_cost);
		mAddFee = findViewById(R.id.add_fee);
		mProgressBar = findViewById(R.id.pb_loading_indicator);

		mToolbar.setNavigationOnClickListener(view -> onBackPressed());
		mAddFee.setOnClickListener(view -> storeFee());

	}

	/**
	 * Sending a Volley Post Request to store a fee using 1 parameter: connection_number, name, cost
	 */
	private void storeFee() {
		mProgressBar.setVisibility(View.VISIBLE);
		StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_ADD_FEE, response -> {
			JSONObject object = null;

			try {
				object = new JSONObject(response);
				if (!object.getBoolean("error")) {
					Toast.makeText(this, R.string.toast_successful_fee_add,Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(this, object.getString("error_msg"),Toast.LENGTH_LONG).show();
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}, error -> {
			Toast.makeText(this,R.string.toast_unknown_error,Toast.LENGTH_LONG).show();
		}){
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String,String> params = new HashMap<>();
				params.put("name", mFeeName.getText().toString());
				params.put("cost", mFeeCost.getText().toString());
				params.put("connection_number", SharedPreferencesSaver.getLastTeamID(AddFee.this));
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
