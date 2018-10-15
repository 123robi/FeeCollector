package eu.rkosir.feecollector.activity.teamManagement;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import eu.rkosir.feecollector.activity.DashboardActivity;
import eu.rkosir.feecollector.fragment.teamManagementFragment.AddFeeToMember;
import eu.rkosir.feecollector.fragment.teamManagementFragment.AddMember;
import eu.rkosir.feecollector.fragment.teamManagementFragment.Summary;
import eu.rkosir.feecollector.helper.SharedPreferencesSaver;
import eu.rkosir.feecollector.helper.ViewPagerAdapter;

public class TeamActivity extends AppCompatActivity {

	private TabLayout tabLayout;
	private ViewPager viewPager;
	private Toolbar toolbar;
	private ProgressBar loadingBar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_team);
		toolbar = findViewById(R.id.back_action_bar);
		toolbar.setTitle(SharedPreferencesSaver.getLastTeamName(this));
		setSupportActionBar(toolbar);
		toolbar.setNavigationOnClickListener(view -> {
			onBackPressed();
		});

		tabLayout = findViewById(R.id.navigation_top);
		viewPager = findViewById(R.id.content);
		ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
		adapter.addFragment(new Summary(), "Summary");
		adapter.addFragment(new AddFeeToMember(), "Add Fee");
		adapter.addFragment(new AddMember(), "Add Member");

		viewPager.setAdapter(adapter);
		tabLayout.setupWithViewPager(viewPager);

		loadingBar = findViewById(R.id.pb_loading_indicator);
	}

	@Override
	public void onBackPressed() {
		SharedPreferencesSaver.setLastTeamId(this,null);
		SharedPreferencesSaver.setLastTeamName(this,null);
		super.onBackPressed();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.team_menu,menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		ClipboardManager clipboard = (ClipboardManager)
				this.getSystemService(Context.CLIPBOARD_SERVICE);
		if (item.getItemId() == R.id.copy) {
			Toast.makeText(this, "Your team_id " + SharedPreferencesSaver.getLastTeamID(this) + " has been copied to clipboard",Toast.LENGTH_LONG).show();
			ClipData clip = ClipData.newPlainText("id",SharedPreferencesSaver.getLastTeamID(this));
			clipboard.setPrimaryClip(clip);
		} else if (item.getItemId() == R.id.delete_team) {
			AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
			View mView = getLayoutInflater().inflate(R.layout.dialog_delete_team,null);
			EditText team_name = mView.findViewById(R.id.team_name);
			Button button = mView.findViewById(R.id.delete_team_button);
			button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (team_name.getText().toString().equals(SharedPreferencesSaver.getLastTeamName(TeamActivity.this))) {
						deleteTeamApi();
					} else {
						team_name.setError(getString(R.string.wrong_team_name),null);
						View focusView = team_name;
						focusView.requestFocus();
					}
				}
			});
			mBuilder.setView(mView);
			AlertDialog dialog = mBuilder.create();
			dialog.show();
		}
		return super.onOptionsItemSelected(item);
	}

	private void deleteTeamApi() {
		loadingBar.setVisibility(View.VISIBLE);
		StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_DELETE_TEAM, response -> {
			JSONObject object = null;

			try {
				object = new JSONObject(response);
				Log.d("JSON", object + "JSON");
				if (!object.getBoolean("error")) {
					Toast.makeText(this, R.string.successful_team_deletion,Toast.LENGTH_LONG).show();

					SharedPreferencesSaver.setToken(this,true);

					Intent intent = new Intent(this, DashboardActivity.class);
					SharedPreferencesSaver.setLastTeamName(this,null);
					SharedPreferencesSaver.setLastTeamId(this,null);
					this.startActivity(intent);
					this.finish();

				} else {
					Toast.makeText(this, object.getString("error_msg"),Toast.LENGTH_LONG).show();
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}, error -> {
			Toast.makeText(this,R.string.unknown_error,Toast.LENGTH_LONG).show();
		})
		{
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String,String> params = new HashMap<>();
				params.put("connection_number", SharedPreferencesSaver.getLastTeamID(TeamActivity.this));
				return params;
			}
		};

		RequestQueue requestQueue = Volley.newRequestQueue(this);
		requestQueue.add(stringRequest);
		requestQueue.addRequestFinishedListener((RequestQueue.RequestFinishedListener<String>) request -> {
			if (loadingBar != null) {
				loadingBar.setVisibility(View.INVISIBLE);
			}
		});
	}
}
