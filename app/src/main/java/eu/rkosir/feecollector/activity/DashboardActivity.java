package eu.rkosir.feecollector.activity;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import eu.rkosir.feecollector.activity.teamManagement.TeamActivity;
import eu.rkosir.feecollector.fragment.dashboardFragment.CreateTeam;
import eu.rkosir.feecollector.fragment.dashboardFragment.MainFragment;
import eu.rkosir.feecollector.R;
import eu.rkosir.feecollector.fragment.dashboardFragment.ShowTeams;
import eu.rkosir.feecollector.fragment.teamManagementFragment.AddFeeToMember;
import eu.rkosir.feecollector.helper.JsonObjectConverter;
import eu.rkosir.feecollector.helper.SharedPreferencesSaver;

public class DashboardActivity extends AppCompatActivity {

	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mToggle;
	private NavigationView mNavigationView;
	private TextView mHeader_username;
	private TextView mHeader_email;
	private ImageView mHeader_picture;
	private JSONObject mRespone;
	private Toolbar mToolbar;
	private JsonObjectConverter mConverter;
	private FragmentManager mFragmentManager;


	/**
	 * if team_id in shared PReferences is not null go to Team activity, if there is no fragment displayed than display MainFragment
	 *
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (SharedPreferencesSaver.getLastTeamID(this) != null) {
			Intent intent = new Intent(this, TeamActivity.class);
			startActivity(intent);
		}
		setContentView(R.layout.activity_dashboard);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new MainFragment(), "dashboard").commit();
		}
		initialize();
		mConverter = new JsonObjectConverter(SharedPreferencesSaver.getUser(this));
		setNavigationView();
		setUserProfile(mConverter);

	}

	/**
	 * On backpress
	 * If drawer is open close it
	 * if is not instance of Main Fragment set checked item to main fragment
	 * if is instance of main Fragment then show alert dialog
	 */
	@Override
	public void onBackPressed() {
		if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
			mDrawerLayout.closeDrawer(GravityCompat.START);
		} else if (!(getCurrentFragment() instanceof MainFragment)) {
			mNavigationView.setCheckedItem(R.id.dashboard);
			super.onBackPressed();
		} else if (getCurrentFragment() instanceof MainFragment) {
			new AlertDialog.Builder(this)
					.setTitle(R.string.dialog_closing_application_title)
					.setMessage(R.string.dialog_closing_application_message)
					.setPositiveButton(R.string.dialog_yes_button, (dialogInterface, i) -> finish())
					.setNegativeButton(R.string.dialog_no_button, null)
					.show();
		}
		else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(mToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void initialize() {
		mToolbar = findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);

		mDrawerLayout = findViewById(R.id.activity_dashboard);
		mToggle = new ActionBarDrawerToggle(this,mDrawerLayout,mToolbar,R.string.dashboard_open,R.string.dashboard_close);
		mDrawerLayout.addDrawerListener(mToggle);
		mToggle.syncState();

		mNavigationView = findViewById(R.id.nav_view);
		mNavigationView.setCheckedItem(R.id.dashboard);
		mNavigationViewListener();

		if (!SharedPreferencesSaver.getLogin(this)) {
			findViewById(R.id.information_header).setVisibility(View.GONE);
		} else {
			TextView textView = findViewById(R.id.information_body);
			textView.setText(R.string.information_addPassword);
			textView.setOnClickListener(view -> {
				Intent intent = new Intent(DashboardActivity.this, ChangePasswordActivity.class);
				startActivity(intent);
			});
		}

		mFragmentManager = getSupportFragmentManager();
	}

	/**
	 * Prepare naviagtion view for inflation
	 */
	private void setNavigationView() {
		View header = LayoutInflater.from(this).inflate(R.layout.navigation_header,null);
		mNavigationView.addHeaderView(header);

		mHeader_username = header.findViewById(R.id.header_username);
		mHeader_email = header.findViewById(R.id.header_email);
		mHeader_picture = header.findViewById(R.id.header_profile_pic);
	}

