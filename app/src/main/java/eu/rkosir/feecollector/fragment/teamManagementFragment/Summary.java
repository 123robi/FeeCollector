package eu.rkosir.feecollector.fragment.teamManagementFragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import eu.rkosir.feecollector.AppConfig;
import eu.rkosir.feecollector.R;
import eu.rkosir.feecollector.entity.Event;
import eu.rkosir.feecollector.entity.Place;
import eu.rkosir.feecollector.helper.JsonObjectConverter;
import eu.rkosir.feecollector.helper.MyYAxisValueFormatter;
import eu.rkosir.feecollector.helper.SharedPreferencesSaver;
import eu.rkosir.feecollector.helper.VolleySingleton;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.github.mikephil.charting.utils.ColorTemplate.MATERIAL_COLORS;

/**
 * A simple {@link Fragment} subclass.
 */
public class Summary extends Fragment implements OnMapReadyCallback {

	private Place place;
	private Event nextEvent;
	private GoogleMap mMap;
	private String [] latlngArray;
	private TextView mEventName, mEventDate, mEventTime, mEventDescription, mPlaceName, mTotal, mNotPaid, mPaid;
	private CardView mNextEvent, mMostViolated, mSummary;
	private SwipeRefreshLayout mSwipeRefreshLayout;

	private BarChart mChart;

