package eu.rkosir.feecollector.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import eu.rkosir.feecollector.R;
import eu.rkosir.feecollector.entity.User;
import eu.rkosir.feecollector.helper.SharedPreferencesSaver;

public class ShowMembersAdapter extends RecyclerView.Adapter<ShowMembersAdapter.ViewHolder> {
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
		private TextView mCostAmount;
		private CircleImageView mImage;

		public ViewHolder(View itemView, ShowTeamsAdapter.OnItemClickListener listener) {
			super(itemView);
			mMembers = itemView.findViewById(R.id.members);
			mRole = itemView.findViewById(R.id.role);
			mImage = itemView.findViewById(R.id.image);
			mCostAmount = itemView.findViewById(R.id.cost_amount);

			itemView.setOnClickListener(view -> {
				if (listener != null) {
					int position = getAdapterPosition();
					if (position != RecyclerView.NO_POSITION) {
						listener.onItemClick(position);
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
		ShowMembersAdapter.ViewHolder viewHolder = new ShowMembersAdapter.ViewHolder(v, mListener);
		return viewHolder;
	}

	@Override
	public void onBindViewHolder(@NonNull ShowMembersAdapter.ViewHolder holder, int position) {
		holder.mMembers.setText(users.get(position).getName());
		if (users.get(position).getRole() != null) {
			holder.mRole.setText(users.get(position).getRole());
		}
		holder.mCostAmount.setText(users.get(position).getToPay() + "" + SharedPreferencesSaver.getCurrencySymbol(context));
		String imageUrl = "https://rkosir.eu/images/" + users.get(position).getEmail() + ".jpg";
		Picasso.get().load(imageUrl).resize(200,200).error(R.mipmap.ic_team_member_no_photo).into(holder.mImage);
	}

	@Override
	public int getItemCount() {
		return users.size();
	}
}
