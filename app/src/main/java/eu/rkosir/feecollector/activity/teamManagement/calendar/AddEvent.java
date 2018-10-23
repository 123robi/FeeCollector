package eu.rkosir.feecollector.activity.teamManagement.calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.applandeo.materialcalendarview.CalendarView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
	private CalendarView mCalendarView;
	private ProgressBar mProgressBar;
	private RelativeLayout mStartsRelative, mEndsRelative;
	private int mDay, mMonth, mYear, mHour, mMinute;
	private Calendar cStart, cEnd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_event);
		mCalendarView = findViewById(R.id.calendarView);
		mProgressBar = findViewById(R.id.pb_loading_indicator);

		cStart = Calendar.getInstance();
		cEnd = cStart;
		mYear = cStart.get(Calendar.YEAR);
		mMonth = cStart.get(Calendar.MONTH);
		mDay = cStart.get(Calendar.DAY_OF_MONTH);

		mButton = findViewById(R.id.addNoteButton);
		mDescrition = findViewById(R.id.description);

		mStartsDate = findViewById(R.id.start_date);
		mStartsDate.setText(getDateFormat(cStart));
		mStartsTime = findViewById(R.id.start_time);
		mStartsTime.setText(getTimeFormat(cStart));

		mEndDate = findViewById(R.id.end_date);
		mEndDate.setText(getDateFormat(cStart));
		mEndTime = findViewById(R.id.end_time);
		mEndTime.setText(getTimeFormat(cStart,2));

		mStartsRelative = findViewById(R.id.starts_picker);
		mStartsRelative.setOnClickListener(view -> {
			new DatePickerDialog(AddEvent.this, AddEvent.this, mYear, mMonth, mDay).show();
		});

		mEndsRelative = findViewById(R.id.end_picker);
		mEndsRelative.setOnClickListener(view -> {
			new TimePickerDialog(AddEvent.this, (timePicker, hour, minute) -> {
				cEnd.set(Calendar.HOUR_OF_DAY,hour);
				cEnd.set(Calendar.MINUTE,minute);
				mEndTime.setText(getTimeFormat(cEnd));
			}, mHour, mMinute, true).show();
		});

		mButton.setOnClickListener(v -> {
			String title = getIntent().getStringExtra("title");
			Event event;
			event = new Event(mCalendarView.getFirstSelectedDate(),title, mDescrition.getText().toString(),
					R.drawable.ic_event_available_black_24dp);
			saveEvent(event);
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
				params.put("start",AppConfig.df.format(cStart.getTime()));
				params.put("end",AppConfig.df.format(cEnd.getTime()));
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
		Log.d("HOUR",hour+"");
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

	private String getTimeFormat(Calendar c, int hours) {
		c.set(Calendar.HOUR_OF_DAY,c.get(Calendar.HOUR_OF_DAY)+hours);
		Date date = c.getTime();
		return String.format("%tH", date) + ":" + String.format("%tM", date);
	}
}
