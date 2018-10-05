package eu.rkosir.feecollector.fragment.dashboardFragment;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import eu.rkosir.feecollector.activity.ChangePasswordActivity;
import eu.rkosir.feecollector.activity.DashboardActivity;
import eu.rkosir.feecollector.activity.LoginActivity;
import eu.rkosir.feecollector.helper.JsonObjectConverter;
import eu.rkosir.feecollector.helper.SharedPreferencesSaver;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateTeam extends Fragment {

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

		team_name = getView().findViewById(R.id.team_name);
		create_team_button = getView().findViewById(R.id.create_team);
		progressBar = getView().findViewById(R.id.pb_loading_indicator);

		create_team_button.setOnClickListener(view -> {
			progressBar.setVisibility(View.VISIBLE);
			StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_LOGIN, response -> {
				JSONObject object = null;

				try {
					object = new JSONObject(response);
					Log.d("JSON", object + "JSON");
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

			RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
			requestQueue.add(stringRequest);
			requestQueue.addRequestFinishedListener((RequestQueue.RequestFinishedListener<String>) request -> {
				if (progressBar != null) {
					progressBar.setVisibility(View.INVISIBLE);
				}
			});
		});

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_create_team, container, false);
	}

}
