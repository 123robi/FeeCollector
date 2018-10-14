package eu.rkosir.feecollector.fragment.teamManagementFragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.rkosir.feecollector.R;
import eu.rkosir.feecollector.activity.teamManagement.AddFee;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddFeeToMember extends Fragment {

	private FloatingActionButton addFee;

	public AddFeeToMember() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_add_fee_to_member, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		addFee = view.findViewById(R.id.add_fee);
		addFee.setOnClickListener(v -> {
			Intent intent = new Intent(getActivity(), AddFee.class);
			startActivity(intent);
		});
	}
}
