package com.feecollector.android.feecollector.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.feecollector.android.feecollector.Helper.TokenSaver;
import com.feecollector.android.feecollector.R;

public class DashboardActivity extends AppCompatActivity {

	private Button logout;

	private DrawerLayout drawerLayout;
	private ActionBarDrawerToggle toggle;
	private NavigationView navigationView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);
		/**
		 * Navigation bar configuration
		 */
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
					TokenSaver.setToken(DashboardActivity.this,false);
					Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
					startActivity(intent);
					finish();
				}
				return true;
			}
		});

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(toggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
