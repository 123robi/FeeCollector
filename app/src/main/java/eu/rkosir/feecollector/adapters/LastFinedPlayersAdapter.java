package eu.rkosir.feecollector.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import eu.rkosir.feecollector.R;
import eu.rkosir.feecollector.helper.SharedPreferencesSaver;

public class LastFinedPlayersAdapter extends RecyclerView.Adapter<LastFinedPlayersAdapter.ViewHolder> {
	private List<String> fees_name, costs, users_name, users_email;
	private LinkedHashMap<String, String> users,fees;
	private LastFinedPlayersAdapter.OnItemClickListener mListener;
	private Context context;
	public interface OnItemClickListener {
		void onItemClick(int position);
	}
	public void setOnItemClickListener(LastFinedPlayersAdapter.OnItemClickListener listener) {
		mListener = listener;
	}

	public LastFinedPlayersAdapter(LinkedHashMap<String, String> users, LinkedHashMap<String, String> fees, Context context) {
		this.users = users;
		this.fees = fees;
		this.fees_name = new ArrayList<>(fees.keySet());
		this.costs = new ArrayList<>(fees.values());
		this.users_name = new ArrayList<>(users.keySet());
		this.users_email = new ArrayList<>(users.values());
		this.context = context;
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		public TextView mName;
		public TextView mFeeName;
		public TextView mCost;
		public CircleImageView circleImageView;

		public ViewHolder(View itemView, LastFinedPlayersAdapter.OnItemClickListener listener) {
			super(itemView);
			mName = itemView.findViewById(R.id.name);
			mFeeName = itemView.findViewById(R.id.fee_name);
			mCost = itemView.findViewById(R.id.cost);
			circleImageView = itemView.findViewById(R.id.user_picture);
		}
	}

	@NonNull
	@Override
	public LastFinedPlayersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_last_fined_users, parent, false);
		LastFinedPlayersAdapter.ViewHolder viewHolder = new LastFinedPlayersAdapter.ViewHolder(v,mListener);
		return viewHolder;
	}

	@Override
	public void onBindViewHolder(@NonNull LastFinedPlayersAdapter.ViewHolder holder, int position) {
		holder.mFeeName.setText(fees_name.get(position));
		holder.mName.setText(users_name.get(position));
		holder.mCost.setText(costs.get(position) + SharedPreferencesSaver.getCurrencySymbol(context));
		String imageUrl = "https://rkosir.eu/images/" + users_email.get(position) + ".jpg";
		Picasso.get().load(imageUrl).resize(200,200).networkPolicy(NetworkPolicy.NO_CACHE).error(R.mipmap.ic_team_member_no_photo).into(holder.circleImageView);
	}

	@Override
	public int getItemCount() {
		return users.size();
	}
}