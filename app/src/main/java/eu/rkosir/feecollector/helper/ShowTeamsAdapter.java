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


import eu.rkosir.feecollector.R;

public class ShowTeamsAdapter extends ArrayAdapter<String>{
	private String[] teams;
	private String[] team_id;
	private Context context;

	public ShowTeamsAdapter(@NonNull Context context, String[] teams, String[] team_id) {
		super(context, R.layout.listview_team);
		this.context = context;
		this.teams = teams;
		this.team_id = team_id;
	}

	@Override
	public int getCount() {
		return teams.length;
	}

	@NonNull
	@Override
	public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
		ViewHolder viewHolder = new ViewHolder();
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.listview_team, parent, false);
			viewHolder.teamName = convertView.findViewById(R.id.team_name);
			viewHolder.teamId = convertView.findViewById(R.id.team_id);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
			viewHolder.teamName.setText(teams[position]);
			viewHolder.teamId.setText(team_id[position]);


		return convertView;
	}
	static class ViewHolder {
		TextView teamName;
		TextView teamId;
	}
}
