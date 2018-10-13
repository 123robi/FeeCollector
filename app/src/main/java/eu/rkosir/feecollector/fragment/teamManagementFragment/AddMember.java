package eu.rkosir.feecollector.fragment.teamManagementFragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.rkosir.feecollector.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddMember extends Fragment {


	public AddMember() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_add_member, container, false);
	}

}
