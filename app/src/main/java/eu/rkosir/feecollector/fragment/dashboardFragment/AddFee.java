package eu.rkosir.feecollector.fragment.dashboardFragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import eu.rkosir.feecollector.activity.DashboardActivity;
import eu.rkosir.feecollector.helper.JsonObjectConverter;
import eu.rkosir.feecollector.helper.SharedPreferencesSaver;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddFee extends Fragment {

	private EditText feeName;
	private EditText feeCost;
	private Button addFee;
	private ProgressBar progressBar;

	public AddFee() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_add_fee, container, false);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().setTitle(R.string.add_fee);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initialize();
	}

	private void initialize() {
		feeName = getView().findViewById(R.id.fee_name);
		feeCost = getView().findViewById(R.id.fee_cost);
		addFee = getView().findViewById(R.id.add_fee);
		progressBar = getActivity().findViewById(R.id.pb_loading_indicator);

		addFee.setOnClickListener(view -> {
			progressBar.setVisibility(View.VISIBLE);
			StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_ADD_FEE, response -> {
				JSONObject object = null;

				try {
					object = new JSONObject(response);
					if (!object.getBoolean("error")) {
						Toast.makeText(getActivity(), R.string.successful_fee_add,Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(getActivity(), object.getString("error_msg"),Toast.LENGTH_LONG).show();
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}, error -> {
				Toast.makeText(getActivity(),R.string.unknown_error,Toast.LENGTH_LONG).show();
			}){
				@Override
				protected Map<String, String> getParams() throws AuthFailureError {
					Map<String,String> params = new HashMap<>();
					params.put("name", feeName.getText().toString());
					params.put("cost", feeCost.getText().toString());
					params.put("team_id", SharedPreferencesSaver.getTeam(getActivity()));
					return params;
				}
			};

			RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
			requestQueue.add(stringRequest);
			requestQueue.addRequestFinishedListener((RequestQueue.RequestFinishedListener<String>) request -> {
				if (progressBar != null) {
					progressBar.setVisibility(View.INVISIBLE);
				}
			});
		});
	}
}
