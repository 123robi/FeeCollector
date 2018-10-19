package eu.rkosir.feecollector.fragment.teamManagementFragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import eu.rkosir.feecollector.AppConfig;
import eu.rkosir.feecollector.R;
import eu.rkosir.feecollector.activity.teamManagement.calendar.AddEvent;
import eu.rkosir.feecollector.entity.Fee;
import eu.rkosir.feecollector.entity.User;
import eu.rkosir.feecollector.helper.Event;
import eu.rkosir.feecollector.helper.SharedPreferencesSaver;
import eu.rkosir.feecollector.helper.VolleySingleton;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;

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
		getEvents();
		return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ADD_NOTE && resultCode == RESULT_OK) {
			Event myEventDay = data.getParcelableExtra(RESULT);
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

	private void getEvents() {
		String uri = String.format(AppConfig.URL_GET_EVENTS,
				SharedPreferencesSaver.getLastTeamID(getApplicationContext()));
		StringRequest stringRequest = new StringRequest(Request.Method.GET, uri, response -> {
			JSONObject object = null;
			try {
				object = new JSONObject(response);
				JSONArray eventsArray = object.getJSONArray("events");
				for(int i = 0; i < eventsArray.length(); i++) {
					JSONObject event = eventsArray.getJSONObject(i);
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(AppConfig.df.parse(event.getString("date")));
					Event addingEvent = new Event(calendar,event.getString("name"),event.getString("description"),R.drawable.ic_add_black_24dp);
					try {
						mCalendarView.setDate(addingEvent.getCalendar());
					} catch (OutOfDateRangeException e) {
						e.printStackTrace();
					}
					mEventDays.add(addingEvent);
					mCalendarView.setEvents(mEventDays);

				}
			} catch (JSONException e) {
				Toast.makeText(getActivity(),R.string.toast_unknown_error,Toast.LENGTH_LONG).show();
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}, error -> {
			Toast.makeText(getActivity(),R.string.toast_unknown_error,Toast.LENGTH_LONG).show();
		});

		RequestQueue requestQueue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();
		requestQueue.add(stringRequest);
		requestQueue.addRequestFinishedListener((RequestQueue.RequestFinishedListener<String>) request -> {
		});
	}
}
