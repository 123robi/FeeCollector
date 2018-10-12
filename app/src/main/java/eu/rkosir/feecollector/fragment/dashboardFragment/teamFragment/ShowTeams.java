package eu.rkosir.feecollector.fragment.dashboardFragment.teamFragment;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
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

import eu.rkosir.feecollector.R;
import eu.rkosir.feecollector.helper.ShowTeamsAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowTeams extends Fragment {
	ListView lv;

	SearchView searchView;
	ArrayAdapter<String> adapter;


	public ShowTeams() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		String[]teams = {"ASDASD","ASDASD2","ASDA3SD","ASDAS5D"};
		String[]ids = {"123","124","1245","16212"};
		ClipboardManager clipboard = (ClipboardManager)
				getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
		View view = inflater.inflate(R.layout.fragment_show_teams, container, false);
		lv = view.findViewById(R.id.teamsList);
		ShowTeamsAdapter teamsAdapter = new ShowTeamsAdapter(getActivity(), teams,ids);
		lv.setAdapter(teamsAdapter);

		lv.setOnItemClickListener((adapterView, view1, position, l) -> {
			Toast.makeText(getActivity(), "Your team_id" + ids[position] + "has been copied to clipboard",Toast.LENGTH_LONG).show();
			ClipData clip = ClipData.newPlainText("id", ids[position]);
			clipboard.setPrimaryClip(clip);
		});
		return view;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

		super.onViewCreated(view, savedInstanceState);
	}
}
