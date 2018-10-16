package eu.rkosir.feecollector.helper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import java.util.ArrayList;

import eu.rkosir.feecollector.R;
import eu.rkosir.feecollector.User.entity.Team;

public class ShowTeamsAdapter extends ArrayAdapter<String>{
	private  ArrayList<Team> teams;
	private Context context;

	public ShowTeamsAdapter(@NonNull Context context, ArrayList<Team> teams) {
		super(context, R.layout.listview_team);
		this.context = context;
		this.teams = teams;
	}

	@Override
	public int getCount() {
		return teams.size();
	}

	@NonNull
	@Override
	public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
		ViewHolder viewHolder = new ViewHolder();
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.listview_team, parent, false);
			viewHolder.teamName = convertView.findViewById(R.id.team_name);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		viewHolder.teamName.setText(teams.get(position).getName());
		if (teams.get(position).isAdmin()) {
			viewHolder.teamName.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_info_24dp,0);
		} else {
			viewHolder.teamName.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_add_black_24dp,0);
		}


		return convertView;
	}
	static class ViewHolder {
		TextView teamName;
	}
}
