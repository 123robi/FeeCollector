package eu.rkosir.feecollector.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import eu.rkosir.feecollector.R;
import eu.rkosir.feecollector.entity.User;
import eu.rkosir.feecollector.helper.Event;

public class ShowEventsAdapter extends RecyclerView.Adapter<ShowEventsAdapter.ViewHolder> {
	private List<Event> events;
	private Context context;
	private ShowEventsAdapter.OnItemClickListener mListener;

	public interface OnItemClickListener {
		void onItemClick(int position);
	}
	public void setOnItemClickListener(ShowEventsAdapter.OnItemClickListener listener) {
		mListener = listener;
	}

	public ShowEventsAdapter(List<Event> events, Context context) {
		this.events = events;
		this.context = context;
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		public ImageView imageView;
		public TextView mEventName;
		public TextView mEventTime;

		public ViewHolder(View itemView, ShowEventsAdapter.OnItemClickListener listener) {
			super(itemView);
			imageView = itemView.findViewById(R.id.privileges);
			mEventName = itemView.findViewById(R.id.event_name);
			mEventTime = itemView.findViewById(R.id.event_time);

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

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_event_item, parent, false);
		ShowEventsAdapter.ViewHolder viewHolder = new ShowEventsAdapter.ViewHolder(v,mListener);
		return viewHolder;
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		holder.mEventName.setText(events.get(position).getName());
		holder.mEventTime.setText(events.get(position).getDescription());

	}

	@Override
	public int getItemCount() {
		return events.size();
	}
}
