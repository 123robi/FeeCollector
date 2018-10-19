package eu.rkosir.feecollector.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import eu.rkosir.feecollector.R;
import eu.rkosir.feecollector.entity.User;

public class ShowMembersAdapter extends RecyclerView.Adapter<ShowMembersAdapter.ViewHolder>{
	private ArrayList<User> users;
	private Context context;
	private ShowTeamsAdapter.OnItemClickListener mListener;

	public interface OnItemClickListener {
		void onItemClick(int position);
	}

	public void setOnItemClickListener(ShowTeamsAdapter.OnItemClickListener listener) {
		mListener = listener;
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		private TextView mMembers;
		private TextView mRole;

		public ViewHolder(View itemView, ShowTeamsAdapter.OnItemClickListener listener) {
			super(itemView);
			mMembers = itemView.findViewById(R.id.members);
			mRole = itemView.findViewById(R.id.role);

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

	public ShowMembersAdapter(ArrayList<User> users, Context context) {
		this.users = users;
		this.context = context;
	}

	@NonNull
	@Override
	public ShowMembersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_member_item, parent, false);
		ShowMembersAdapter.ViewHolder viewHolder = new ShowMembersAdapter.ViewHolder(v,mListener);
		return viewHolder;
	}

	@Override
	public void onBindViewHolder(@NonNull ShowMembersAdapter.ViewHolder holder, int position) {
		holder.mMembers.setText(users.get(position).getName());
		if (users.get(position).getRole() != null) {
			holder.mRole.setText(users.get(position).getRole());
		}
	}

	@Override
	public int getItemCount() {
		return users.size();
	}
}
