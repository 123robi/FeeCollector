package eu.rkosir.feecollector.Fragment.settingsFragment;


import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceFragment;

import eu.rkosir.feecollector.R;

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
