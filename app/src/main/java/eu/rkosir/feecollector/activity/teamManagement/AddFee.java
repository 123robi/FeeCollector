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
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import eu.rkosir.feecollector.AppConfig;
import eu.rkosir.feecollector.R;
import eu.rkosir.feecollector.helper.SharedPreferencesSaver;

public class AddFee extends AppCompatActivity {

	private EditText feeName;
	private EditText feeCost;
	private Button addFee;
	private Toolbar toolbar;
	private ProgressBar progressBar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_fee);
		initialize();
	}
	private void initialize() {
		toolbar = findViewById(R.id.back_action_bar);
		toolbar.setTitle(R.string.add_fee_title);
		toolbar.setNavigationOnClickListener(view -> {
			onBackPressed();
		});
		feeName = findViewById(R.id.fee_name);
		feeCost = findViewById(R.id.fee_cost);
		addFee = findViewById(R.id.add_fee);
		progressBar = findViewById(R.id.pb_loading_indicator);

		addFee.setOnClickListener(view -> {
			progressBar.setVisibility(View.VISIBLE);
			StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_ADD_FEE, response -> {
				JSONObject object = null;

				try {
					object = new JSONObject(response);
					if (!object.getBoolean("error")) {
						Toast.makeText(this, R.string.successful_fee_add,Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(this, object.getString("error_msg"),Toast.LENGTH_LONG).show();
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}, error -> {
				Toast.makeText(this,R.string.unknown_error,Toast.LENGTH_LONG).show();
			}){
				@Override
				protected Map<String, String> getParams() throws AuthFailureError {
					Map<String,String> params = new HashMap<>();
					params.put("name", feeName.getText().toString());
					params.put("cost", feeCost.getText().toString());
					params.put("connection_number", SharedPreferencesSaver.getLastTeamID(AddFee.this));
					return params;
				}
			};

			RequestQueue requestQueue = Volley.newRequestQueue(this);
			requestQueue.add(stringRequest);
			requestQueue.addRequestFinishedListener((RequestQueue.RequestFinishedListener<String>) request -> {
				if (progressBar != null) {
					progressBar.setVisibility(View.INVISIBLE);
				}
			});
		});
	}
}
