package eu.rkosir.feecollector.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import eu.rkosir.feecollector.AppConfig;
import eu.rkosir.feecollector.R;
import eu.rkosir.feecollector.entity.Event;
import eu.rkosir.feecollector.entity.Place;

public class ShowEventsAdapter extends RecyclerView.Adapter<ShowEventsAdapter.ViewHolder> {
	private List<Event> events;
	private List<Place> places;
	private Context context;
	private ShowEventsAdapter.OnItemClickListener mListener;
	private Place place;

	public interface OnItemClickListener {
		void onItemClick(int position);
	}
	public void setOnItemClickListener(ShowEventsAdapter.OnItemClickListener listener) {
		mListener = listener;
	}

	public ShowEventsAdapter(List<Event> events,List<Place> places, Context context) {
		this.events = events;
		this.places = places;
		this.context = context;
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		public TextView mEventName;
		public TextView mEventTime;
		public TextView mEventLocation;
		public TextView mEventDate;
		public TextView mEventDay;

		public ViewHolder(View itemView, ShowEventsAdapter.OnItemClickListener listener) {
			super(itemView);
			mEventName = itemView.findViewById(R.id.event_name);
			mEventTime = itemView.findViewById(R.id.event_time);
			mEventLocation = itemView.findViewById(R.id.event_location);
			mEventDate = itemView.findViewById(R.id.date);
			mEventDay = itemView.findViewById(R.id.date_name);

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
		if (events.get(position).getName().equals(Event.EVENT)) {
			holder.mEventName.setBackground(context.getDrawable(R.drawable.shape_event));
		} else if (events.get(position).getName().equals(Event.MATCH)) {
			holder.mEventName.setBackground(context.getDrawable(R.drawable.shape_match));
		} else {
			holder.mEventName.setBackground(context.getDrawable(R.drawable.shape_training));
		}
		Calendar calendarStart = Calendar.getInstance();
		Calendar calendarEnd = Calendar.getInstance();
		try {
			calendarStart.setTime(AppConfig.parse.parse(events.get(position).getStartDateTime()));
			calendarEnd.setTime(AppConfig.parse.parse(events.get(position).getEndDateTime()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		holder.mEventTime.setText(String.valueOf(getTimeFormat(calendarStart)));
		holder.mEventTime.append(" - ");
		holder.mEventTime.append(String.valueOf(getTimeFormat(calendarEnd)));
		holder.mEventDay.setText( String.format("%Ta", new Date(events.get(position).getCalendar().getTimeInMillis())));
		holder.mEventDate.setText(String.valueOf(events.get(position).getCalendar().get(Calendar.DATE)));
		for (Place placeCheck : places) {
			if (placeCheck.getId() == Integer.parseInt(events.get(position).getPlaceId())) {
				holder.mEventLocation.setText(placeCheck.getName());
			}
		}
	}

	@Override
	public int getItemCount() {
		return events.size();
	}
	private String getTimeFormat(Calendar c) {
		Date date = c.getTime();
		DateFormat timeFormatter =
				DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());
		return timeFormatter.format(date);
	}
}
