package eu.rkosir.feecollector.activity.teamManagement.calendar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.zxing.common.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import eu.rkosir.feecollector.AppConfig;
import eu.rkosir.feecollector.R;
import eu.rkosir.feecollector.entity.Event;
import eu.rkosir.feecollector.entity.Place;
import eu.rkosir.feecollector.helper.VolleySingleton;

public class ShowEvent extends FragmentActivity implements OnMapReadyCallback {

	private GoogleMap mMap;
	private Event myEvent;
	private Toolbar mToolbar;
	private FloatingActionButton mNavigate;
	private Place mPlace;
	private String [] latlngArray;
	private TextView mEventDate;
	private TextView mEventDay;
	private TextView mStartsTime;
	private TextView mEndsTime;
	private TextView mDescription;
	private TextView mName;
	private TextView mLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_event);
		// Obtain the SupportMapFragment and get notified when the map is ready to be used.
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
		Intent intent = getIntent();
		if (intent != null) {
			Object event = intent.getParcelableExtra("event");
			if (event instanceof Event) {
				myEvent = (Event) event;
			}
		}

		mToolbar = findViewById(R.id.back_action_bar);
		mToolbar.setTitle(myEvent.getName());
		mToolbar.setNavigationOnClickListener(view -> onBackPressed());

		mNavigate = findViewById(R.id.navigate);
		mNavigate.setOnClickListener(view -> {
			if (mPlace != null) {
				LatLng destiny = new LatLng(Double.parseDouble(latlngArray[0]),Double.parseDouble(latlngArray[1])); // Your destiny LatLng object
				String uri1 = "geo:0,0?q=%f, %f(%s)";
				Intent navIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(String
						.format(Locale.US, uri1, destiny.latitude, destiny.longitude, mPlace.getName())));
				startActivity(navIntent);
			}
		});

		mEventDate = findViewById(R.id.date);
		mEventDay = findViewById(R.id.date_name);
		mStartsTime = findViewById(R.id.startsTime);
		mEndsTime = findViewById(R.id.endsTime);
		mDescription = findViewById(R.id.description);
		mName = findViewById(R.id.name);
		mLocation = findViewById(R.id.location);

		Calendar calendarStart = Calendar.getInstance();
		Calendar calendarEnd = Calendar.getInstance();
		try {
			calendarStart.setTime(AppConfig.parse.parse(myEvent.getStartDateTime()));
			calendarEnd.setTime(AppConfig.parse.parse(myEvent.getEndDateTime()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		mEventDay.setText(AppConfig.dayFormat.format(new Date(myEvent.getCalendar().getTimeInMillis())));
		mEventDate.setText(String.valueOf(myEvent.getCalendar().get(Calendar.DATE)));
		mStartsTime.setText(String.valueOf(getTimeFormat(calendarStart)));
		mEndsTime.setText(String.valueOf(getTimeFormat(calendarEnd)));
		if (!TextUtils.isEmpty(myEvent.getDescription())) {
			mDescription.setText(myEvent.getDescription());
		} else {
			findViewById(R.id.card_description).setVisibility(View.GONE);
		}
		mName.setText(myEvent.getName());
	}

	/**
	 * Manipulates the map once available.
	 * This callback is triggered when the map is ready to be used.
	 * This is where we can add markers or lines, add listeners or move the camera. In this case,
	 * we just add a marker near Sydney, Australia.
	 * If Google Play services is not installed on the device, the user will be prompted to install
	 * it inside the SupportMapFragment. This method will only be triggered once the user has
	 * installed Google Play services and returned to the app.
	 */
	@Override
	public void onMapReady(GoogleMap googleMap) {
		String uri = String.format(AppConfig.URL_GET_PLACE_ID,
				myEvent.getPlaceId());
		StringRequest stringRequest = new StringRequest(Request.Method.GET, uri, response -> {
			JSONObject object = null;
			try {
				object = new JSONObject(response);
				mPlace = new Place(object.getInt("id"),object.getString("name"), object.getString("address"), object.getString("latlng"), object.getInt("team_id"));
				mMap = googleMap;
				mMap.getUiSettings().setAllGesturesEnabled(false);
				mMap.getUiSettings().setMapToolbarEnabled(false);
				String lanltd = mPlace.getLatlng().substring(mPlace.getLatlng().indexOf("(")+1, mPlace.getLatlng().indexOf(")"));
				latlngArray = lanltd.split(",");
				LatLng sydney = new LatLng(Double.parseDouble(latlngArray[0]),Double.parseDouble(latlngArray[1]));
				mMap.addMarker(new MarkerOptions().position(sydney).title(mPlace.getName()));
				mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));
				mLocation.setText(mPlace.getName());
				mLocation.append("\n" + mPlace.getAddress());
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
		});
	}

	private String getTimeFormat(Calendar c) {
		Date date = c.getTime();
		DateFormat timeFormatter =
				DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());
		return timeFormatter.format(date);
	}
}
