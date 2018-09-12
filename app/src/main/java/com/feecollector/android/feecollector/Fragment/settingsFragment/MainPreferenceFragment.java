package com.feecollector.android.feecollector.Fragment.settingsFragment;


import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceFragment;

import com.feecollector.android.feecollector.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainPreferenceFragment extends PreferenceFragment {
	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref_main);
	}
}
