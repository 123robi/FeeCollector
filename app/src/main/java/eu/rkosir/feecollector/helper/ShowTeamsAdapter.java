package eu.rkosir.feecollector.helper;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import eu.rkosir.feecollector.R;
import eu.rkosir.feecollector.entity.Team;

public class ShowTeamsAdapter extends RecyclerView.Adapter<ShowTeamsAdapter.ViewHolder> {

	private ArrayList<Team> teams;
	private OnItemClickListener mListener;

	public interface OnItemClickListener {
		void onItemClick(int position);
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		mListener = listener;
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		public ImageView imageView;
		public TextView textView;
		public TextView countMembers;

		public ViewHolder(View itemView, OnItemClickListener listener) {
			super(itemView);
			imageView = itemView.findViewById(R.id.privileges);
			textView = itemView.findViewById(R.id.team_name);
			countMembers = itemView.findViewById(R.id.count);

			itemView.setOnClickListener(view -> {
				if (listener != null) {
					int position = getAdapterPosition();
					if (position != RecyclerView.NO_POSITION) {
						listener.onItemClick(position );
					}
				}
			});
		}
	}

	public ShowTeamsAdapter(ArrayList<Team> teams) {
		this.teams = teams;
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_team, parent, false);
		ViewHolder viewHolder = new ViewHolder(v,mListener);
		return viewHolder;
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		Team currentTeam = teams.get(position);

		if (currentTeam.isAdmin()) {
			holder.imageView.setImageResource(R.drawable.team_admin_image);
			holder.countMembers.append(" " + currentTeam.getMembers());
		} else {
			holder.imageView.setImageResource(R.drawable.team_member_image);
		}
		holder.textView.setText(currentTeam.getName());
	}

	@Override
	public int getItemCount() {
		return teams.size();
	}
}
