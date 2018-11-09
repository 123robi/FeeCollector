package eu.rkosir.feecollector.activity.teamManagement.calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
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
	private Toolbar mToolbar;
	private EditText mDescrition;
	private ProgressBar mProgressBar;
	private TextInputEditText mStartsDateTime;
	private TextInputEditText mEndsDateTime;
	private TextInputLayout mStartsDateLayout,mEndsDateLayout;
	private int mDay, mMonth, mYear, mHour, mMinute;
	private Calendar cStart, cEnd;
	private AutoCompleteTextView mAutoCompleteLocation;
	private List<eu.rkosir.feecollector.entity.Place> places;
	private eu.rkosir.feecollector.entity.Place selectedPlace;

	private int PLACE_PICKER_REQUEST = 1;
	private String title;

	@SuppressLint("ClickableViewAccessibility")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_event);
		mProgressBar = findViewById(R.id.pb_loading_indicator);
		title = getIntent().getStringExtra("title");
		mToolbar = findViewById(R.id.back_action_bar);
		mToolbar.setTitle(title);
		setSupportActionBar(mToolbar);
		mToolbar.setNavigationOnClickListener(view -> onBackPressed());

		mDescrition = findViewById(R.id.description);

		cStart = Calendar.getInstance();
		cEnd = Calendar.getInstance();
		mYear = cStart.get(Calendar.YEAR);
		mMonth = cStart.get(Calendar.MONTH);
		mDay = cStart.get(Calendar.DAY_OF_MONTH);

		mAutoCompleteLocation = findViewById(R.id.choose_location);
		mAutoCompleteLocation.setInputType(InputType.TYPE_NULL);

		mStartsDateTime = findViewById(R.id.starts_picker);
		mStartsDateTime.setText(String.valueOf(getDateFormat(cStart)));
		mStartsDateTime.append(" " + String.valueOf(getTimeFormat(cStart)));

		mEndsDateTime = findViewById(R.id.ends_picker);
		mEndsDateTime.setText(String.valueOf(getTimeFormat(cStart)));


		mAutoCompleteLocation.setOnTouchListener((arg0, arg1) -> {
			removeKeyboard();
			getLocations();
			mAutoCompleteLocation.showDropDown();
			return false;
		});
		mAutoCompleteLocation.setOnDismissListener(() -> mAutoCompleteLocation.setError(null));

		mAutoCompleteLocation.setOnItemClickListener((parent, view, position, id) -> {
			if(position == 0) {
				mAutoCompleteLocation.setText(null);
				PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
				try {
					startActivityForResult(builder.build(AddEvent.this), PLACE_PICKER_REQUEST);
				} catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
					e.printStackTrace();
				}
			} else {
				selectedPlace = places.get(position);
			}
		});

		mStartsDateTime.setOnClickListener(view -> {
			new DatePickerDialog(AddEvent.this, AddEvent.this, mYear, mMonth, mDay).show();
		});

		mEndsDateTime.setOnClickListener(view -> {
			new TimePickerDialog(AddEvent.this, (timePicker, hour, minute) -> {
				cEnd.set(cStart.get(Calendar.YEAR),cStart.get(Calendar.MONTH),cStart.get(Calendar.DATE));
				cEnd.set(Calendar.HOUR_OF_DAY,hour);
				cEnd.set(Calendar.MINUTE,minute);
				mEndsDateTime.setText(String.valueOf(getTimeFormat(cEnd)));
			}, mHour, mMinute, true).show();
		});

		places = new ArrayList<>();
		getLocations();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PLACE_PICKER_REQUEST) {
			if (resultCode == RESULT_OK) {
				Place place = PlacePicker.getPlace(this,data);
				if (place.getName() != null) {
					savePlace(place);
				}
			}
		}
	}
	/**
	 * Inflate a menu with team_menu
	 * @param menu
	 * @return
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.save_menu,menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.save) {
			if (attemptToSaveEvent()) {
				Event event = new Event(cStart,String.valueOf(AppConfig.df.format(cStart.getTime())),String.valueOf(AppConfig.df.format(cEnd.getTime())),title, mDescrition.getText().toString(),
						R.drawable.ic_event_available_black_24dp,Integer.toString(selectedPlace.getId()));
				saveEvent(event);
			}
		}
		return super.onOptionsItemSelected(item);
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
				params.put("latlng",place.getLatLng().toString());
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
					returnIntent.putExtra(Events.RESULTPLACE, selectedPlace);
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
				params.put("place_name",mAutoCompleteLocation.getText().toString());
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
		mStartsDateTime.setText(getDateFormat(cStart));
		mHour = cStart.get(Calendar.HOUR_OF_DAY);
		mMinute = cStart.get(Calendar.MINUTE);
		TimePickerDialog timePickerDialog = new TimePickerDialog(AddEvent.this, AddEvent.this, mHour, mMinute, true);
		timePickerDialog.show();
	}

	@Override
	public void onTimeSet(TimePicker timePicker, int hour, int minute) {
		cStart.set(Calendar.HOUR_OF_DAY,hour);
		cStart.set(Calendar.MINUTE,minute);
		mStartsDateTime.append(" " + getTimeFormat(cStart));
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
		StringRequest stringRequest = new StringRequest(Request.Method.GET, uri, response -> {
			JSONObject object = null;
			try {
				object = new JSONObject(response);
				JSONArray placesArray = object.getJSONArray("places");
				places = new ArrayList<>();
				places.add(new eu.rkosir.feecollector.entity.Place(0,getResources().getString(R.string.add_event_new_location),null,"",0));
				for(int i = 0; i < placesArray.length(); i++) {
					JSONObject place = placesArray.getJSONObject(i);
					eu.rkosir.feecollector.entity.Place getPlace = new eu.rkosir.feecollector.entity.Place(
							place.getInt("id"),
							place.getString("name"),
							place.getString("address"),
							place.getString("latlng"),
							place.getInt("team_id"));
					places.add(getPlace);
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

	/**
	 * validate fields and request focus if any trouble
	 *
	 * @return true|false
	 */
	private boolean attemptToSaveEvent() {
		mStartsDateLayout = findViewById(R.id.starts_error);
		mEndsDateLayout = findViewById(R.id.ends_error);
		mAutoCompleteLocation.setError(null);
		mEndsDateLayout.setError(null);
		mStartsDateLayout.setError(null);
		String location = mAutoCompleteLocation.getText().toString();

		boolean cancel = false;
		View focusView = null;
		if (TextUtils.isEmpty(location)) {
			mAutoCompleteLocation.setError(getString(R.string.error_field_required), null);
			focusView = mAutoCompleteLocation;
			cancel = true;
		} else if(cEnd.compareTo(cStart) == 0 || cEnd.compareTo(cStart) < 0) {
			mStartsDateLayout.setError(getString(R.string.error_wrong_time));
			mEndsDateLayout.setError(" ");
			focusView = mStartsDateLayout;
			cancel = true;
		}

		if (cancel) {
			focusView.requestFocus();
			return false;
		} else
			return true;
	}

	/**
	 * Remove keyboard when clicking on edit Text where no keyboard is needed e.g. location picker
	 */
	private void removeKeyboard() {
		View view = this.getCurrentFocus();
		if (view != null) {
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}
}
