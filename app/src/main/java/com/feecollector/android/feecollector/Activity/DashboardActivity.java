package com.feecollector.android.feecollector.Activity;

import android.app.FragmentManager;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
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
import com.feecollector.android.feecollector.Helper.JsonObjectConverter;
import com.feecollector.android.feecollector.Helper.SharedPreferencesSaver;
import com.feecollector.android.feecollector.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);
		initialize();
		if (SharedPreferencesSaver.getUser(this) != null) {
			converter = new JsonObjectConverter(SharedPreferencesSaver.getUser(this));

			String jsonData = converter.getString("facebook_json");
			if (!(jsonData == null || jsonData.equals(""))) {
				setNavigationView();
				setUserProfile(jsonData);
			}
		}
	}

	@Override
	public void onBackPressed() {
		if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
			drawerLayout.closeDrawer(GravityCompat.START);
		} else {
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
		if (jsondata == null || jsondata.equals("")){
			return;
		}
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
				FragmentManager fragmentManager = getFragmentManager();
			int id = item.getItemId();

			if(id == R.id.log_out) {
				if(AccessToken.getCurrentAccessToken() != null) {
					LoginManager.getInstance().logOut();
				}
				//claring SharedPReferences after logout
				SharedPreferencesSaver.clearUser(this);
				SharedPreferencesSaver.setToken(DashboardActivity.this,false);

				Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
				startActivity(intent);
				finish();
			} else if(id == R.id.settings) {
				Intent intent = new Intent(DashboardActivity.this, SettingsActivity.class);
				startActivity(intent);
			}

			drawerLayout.closeDrawer(GravityCompat.START);

			return true;
		});
	}

	@Override
	protected void onResume() {
		navigationView.setCheckedItem(R.id.dashboard);
		super.onResume();
	}
}