	/**
	 * get a jsonData a inflate a navigation header
	 * @param jsondata
	 */
	private void setUserProfile(JsonObjectConverter jsondata) {
		JSONObject pic_data, pic_url = null;
			try {
				if (!jsondata.getString("facebook_json").equals("null")) {
					mRespone = new JSONObject(jsondata.getString("facebook_json"));
					mHeader_username.setText(mRespone.get("name").toString());
					mHeader_email.setText(mRespone.get("email").toString());
					pic_data = new JSONObject(mRespone.get("picture").toString());
					pic_url = new JSONObject(pic_data.getString("data"));
					Picasso.get().load(pic_url.getString("url")).into(mHeader_picture);
				} else {
					mHeader_username.setText(jsondata.getString("name"));
					mHeader_email.setText(jsondata.getString("email"));
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
	}

	/**
	 * check for any clicks in the navigation drawer and link it to other activities/fragments
	 */
	private void mNavigationViewListener() {
		mNavigationView.setNavigationItemSelectedListener(item -> {
			int id = item.getItemId();
			Fragment fragment = null;

			switch (id) {
				case R.id.dashboard: {
					if (getCurrentFragment() instanceof MainFragment) {
						mDrawerLayout.closeDrawer(GravityCompat.START);
					} else {
						mFragmentManager.popBackStackImmediate(0, FragmentManager.POP_BACK_STACK_INCLUSIVE);
					}
					break;
				}
				case R.id.create_team: {
					if (getCurrentFragment() instanceof CreateTeam) {
						mDrawerLayout.closeDrawer(GravityCompat.START);
					} else {
						mFragmentManager.popBackStackImmediate(0, FragmentManager.POP_BACK_STACK_INCLUSIVE);
						fragment = new CreateTeam();
						loadFragment(fragment);
					}
					break;
				}
				case R.id.teams: {
					if (getCurrentFragment() instanceof ShowTeams) {
						mDrawerLayout.closeDrawer(GravityCompat.START);
					} else {
						mFragmentManager.popBackStackImmediate(0, FragmentManager.POP_BACK_STACK_INCLUSIVE);
						fragment = new ShowTeams();
						loadFragment(fragment);
					}
					break;
				}
				case R.id.settings: {
					Intent intent = new Intent(DashboardActivity.this, SettingsActivity.class);
					startActivity(intent);
					break;
				}
				case R.id.log_out: {
					if(AccessToken.getCurrentAccessToken() != null) {
						LoginManager.getInstance().logOut();
					}
					//claring SharedPReferences after logout
					SharedPreferencesSaver.clearUser(this);
					SharedPreferencesSaver.setToken(DashboardActivity.this,false);

					Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
					startActivity(intent);
					finish();
					break;
				}
			}
			mDrawerLayout.closeDrawer(GravityCompat.START);

			return true;
		});
	}

	/**
	 * check which fragment is visibale and check it in drawer
	 */
	@Override
	protected void onResume() {
		if (getCurrentFragment() instanceof  MainFragment) {
			mNavigationView.setCheckedItem(R.id.dashboard);
		} else if (getCurrentFragment() instanceof  CreateTeam) {
			mNavigationView.setCheckedItem(R.id.create_team);
		} else if (getCurrentFragment() instanceof AddFeeToMember) {
			mNavigationView.setCheckedItem(R.id.add_fee);
		}
		super.onResume();
	}

	private boolean loadFragment(Fragment fragment) {
		//switching fragment
		if (fragment != null) {
			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.fragment_container, fragment)
					.addToBackStack(null)
					.commit();
			return true;
		}
		return false;
	}

	private Fragment getCurrentFragment() {
		Fragment currentFragment = mFragmentManager.findFragmentById(R.id.fragment_container);

		return currentFragment;
	}
}
