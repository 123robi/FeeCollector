package eu.rkosir.feecollector.fragment.teamManagementFragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.facebook.internal.LockOnGetVariable;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import eu.rkosir.feecollector.AppConfig;
import eu.rkosir.feecollector.R;
import eu.rkosir.feecollector.activity.teamManagement.calendar.AddEvent;
import eu.rkosir.feecollector.activity.teamManagement.calendar.ShowEvent;
import eu.rkosir.feecollector.adapters.ShowEventsAdapter;
import eu.rkosir.feecollector.entity.Event;
import eu.rkosir.feecollector.entity.Place;
import eu.rkosir.feecollector.helper.SharedPreferencesSaver;
import eu.rkosir.feecollector.helper.VolleySingleton;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class Events extends Fragment {

	public static final String RESULT = "result";
	public static final String RESULTPLACE = "resultplace";
	public static final String EVENT = "event";
	private static final int ADD_NOTE = 44;

	private FloatingActionButton mAddEvent;
	private FloatingActionButton mAddMatch;
	private FloatingActionButton mAddTraining;
	private FloatingActionMenu mMenu;
	private CalendarView mCalendarView;
	private List<EventDay> mEventDays = new ArrayList<>();
	private List<Event> mEvents = new ArrayList<>();
	private List<Place> mPlaces = new ArrayList<>();
	private FrameLayout mFrameLayout;
	private TabLayout mTabLayout;
	private Toolbar mToolbar;
	private RecyclerView mRecyclerView;
	private ShowEventsAdapter mAdapter;
	private RecyclerView.LayoutManager mLayoutManager;
	public Events() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_events, container, false);
		mAddEvent = view.findViewById(R.id.event);
		mAddMatch = view.findViewById(R.id.match);
		mAddTraining = view.findViewById(R.id.training);
		mCalendarView = view.findViewById(R.id.calendarView);
		mMenu = view.findViewById(R.id.float_menu);
		mFrameLayout = view.findViewById(R.id.frame_layout);
		mRecyclerView = view.findViewById(R.id.eventList);

		mTabLayout = getActivity().findViewById(R.id.navigation_top);
		mToolbar = getActivity().findViewById(R.id.back_action_bar);

		mMenu.setOnMenuButtonClickListener(v -> {
			if (mFrameLayout.getVisibility() == View.GONE) {
				openMenu();
			} else {
				closeMenu();
			}
		});

		ViewPager pager = getActivity().findViewById(R.id.content);
		pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				closeMenu();
			}

			@Override
			public void onPageSelected(int position) {
				closeMenu();
			}

			@Override
			public void onPageScrollStateChanged(int state) {
				closeMenu();
			}
		});

		mFrameLayout.setOnClickListener(v -> {
			if(mMenu.isOpened()) {
				closeMenu();
			}
		});


		mAddEvent.setOnClickListener(view1 -> addEvent(Event.EVENT));
		mAddMatch.setOnClickListener(view1 -> addEvent(Event.MATCH));
		mAddTraining.setOnClickListener(view1 -> addEvent(Event.TRANING));

		getEvents();
		getLocations();

		mCalendarView.setOnDayClickListener(eventDay -> {
			List<Event> events = new ArrayList<>();
			for (Event event : mEvents) {
				if(event.getCalendar().getTimeInMillis() == eventDay.getCalendar().getTimeInMillis()) {
					events.add(event);
				}
			}
			mAdapter = new ShowEventsAdapter(events,mPlaces,getApplicationContext());
			mRecyclerView.setAdapter(mAdapter);
			mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
			mAdapter.setOnItemClickListener(position -> {
				Intent intent = new Intent(getApplicationContext(), ShowEvent.class);
				intent.putExtra("event", events.get(position));
				startActivity(intent);
			});
		});
		return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ADD_NOTE && resultCode == RESULT_OK) {
			Event myEventDay = data.getParcelableExtra(RESULT);
			Place myPlace = data.getParcelableExtra(RESULTPLACE);
			try {
				mCalendarView.setDate(myEventDay.getCalendar());
			} catch (OutOfDateRangeException e) {
				e.printStackTrace();
			}
			mEventDays.add(myEventDay);
			mEvents.add(myEventDay);
			mCalendarView.setEvents(mEventDays);
			mPlaces.add(myPlace);
		}
	}
	private void addEvent(String name) {
		Intent intent = new Intent(getContext(), AddEvent.class);
		intent.putExtra("title", name);
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
					calendar.setTime(AppConfig.parse.parse(event.getString("start")));
					Event addingEvent = new Event(
							calendar,event.getString("start"),
							event.getString("end"),
							event.getString("name"),
							event.getString("description"),
							R.drawable.ic_event_available_black_24dp,
							event.getString("place_id")
					);
					try {
						mCalendarView.setDate(addingEvent.getCalendar());
					} catch (OutOfDateRangeException e) {
						e.printStackTrace();
					}
					mEventDays.add(addingEvent);
					mEvents.add(addingEvent);
					mCalendarView.setEvents(mEventDays);

				}
			} catch (JSONException e) {
				Toast.makeText(getApplicationContext(),R.string.toast_unknown_error,Toast.LENGTH_LONG).show();
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}, error -> {
			Toast.makeText(getApplicationContext(),R.string.toast_unknown_error,Toast.LENGTH_LONG).show();
		});

		RequestQueue requestQueue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();
		requestQueue.add(stringRequest);
	}
	/**
	 * Sending a Volley GET Request to get locations using 1 parameter: team_name
	 */
	private void getLocations() {
		String uri = String.format(AppConfig.URL_GET_LOCATIONS,
				SharedPreferencesSaver.getLastTeamID(getApplicationContext()));
		StringRequest stringRequest = new StringRequest(Request.Method.GET, uri, response -> {
			JSONObject object = null;
			try {
				object = new JSONObject(response);
				JSONArray placesArray = object.getJSONArray("places");
				mPlaces = new ArrayList<>();
				mPlaces.add(new eu.rkosir.feecollector.entity.Place(0,getResources().getString(R.string.add_event_new_location),null,"",0));
				for(int i = 0; i < placesArray.length(); i++) {
					JSONObject place = placesArray.getJSONObject(i);
					eu.rkosir.feecollector.entity.Place getPlace = new eu.rkosir.feecollector.entity.Place(
							place.getInt("id"),
							place.getString("name"),
							place.getString("address"),
							place.getString("latlng"),
							place.getInt("team_id"));
					mPlaces.add(getPlace);
				}


			} catch (JSONException e) {
				Toast.makeText(getApplicationContext(),R.string.toast_unknown_error,Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
		}, error -> {
			Toast.makeText(getApplicationContext(),R.string.toast_unknown_error,Toast.LENGTH_LONG).show();
		});

		RequestQueue requestQueue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();
		requestQueue.add(stringRequest);
	}
	private void openMenu() {
		mMenu.open(true);
		mFrameLayout.setVisibility(View.VISIBLE);
		mTabLayout.setAlpha(0.7f);
		mToolbar.setAlpha(0.7f);
	}
	private void closeMenu() {
		mMenu.close(true);
		mFrameLayout.setVisibility(View.GONE);
		mTabLayout.setAlpha(1f);
		mToolbar.setAlpha(1f);
	}
}