	public Summary() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_summary, container, false);
		SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);

		mSwipeRefreshLayout = view.findViewById(R.id.swiperefresh);
		mNextEvent = view.findViewById(R.id.next_event_card);
		mMostViolated = view.findViewById(R.id.most_violated_players);
		mSummary = view.findViewById(R.id.summary);
		mEventName = view.findViewById(R.id.event_name);
		mEventDate = view.findViewById(R.id.event_date);
		mEventTime = view.findViewById(R.id.event_time);
		mEventDescription = view.findViewById(R.id.event_description);
		mPlaceName = view.findViewById(R.id.event_location);
		mTotal = view.findViewById(R.id.total);
		mNotPaid = view.findViewById(R.id.notpaid);
		mPaid = view.findViewById(R.id.paid);
		mChart = view.findViewById(R.id.chart);
		if (SharedPreferencesSaver.getIcal(getApplicationContext()) == null || SharedPreferencesSaver.getIcal(getApplicationContext()).equals("null")) {
			mNextEvent.setVisibility(View.VISIBLE);
		} else {
			mNextEvent.setVisibility(View.GONE);
		}
		return view;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (SharedPreferencesSaver.getIcal(getApplicationContext()) == null || SharedPreferencesSaver.getIcal(getApplicationContext()).equals("null")) {
			mSwipeRefreshLayout.setOnRefreshListener(() -> {
				getSummary();
				getMembers();
				getNextEvent();
			});
		} else {
			mSwipeRefreshLayout.setOnRefreshListener(() -> {
				getMembers();
				getSummary();
			});
		}

	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		mMap = googleMap;
		getNextEvent();
		getSummary();

	}

	private void getNextEvent() {
		mSwipeRefreshLayout.setRefreshing(true);
		String uri = String.format(AppConfig.URL_GET_NEXT_EVENT,
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
					nextEvent = new Event(
							calendar,event.getString("start"),
							event.getString("end"),
							event.getString("name"),
							event.getString("description"),
							R.drawable.ic_event_available_black_24dp,
							event.getString("place_id")
					);
					JSONObject matchingData = event.getJSONObject("_matchingData").getJSONObject("Places");
					place = new Place(
							matchingData.getInt("id"),
							matchingData.getString("name"),
							matchingData.getString("address"),
							matchingData.getString("latlng"),
							matchingData.getInt("team_id")
					);
				}
				if (place != null) {
					mMap.getUiSettings().setAllGesturesEnabled(false);
					mMap.getUiSettings().setMapToolbarEnabled(false);
					String lanltd = place.getLatlng().substring(place.getLatlng().indexOf("(")+1, place.getLatlng().indexOf(")"));
					latlngArray = lanltd.split(",");
					LatLng location = new LatLng(Double.parseDouble(latlngArray[0]),Double.parseDouble(latlngArray[1]));
					mMap.addMarker(new MarkerOptions().position(location).title(place.getName()));
					mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15), new GoogleMap.CancelableCallback() {
						@Override
						public void onFinish() {
							getMembers();
						}

						@Override
						public void onCancel() {

						}
					});

					mEventName.setText("Next " + nextEvent.getName());
					mEventDescription.setText(nextEvent.getDescription());
					mEventDescription.setVisibility(View.VISIBLE);
					mEventDate.setText(getDateFormat(nextEvent.getCalendar()));

					Calendar calendar = Calendar.getInstance();
					calendar.setTime(AppConfig.parse.parse(nextEvent.getStartDateTime()));

					mEventTime.setText(getTimeFormat(calendar));
					mEventTime.setVisibility(View.VISIBLE);
					mPlaceName.setText(place.getName());
					mPlaceName.setVisibility(View.VISIBLE);

					mNextEvent.setOnClickListener(view -> {
						if (place != null) {
							LatLng destiny = new LatLng(Double.parseDouble(latlngArray[0]),Double.parseDouble(latlngArray[1])); // Your destiny LatLng object
							String uri1 = "geo:0,0?q=%f, %f(%s)";
							Intent navIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(String
									.format(Locale.US, uri1, destiny.latitude, destiny.longitude, place.getName())));
							startActivity(navIntent);
						}
					});
				} else {
					mEventDate.setText("No next events!");
					mEventTime.setVisibility(View.GONE);
					mPlaceName.setVisibility(View.GONE);
					mEventDescription.setVisibility(View.GONE);
					getMembers();
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

	private void getSummary() {
		mSwipeRefreshLayout.setRefreshing(true);
		String uri = String.format(AppConfig.URL_SUMMARY,
				SharedPreferencesSaver.getLastTeamID(getApplicationContext()));
		StringRequest stringRequest = new StringRequest(Request.Method.GET, uri, response -> {
			JSONObject object = null;
			try {
				object = new JSONObject(response);
				JSONArray total = object.getJSONArray("total");
				JSONArray notpaid = object.getJSONArray("notpaid");
				JSONArray paid = object.getJSONArray("paid");
				JSONObject totalObject = total.getJSONObject(0);
				JSONObject notpaidObject = notpaid.getJSONObject(0);
				JSONObject paidObject = paid.getJSONObject(0);

				if (totalObject.getString("sum").equals("null")) {
				    mSummary.setVisibility(View.GONE);
                } else {
					mTotal.setText(totalObject.getString("sum") + " " + SharedPreferencesSaver.getCurrencySymbol(getApplicationContext()));
				}
				if (notpaidObject.getString("sum").equals("null")) {
					mNotPaid.setText(0 + " " + SharedPreferencesSaver.getCurrencySymbol(getApplicationContext()));
				} else {
					mNotPaid.setText(notpaidObject.getString("sum") + " " + SharedPreferencesSaver.getCurrencySymbol(getApplicationContext()));
				}
				if (paidObject.getString("sum").equals("null")) {
					mPaid.setText(0 + " " + SharedPreferencesSaver.getCurrencySymbol(getApplicationContext()));
				} else {
					mPaid.setText(paidObject.getString("sum") + " " + SharedPreferencesSaver.getCurrencySymbol(getApplicationContext()));
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
		requestQueue.addRequestFinishedListener((RequestQueue.RequestFinishedListener<String>) request -> mSwipeRefreshLayout.setRefreshing(false));
	}

	private void getMembers() {
		mSwipeRefreshLayout.setRefreshing(true);
		String uri = String.format(AppConfig.URL_GET_TOP_3_FINED_USERS,
				SharedPreferencesSaver.getLastTeamID(getApplicationContext()));
		StringRequest stringRequest = new StringRequest(Request.Method.GET, uri, response -> {
			JSONObject object = null;
			try {
				float startingPosition = 0f;
				object = new JSONObject(response);
				List<BarEntry> entries = new ArrayList<>();
				ArrayList<String> names = new ArrayList<>();
				JSONArray membersArray = object.getJSONArray("members");
				for(int i = 0; i < membersArray.length(); i++) {
					JSONObject user = membersArray.getJSONObject(i);
					JSONObject matchingData = user.getJSONObject("_matchingData").getJSONObject("Users");
					String name = matchingData.getString("name");
					name = name.substring(name.indexOf(" ") + 1, name.length());
					names.add(name);
					entries.add(new BarEntry(startingPosition,user.getInt("sum"),matchingData.getString("name")));
					startingPosition ++;
				}
				if (entries.size() != 0) {
					mChart.getDescription().setEnabled(false);
					mChart.fitScreen();
					mChart.getAxisLeft().setDrawGridLines(false);
					mChart.getAxisRight().setDrawGridLines(false);
					mChart.getXAxis().setDrawGridLines(false);
					mChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
					mChart.getAxisLeft().setEnabled(false);
					mChart.getLegend().setEnabled(false);
					mChart.setScaleEnabled(false);
					mChart.setTouchEnabled(false);
					mChart.setNoDataText(getResources().getString(R.string.summary_loading));
					mChart.setViewPortOffsets(0, 10, 0,60);
					mChart.getAxisRight().setEnabled(false);

					BarDataSet set = new BarDataSet(entries,"");
					set.setValueTextSize(15f);
					set.setColors(MATERIAL_COLORS);

					XAxis xAxis = mChart.getXAxis();
					xAxis.setGranularity(1f);
					xAxis.setGranularityEnabled(true);

					BarData data = new BarData(set);
					data.setValueFormatter(new MyYAxisValueFormatter(SharedPreferencesSaver.getCurrencySymbol(getApplicationContext())));
					data.setBarWidth(0.9f);
					data.setValueTextSize(12f);

					mChart.setData(data);
					mChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(names));
					mChart.getXAxis().setTextSize(12f);
					mChart.notifyDataSetChanged();
					mChart.fitScreen();
					mChart.notifyDataSetChanged();
					mChart.invalidate();
					mChart.animateY(1000);
				} else {
					mMostViolated.setVisibility(View.GONE);
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
		requestQueue.addRequestFinishedListener((RequestQueue.RequestFinishedListener<String>) request -> mSwipeRefreshLayout.setRefreshing(false));
	}
	private String getDateFormat(Calendar c) {
		Date date = c.getTime();
		return AppConfig.nextEventFormat.format(date) + " " + c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
	}

	private String getTimeFormat(Calendar c) {
		Date date = c.getTime();
		DateFormat timeFormatter =
				DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());
		return timeFormatter.format(date);
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			if (mChart != null) {
				mChart.notifyDataSetChanged();
				mChart.fitScreen();
				mChart.invalidate();
				mChart.animateY(1000);
			}
		}
	}
}
