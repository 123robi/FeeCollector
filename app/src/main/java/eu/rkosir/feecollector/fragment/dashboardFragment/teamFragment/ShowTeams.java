package eu.rkosir.feecollector.fragment.dashboardFragment.teamFragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;

import eu.rkosir.feecollector.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowTeams extends Fragment {
	ListView lv;

	SearchView searchView;
	ArrayAdapter<String> adapter;
	ArrayList<String> data = new ArrayList<>();


	public ShowTeams() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_show_teams, container, false);
		lv = view.findViewById(R.id.teamsList);
		adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1,data);
		lv.setAdapter(adapter);
		data.add("Test");
		data.add("Test2");
		data.add("Test3");
		lv.setOnItemClickListener((adapterView, view1, position, l) -> {
			String s = data.get(position);
			Toast.makeText(getActivity(), s,Toast.LENGTH_LONG).show();
		});
		return view;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

		super.onViewCreated(view, savedInstanceState);
	}
}
