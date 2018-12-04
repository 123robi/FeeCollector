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
import com.github.clans.fab.FloatingActionMenu;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import eu.rkosir.feecollector.AppConfig;
import eu.rkosir.feecollector.R;
import eu.rkosir.feecollector.activity.DashboardActivity;
import eu.rkosir.feecollector.fragment.teamManagementFragment.AddFeeToMember;
import eu.rkosir.feecollector.fragment.teamManagementFragment.Events;
import eu.rkosir.feecollector.fragment.teamManagementFragment.ShowMembers;
import eu.rkosir.feecollector.fragment.teamManagementFragment.Summary;
import eu.rkosir.feecollector.helper.SharedPreferencesSaver;
import eu.rkosir.feecollector.adapters.ViewPagerAdapter;
import eu.rkosir.feecollector.helper.VolleySingleton;

import static com.facebook.FacebookSdk.getApplicationContext;

public class TeamActivity extends AppCompatActivity {

	private TabLayout mTabLayout;
	private ViewPager mViewPager;
	private Toolbar mToolbar;
	private ViewPagerAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_team);
		mToolbar = findViewById(R.id.back_action_bar);
		mToolbar.setTitle(SharedPreferencesSaver.getLastTeamName(this));
		setSupportActionBar(mToolbar);
		mToolbar.setNavigationOnClickListener(view -> {
			onBackPressed();
		});

		mTabLayout = findViewById(R.id.navigation_top);
		mViewPager = findViewById(R.id.content);

		mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
		mAdapter.addFragment(new Summary(), "");
		mAdapter.addFragment(new Events(), "");
		if (SharedPreferencesSaver.isAdmin(getApplicationContext())) {
			mAdapter.addFragment(new AddFeeToMember(),"");
		}
		mAdapter.addFragment(new ShowMembers(), "");

		mViewPager.setAdapter(mAdapter);
		mTabLayout.setupWithViewPager(mViewPager);

		if (SharedPreferencesSaver.isAdmin(getApplicationContext())) {
			mViewPager.setOffscreenPageLimit(4);
			mTabLayout.getTabAt(0).setIcon(R.drawable.ic_home_white_24dp);
			mTabLayout.getTabAt(1).setIcon(R.drawable.ic_event_white_24dp);
			mTabLayout.getTabAt(2).setIcon(R.drawable.ic_attach_money_white_24dp);
			mTabLayout.getTabAt(3).setIcon(R.drawable.ic_person_white_24dp);
		} else {
			mViewPager.setOffscreenPageLimit(3);
			mTabLayout.getTabAt(0).setIcon(R.drawable.ic_home_white_24dp);
			mTabLayout.getTabAt(1).setIcon(R.drawable.ic_event_white_24dp);
			mTabLayout.getTabAt(2).setIcon(R.drawable.ic_person_white_24dp);
		}
	}

	/**
	 * Set team_id and team_name in shared preferences to null, as you are exiting the team activity
	 */
	@Override
	public void onBackPressed() {
		SharedPreferencesSaver.setLastTeamId(this,null);
		SharedPreferencesSaver.setLastTeamName(this,null);
		SharedPreferencesSaver.setCurrencyCode(this,null);
		SharedPreferencesSaver.setCurrencySymbol(this,null);
		SharedPreferencesSaver.setAdmin(this,false);
		super.onBackPressed();
	}

	/**
	 * Inflate a menu with team_menu
	 * @param menu
	 * @return
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.team_menu,menu);
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * * Depends on the click in the toolbar you either copy a team_id or open a AlertDialog where you can delete a team
	 * @param item
	 * @return boolean
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.delete_team) {
			AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
			View mView = getLayoutInflater().inflate(R.layout.dialog_delete_team,null);
			EditText team_name = mView.findViewById(R.id.team_name);
			Button button = mView.findViewById(R.id.delete_team_button);
			button.setOnClickListener(v -> {
				if (team_name.getText().toString().equals(SharedPreferencesSaver.getLastTeamName(TeamActivity.this))) {
					deleteTeamApi();
				} else {
					team_name.setError(getString(R.string.team_wrong_team_name),null);
					View focusView = team_name;
					focusView.requestFocus();
				}
			});
			mBuilder.setView(mView);
			AlertDialog dialog = mBuilder.create();
			dialog.show();
		} else if (item.getItemId() == R.id.refresh) {
			mAdapter.getItemPosition(mAdapter.getItem(mViewPager.getCurrentItem()));
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Sending a Volley Post Request to delete a team using 1 parameter: connection_number
	 */
	private void deleteTeamApi() {
		StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_DELETE_TEAM, response -> {
			JSONObject object = null;

			try {
				object = new JSONObject(response);
				if (!object.getBoolean("error")) {
					Toast.makeText(this, R.string.toast_successful_team_deletion,Toast.LENGTH_LONG).show();

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
			Toast.makeText(this,R.string.toast_unknown_error,Toast.LENGTH_LONG).show();
		})
		{
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String,String> params = new HashMap<>();
				params.put("connection_number", SharedPreferencesSaver.getLastTeamID(TeamActivity.this));
				return params;
			}
		};

		RequestQueue requestQueue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();
		requestQueue.add(stringRequest);
	}
}
