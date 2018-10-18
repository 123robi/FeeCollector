package eu.rkosir.feecollector.activity;

import android.preference.PreferenceActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import eu.rkosir.feecollector.fragment.settingsFragment.MainPreferenceFragment;
import eu.rkosir.feecollector.R;


public class SettingsActivity extends PreferenceActivity {
	private Toolbar mToolbar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		mToolbar = findViewById(R.id.back_action_bar);
		mToolbar.setTitle(R.string.settings);
		mToolbar.setNavigationOnClickListener(view -> onBackPressed());

		getFragmentManager().beginTransaction().replace(R.id.content,new MainPreferenceFragment()).commit();
	}
}
