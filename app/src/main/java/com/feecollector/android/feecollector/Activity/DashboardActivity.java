package com.feecollector.android.feecollector.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.feecollector.android.feecollector.AppConfig;
import com.feecollector.android.feecollector.Helper.FacebookJsonSaver;
import com.feecollector.android.feecollector.Helper.TokenSaver;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);

		String jsonData = getIntent().getStringExtra(AppConfig.FACEBOOK_DETAILS);
		if (!(jsonData == null || jsonData.equals(""))) {
			initialize();
			setNavigationView();
			setUserProfile(jsonData);
		} else if(!(FacebookJsonSaver.getJson(DashboardActivity.this) == null)){
			initialize();
			setNavigationView();
			setUserProfile(FacebookJsonSaver.getJson(DashboardActivity.this));
		} else {
			initialize();
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
		drawerLayout = findViewById(R.id.activity_dashboard);
		toggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
		drawerLayout.addDrawerListener(toggle);
		toggle.syncState();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		navigationView = findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(@NonNull MenuItem item) {
				if(item.getItemId() == R.id.log_out) {
					if(AccessToken.getCurrentAccessToken() != null) {
						LoginManager.getInstance().logOut();
					}
					//claring SharedPReferences after logout
					FacebookJsonSaver.clear(DashboardActivity.this);
					TokenSaver.setToken(DashboardActivity.this,false);

					Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
					startActivity(intent);
					finish();
				}
				return true;
			}
		});
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
}
