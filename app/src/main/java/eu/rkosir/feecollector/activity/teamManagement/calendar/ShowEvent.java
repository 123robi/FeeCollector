package eu.rkosir.feecollector.activity.teamManagement.calendar;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Calendar;

import eu.rkosir.feecollector.AppConfig;
import eu.rkosir.feecollector.R;
import eu.rkosir.feecollector.entity.Event;
import eu.rkosir.feecollector.entity.Place;
import eu.rkosir.feecollector.helper.SharedPreferencesSaver;
import eu.rkosir.feecollector.helper.VolleySingleton;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ShowEvent extends FragmentActivity implements OnMapReadyCallback {

	private GoogleMap mMap;
	private Event myEvent;
	private Toolbar mToolbar;
	private Place place;

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
		Toast.makeText(this, myEvent.getPlaceId(),Toast.LENGTH_LONG).show();

		mToolbar = findViewById(R.id.back_action_bar);
		mToolbar.setTitle(myEvent.getDescription());
		mToolbar.setNavigationOnClickListener(view -> onBackPressed());
		getPlace();
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
		mMap = googleMap;
		mMap.getUiSettings().setAllGesturesEnabled(false);
		String [] latlong = place.getLatlng().split(",");
		LatLng sydney = new LatLng(-34, 151);
		mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));
	}

	public void getPlace() {
		String uri = String.format(AppConfig.URL_GET_PLACE_ID,
				myEvent.getPlaceId());
		StringRequest stringRequest = new StringRequest(Request.Method.GET, uri, response -> {
			JSONObject object = null;
			try {
				object = new JSONObject(response);
				place = new Place(object.getInt("id"),object.getString("name"), object.getString("address"), object.getString("latlng"), object.getInt("team_id"));

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
}
