package eu.rkosir.feecollector.activity.teamManagement.calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.rkosir.feecollector.AppConfig;
import eu.rkosir.feecollector.R;
import eu.rkosir.feecollector.fragment.teamManagementFragment.Events;
import eu.rkosir.feecollector.entity.Event;
import eu.rkosir.feecollector.helper.SharedPreferencesSaver;
import eu.rkosir.feecollector.helper.VolleySingleton;


public class AddEvent extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{
	private Button mButton;
	private EditText mDescrition;
	private TextView mStartsDate;
	private TextView mStartsTime;
	private TextView mEndDate;
	private TextView mEndTime;
	private ProgressBar mProgressBar;
	private RelativeLayout mStartsRelative, mEndsRelative;
	private int mDay, mMonth, mYear, mHour, mMinute;
	private Calendar cStart, cEnd;
	private AutoCompleteTextView mAutoCompleteLocation;

	int PLACE_PICKER_REQUEST = 1;

	@SuppressLint("ClickableViewAccessibility")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_event);
		mProgressBar = findViewById(R.id.pb_loading_indicator);

		cStart = Calendar.getInstance();
		cEnd = Calendar.getInstance();
		mYear = cStart.get(Calendar.YEAR);
		mMonth = cStart.get(Calendar.MONTH);
		mDay = cStart.get(Calendar.DAY_OF_MONTH);

		mButton = findViewById(R.id.addNoteButton);
		mAutoCompleteLocation = findViewById(R.id.choose_location);
		mAutoCompleteLocation.setInputType(InputType.TYPE_NULL);
		getLocations();
		mAutoCompleteLocation.setOnTouchListener((arg0, arg1) -> {
			getLocations();
			mAutoCompleteLocation.showDropDown();
			return false;
		});
		mAutoCompleteLocation.setOnItemClickListener((parent, view, position, id) -> {
			if(position == 0) {
				mAutoCompleteLocation.setText(null);
				PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
				try {
					startActivityForResult(builder.build(AddEvent.this), PLACE_PICKER_REQUEST);
				} catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
					e.printStackTrace();
				}
			}
		});


		mDescrition = findViewById(R.id.description);

		mStartsDate = findViewById(R.id.start_date);
		mStartsDate.setText(getDateFormat(cStart));
		mStartsTime = findViewById(R.id.start_time);
		mStartsTime.setText(getTimeFormat(cStart));

		mEndDate = findViewById(R.id.end_date);
		mEndDate.setText(getDateFormat(cStart));
		mEndTime = findViewById(R.id.end_time);
		mEndTime.setText(String.valueOf(getTimeFormat(cStart)));

		mStartsRelative = findViewById(R.id.starts_picker);
		mStartsRelative.setOnClickListener(view -> {
			new DatePickerDialog(AddEvent.this, AddEvent.this, mYear, mMonth, mDay).show();
		});

		mEndsRelative = findViewById(R.id.end_picker);
		mEndsRelative.setOnClickListener(view -> {
			new TimePickerDialog(AddEvent.this, (timePicker, hour, minute) -> {
				cEnd.set(Calendar.HOUR_OF_DAY,hour);
				cEnd.set(Calendar.MINUTE,minute);
				mEndTime.setText(String.valueOf(getTimeFormat(cEnd)));
			}, mHour, mMinute, true).show();
		});

		mButton.setOnClickListener(v -> {
			String title = getIntent().getStringExtra("title");
			Event event;
			event = new Event(cStart,String.valueOf(AppConfig.df.format(cStart.getTime())),String.valueOf(AppConfig.df.format(cEnd.getTime())),title, mDescrition.getText().toString(),
					R.drawable.ic_event_available_black_24dp);
			saveEvent(event);
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PLACE_PICKER_REQUEST) {
			if (resultCode == RESULT_OK) {
				Place place = PlacePicker.getPlace(this,data);
				if(place.getName() != null) {
					savePlace(place);
				}
				mAutoCompleteLocation.setText(place.getName());
			}
		}
	}

	private void savePlace(Place place) {
		mProgressBar.bringToFront();
		mProgressBar.setVisibility(View.VISIBLE);
		StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://rkosir.eu/placesApi/add", response -> {
			JSONObject object = null;
			try {
				object = new JSONObject(response);
				if (!object.getBoolean("error")) {
					getLocations();
				} else {
					Toast.makeText(this, object.getString("error_msg"),Toast.LENGTH_LONG).show();
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}, error -> {
			Toast.makeText(this,R.string.toast_unknown_error,Toast.LENGTH_LONG).show();
		}){
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String,String> params = new HashMap<>();
				params.put("name",(String) place.getName());
				params.put("address", (String) place.getAddress());
				params.put("connection_number", SharedPreferencesSaver.getLastTeamID(AddEvent.this));
				return params;
			}
		};

		RequestQueue requestQueue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();
		requestQueue.add(stringRequest);
		requestQueue.addRequestFinishedListener((RequestQueue.RequestFinishedListener<String>) request -> {
			if (mProgressBar != null) {
				mProgressBar.setVisibility(View.INVISIBLE);
			}
		});
	}

	/**
	 * Sending a Volley Post Request to create an event using 3 parameter: team_name, email
	 */
	private void saveEvent(Event event) {
		mProgressBar.bringToFront();
		mProgressBar.setVisibility(View.VISIBLE);
		StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://rkosir.eu/eventsApi/add", response -> {
			JSONObject object = null;
			try {
				object = new JSONObject(response);
				if (!object.getBoolean("error")) {
					Toast.makeText(this, R.string.toast_successful_event_creation,Toast.LENGTH_LONG).show();

					Intent returnIntent = new Intent();
					returnIntent.putExtra(Events.RESULT, event);
					setResult(Activity.RESULT_OK, returnIntent);
					finish();
				} else {
					Toast.makeText(this, object.getString("error_msg"),Toast.LENGTH_LONG).show();
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}, error -> {
			Toast.makeText(this,R.string.toast_unknown_error,Toast.LENGTH_LONG).show();
		}){
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String,String> params = new HashMap<>();
				params.put("name",event.getName());
				params.put("description", event.getDescription());
				params.put("start",event.getStartDateTime());
				params.put("end",event.getEndDateTime());
				params.put("connection_number", SharedPreferencesSaver.getLastTeamID(AddEvent.this));
				return params;
			}
		};

		RequestQueue requestQueue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();
		requestQueue.add(stringRequest);
		requestQueue.addRequestFinishedListener((RequestQueue.RequestFinishedListener<String>) request -> {
			if (mProgressBar != null) {
				mProgressBar.setVisibility(View.INVISIBLE);
			}
		});
	}

	@Override
	public void onDateSet(DatePicker datePicker, int year, int month, int day) {
		cStart.set(year, month, day);
		mStartsDate.setText(getDateFormat(cStart));
		mEndDate.setText(getDateFormat(cStart));
		mHour = cStart.get(Calendar.HOUR_OF_DAY);
		mMinute = cStart.get(Calendar.MINUTE);
		TimePickerDialog timePickerDialog = new TimePickerDialog(AddEvent.this, AddEvent.this, mHour, mMinute, true);
		timePickerDialog.show();
	}

	@Override
	public void onTimeSet(TimePicker timePicker, int hour, int minute) {
		cStart.set(Calendar.HOUR_OF_DAY,hour);
		cStart.set(Calendar.MINUTE,minute);
		mStartsTime.setText(getTimeFormat(cStart));
	}
	private String getDateFormat(Calendar c) {
		String comma = ", ";
		Date date = c.getTime();
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%ta", date))
				.append(comma)
				.append(String.format("%tb", date))
				.append(" ")
				.append(c.get(Calendar.DAY_OF_MONTH))
				.append(comma)
				.append(c.get(Calendar.YEAR));
		return sb.toString();
	}

	private String getTimeFormat(Calendar c) {
		Date date = c.getTime();
		return String.format("%tH", date) + ":" + String.format("%tM", date);
	}

	/**
	 * Sending a Volley GET Request to get locations using 1 parameter: team_name
	 */
	private void getLocations() {
		mProgressBar.bringToFront();
		mProgressBar.setVisibility(View.VISIBLE);
		String uri = String.format(AppConfig.URL_GET_LOCATIONS,
				SharedPreferencesSaver.getLastTeamID(getApplicationContext()));
		List<eu.rkosir.feecollector.entity.Place> places = new ArrayList<>();
		places.add(new eu.rkosir.feecollector.entity.Place(0,getResources().getString(R.string.add_event_new_location),null,0));
		StringRequest stringRequest = new StringRequest(Request.Method.GET, uri, response -> {
			JSONObject object = null;
			try {
				object = new JSONObject(response);
				JSONArray placesArray = object.getJSONArray("places");
				for(int i = 0; i < placesArray.length(); i++) {
					JSONObject place = placesArray.getJSONObject(i);
					places.add(new eu.rkosir.feecollector.entity.Place(place.getInt("id"),place.getString("name"),place.getString("address"),place.getInt("team_id")));
				}

				ArrayAdapter<eu.rkosir.feecollector.entity.Place> adapter = new ArrayAdapter<eu.rkosir.feecollector.entity.Place>(this,
						R.layout.auto_complete_text_view_layout,places);
				mAutoCompleteLocation.setAdapter(adapter);

			} catch (JSONException e) {
				Toast.makeText(getApplicationContext(),R.string.toast_unknown_error,Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
		}, error -> {
			Toast.makeText(getApplicationContext(),R.string.toast_unknown_error,Toast.LENGTH_LONG).show();
		});

		RequestQueue requestQueue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();
		requestQueue.add(stringRequest);
		requestQueue.addRequestFinishedListener((RequestQueue.RequestFinishedListener<String>) request -> {
			if (mProgressBar != null) {
				mProgressBar.setVisibility(View.INVISIBLE);
			}
		});
	}
}
