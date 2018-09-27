package eu.rkosir.feecollector.fragment.dashboardFragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import eu.rkosir.feecollector.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

	private Button createTeam;

	public MainFragment() {
		// Required empty public constructor
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initialize();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_main, container, false);
	}

	private void initialize() {
		createTeam = getView().findViewById(R.id.createTeam);
		createTeam.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				getFragmentManager().beginTransaction().replace(R.id.fragment_container, new CreateTeam()).addToBackStack(null).commit();
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().setTitle(R.string.app_name);
	}
}
