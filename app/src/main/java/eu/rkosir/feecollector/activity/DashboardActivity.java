package eu.rkosir.feecollector.activity;

import android.content.DialogInterface;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;

import eu.rkosir.feecollector.fragment.dashboardFragment.AddFee;
import eu.rkosir.feecollector.fragment.dashboardFragment.CreateTeam;
import eu.rkosir.feecollector.fragment.dashboardFragment.MainFragment;
import eu.rkosir.feecollector.R;
import eu.rkosir.feecollector.helper.JsonObjectConverter;
import eu.rkosir.feecollector.helper.SharedPreferencesSaver;

public class DashboardActivity extends AppCompatActivity {

	private DrawerLayout drawerLayout;
	private ActionBarDrawerToggle toggle;
	private NavigationView navigationView;
	private TextView header_username;
	private TextView header_email;
	private ImageView header_picture;
	private JSONObject respone;
	private Toolbar toolbar;
	private JsonObjectConverter converter;
	private FragmentManager fragmentManager;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new MainFragment(), "dashboard").commit();
		}
		initialize();
		if (SharedPreferencesSaver.getUser(this) != null) {
			converter = new JsonObjectConverter(SharedPreferencesSaver.getUser(this));

			String jsonData = converter.getString("facebook_json");
			if (jsonData != null && !jsonData.equals("null")) {
				setNavigationView();
				setUserProfile(jsonData);
			}
		}
	}

	@Override
	public void onBackPressed() {
		if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
			drawerLayout.closeDrawer(GravityCompat.START);
		} else if (!(getCurrentFragment() instanceof MainFragment)) {
			navigationView.setCheckedItem(R.id.dashboard);
			super.onBackPressed();
		} else if (getCurrentFragment() instanceof MainFragment) {
			new AlertDialog.Builder(this)
					.setTitle(R.string.closing_application_title)
					.setMessage(R.string.closing_application_message)
					.setPositiveButton(R.string.yes_button, (dialogInterface, i) -> finish())
					.setNegativeButton(R.string.no_button, null)
					.show();
		}
		else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(toggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void initialize() {
		toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		drawerLayout = findViewById(R.id.activity_dashboard);
		toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
		drawerLayout.addDrawerListener(toggle);
		toggle.syncState();

		navigationView = findViewById(R.id.nav_view);
		navigationView.setCheckedItem(R.id.dashboard);
		navigationViewListener();

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

		fragmentManager = getSupportFragmentManager();
	}

	private void setNavigationView() {
		View header = LayoutInflater.from(this).inflate(R.layout.navigation_header,null);
		navigationView.addHeaderView(header);

		header_username = header.findViewById(R.id.header_username);
		header_email = header.findViewById(R.id.header_email);
		header_picture = header.findViewById(R.id.header_profile_pic);
	}

	private void setUserProfile(String jsondata) {
		JSONObject pic_data, pic_url = null;
		try {
			respone = new JSONObject(jsondata);
			header_username.setText(respone.get("name").toString());
			header_email.setText(respone.get("email").toString());
			pic_data = new JSONObject(respone.get("picture").toString());
			pic_url = new JSONObject(pic_data.getString("data"));
			Picasso.get().load(pic_url.getString("url")).into(header_picture);

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void navigationViewListener() {
		navigationView.setNavigationItemSelectedListener(item -> {
			int id = item.getItemId();
			Fragment fragment = null;

			switch (id) {
				case R.id.dashboard: {
					if (getCurrentFragment() instanceof MainFragment) {
						drawerLayout.closeDrawer(GravityCompat.START);
					} else {
						fragmentManager.popBackStackImmediate(0, FragmentManager.POP_BACK_STACK_INCLUSIVE);
					}
					break;
				}
				case R.id.create_team: {
					if (getCurrentFragment() instanceof CreateTeam) {
						drawerLayout.closeDrawer(GravityCompat.START);
					} else {
						fragmentManager.popBackStackImmediate(0, FragmentManager.POP_BACK_STACK_INCLUSIVE);
						fragment = new CreateTeam();
						loadFragment(fragment);
					}
					break;
				}
				case R.id.add_fee: {
					if (getCurrentFragment() instanceof AddFee) {
						drawerLayout.closeDrawer(GravityCompat.START);
					} else {
						fragmentManager.popBackStackImmediate(0, FragmentManager.POP_BACK_STACK_INCLUSIVE);
						fragment = new AddFee();
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
			drawerLayout.closeDrawer(GravityCompat.START);

			return true;
		});
	}

	@Override
	protected void onResume() {
		if (getCurrentFragment() instanceof  MainFragment) {
			navigationView.setCheckedItem(R.id.dashboard);
		} else if (getCurrentFragment() instanceof  CreateTeam) {
			navigationView.setCheckedItem(R.id.create_team);
		} else if (getCurrentFragment() instanceof  AddFee) {
			navigationView.setCheckedItem(R.id.create_team);
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
		Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);

		return currentFragment;
	}
}
