package eu.rkosir.feecollector.activity.teamManagement.calendar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import eu.rkosir.feecollector.R;
import eu.rkosir.feecollector.entity.Event;

public class ShowEvent extends AppCompatActivity {

	private Toolbar mToolbar;
	private Event myEvent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_event);

		Intent intent = getIntent();
		if (intent != null) {
			Object event = intent.getParcelableExtra("event");
			if (event instanceof Event) {
				myEvent = (Event) event;
			}
		}

		mToolbar = findViewById(R.id.back_action_bar);
		mToolbar.setTitle(myEvent.getDescription());
		mToolbar.setNavigationOnClickListener(view -> onBackPressed());
	}
}
