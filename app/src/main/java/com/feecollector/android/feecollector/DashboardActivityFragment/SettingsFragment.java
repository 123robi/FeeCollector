package com.feecollector.android.feecollector.DashboardActivityFragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
<<<<<<< Updated upstream
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
=======
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;
>>>>>>> Stashed changes

import com.feecollector.android.feecollector.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

<<<<<<< Updated upstream

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings,container,false);
    }
=======
	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		Log.d("KEY", rootKey + "KEY");
		setPreferencesFromResource(R.xml.pref_main, rootKey);
	}

	@Override
	public Fragment getCallbackFragment() {
		return this;
	}
>>>>>>> Stashed changes
}
