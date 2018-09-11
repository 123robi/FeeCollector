package com.feecollector.android.feecollector.Activity;

import android.preference.PreferenceActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.feecollector.android.feecollector.Fragment.SettingsFragment.MainPreferenceFragment;
import com.feecollector.android.feecollector.R;

public class SettingsActivity extends PreferenceActivity {
	private Toolbar toolbar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		toolbar = findViewById(R.id.back_action_bar);
		toolbar.setTitle(R.string.settings);
		toolbar.setNavigationOnClickListener(view -> onBackPressed());

		getFragmentManager().beginTransaction().replace(R.id.content,new MainPreferenceFragment()).commit();
	}
}
