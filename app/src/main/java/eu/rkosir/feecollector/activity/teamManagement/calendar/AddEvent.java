package eu.rkosir.feecollector.activity.teamManagement.calendar;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.applandeo.materialcalendarview.CalendarView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import eu.rkosir.feecollector.AppConfig;
import eu.rkosir.feecollector.R;
import eu.rkosir.feecollector.activity.DashboardActivity;
import eu.rkosir.feecollector.fragment.teamManagementFragment.Events;
import eu.rkosir.feecollector.helper.Event;
import eu.rkosir.feecollector.helper.JsonObjectConverter;
import eu.rkosir.feecollector.helper.SharedPreferencesSaver;
import eu.rkosir.feecollector.helper.VolleySingleton;

import static com.facebook.FacebookSdk.getApplicationContext;

public class AddEvent extends AppCompatActivity {
	private Button mButton;
	private EditText mDescrition;
	private CalendarView mCalendarView;
	private ProgressBar mProgressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_event);
		mCalendarView = findViewById(R.id.calendarView);
		mProgressBar = findViewById(R.id.pb_loading_indicator);

		mButton = findViewById(R.id.addNoteButton);
		mDescrition = findViewById(R.id.description);

		mButton.setOnClickListener(v -> {
			Intent returnIntent = new Intent();
			String title = getIntent().getStringExtra("title");
			Event event;
			event = new Event(mCalendarView.getFirstSelectedDate(),title, mDescrition.getText().toString(),
					R.drawable.ic_event_available_black_24dp);

			returnIntent.putExtra(Events.RESULT, event);
			setResult(Activity.RESULT_OK, returnIntent);
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
					Toast.makeText(this, R.string.toast_successful_team_creation,Toast.LENGTH_LONG).show();
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
				params.put("date",AppConfig.df.format(event.getmCalendar().getTime()));
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
}
