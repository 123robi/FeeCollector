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
import eu.rkosir.feecollector.helper.VolleySingleton;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class        CreateTeam extends Fragment {

	private EditText team_name;
	private Button create_team_button;
	private ProgressBar progressBar;

	public CreateTeam() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().setTitle(R.string.create_team);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_create_team, container, false);
	}
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		progressBar = getActivity().findViewById(R.id.pb_loading_indicator);
		super.onViewCreated(view, savedInstanceState);
		initialize();
	}

	private void initialize() {
		team_name = getView().findViewById(R.id.team_name);
		create_team_button = getView().findViewById(R.id.create_team);

		create_team_button.setOnClickListener(view -> {
			progressBar.bringToFront();
			progressBar.setVisibility(View.VISIBLE);
			StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_SAVE_TEAM, response -> {
				JSONObject object = null;

				try {
					object = new JSONObject(response);
					if (!object.getBoolean("error")) {
						Toast.makeText(getActivity(), R.string.successful_team_creation,Toast.LENGTH_LONG).show();

						Intent intent = new Intent(getActivity(), DashboardActivity.class);
						getActivity().startActivity(intent);
						getActivity().finish();

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
					params.put("team_name", team_name.getText().toString());
					params.put("email", new JsonObjectConverter(SharedPreferencesSaver.getUser(getApplicationContext())).getString("email"));
					return params;
				}
			};

			RequestQueue requestQueue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();
			requestQueue.add(stringRequest);
			requestQueue.addRequestFinishedListener((RequestQueue.RequestFinishedListener<String>) request -> {
				if (progressBar != null) {
					progressBar.setVisibility(View.INVISIBLE);
				}
			});
		});
	}
}
