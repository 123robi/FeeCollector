package eu.rkosir.feecollector.fragment.teamManagementFragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;


import java.util.ArrayList;
import java.util.List;

import eu.rkosir.feecollector.R;
import eu.rkosir.feecollector.activity.teamManagement.calendar.AddEvent;
import eu.rkosir.feecollector.helper.MyEventDay;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class Events extends Fragment {

	public static final String RESULT = "result";
	public static final String EVENT = "event";
	private static final int ADD_NOTE = 44;

	private FloatingActionButton mAddEvent;
	private CalendarView mCalendarView;
	private List<EventDay> mEventDays = new ArrayList<>();

	public Events() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_events, container, false);
		mAddEvent = view.findViewById(R.id.add_fee);
		mCalendarView = view.findViewById(R.id.calendarView);
		mAddEvent.setOnClickListener(view1 -> addEvent());
		return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ADD_NOTE && resultCode == RESULT_OK) {
			MyEventDay myEventDay = data.getParcelableExtra(RESULT);
			try {
				mCalendarView.setDate(myEventDay.getCalendar());
			} catch (OutOfDateRangeException e) {
				e.printStackTrace();
			}
			mEventDays.add(myEventDay);
			mCalendarView.setEvents(mEventDays);
		}
	}
	private void addEvent() {
		Intent intent = new Intent(getContext(), AddEvent.class);
		startActivityForResult(intent, ADD_NOTE);
	}
}
