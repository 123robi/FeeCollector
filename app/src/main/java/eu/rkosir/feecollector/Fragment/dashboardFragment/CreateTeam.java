package eu.rkosir.feecollector.Fragment.dashboardFragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.rkosir.feecollector.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateTeam extends Fragment {


	public CreateTeam() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().setTitle("Join Team");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_create_team, container, false);
	}

}
