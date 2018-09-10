package com.feecollector.android.feecollector.DashboardActivityFragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.feecollector.android.feecollector.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragmentCompat {


	public SettingsFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		addPreferencesFromResource(R.xml.pref_main);
	}
}
