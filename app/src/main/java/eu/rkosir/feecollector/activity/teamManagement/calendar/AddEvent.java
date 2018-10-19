package eu.rkosir.feecollector.activity.teamManagement.calendar;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.applandeo.materialcalendarview.CalendarView;

import eu.rkosir.feecollector.R;
import eu.rkosir.feecollector.activity.teamManagement.TeamActivity;
import eu.rkosir.feecollector.entity.Team;
import eu.rkosir.feecollector.fragment.teamManagementFragment.Events;
import eu.rkosir.feecollector.helper.MyEventDay;

public class AddEvent extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_event);
		final CalendarView datePicker = findViewById(R.id.datePicker);
		Button button =findViewById(R.id.addNoteButton);
		final EditText noteEditText = findViewById(R.id.noteEditText);
		button.setOnClickListener(v -> {
			Intent returnIntent = new Intent();
			MyEventDay myEventDay = new MyEventDay(datePicker.getSelectedDate(),
					R.drawable.ic_add_black_24dp, noteEditText.getText().toString());
			returnIntent.putExtra(Events.RESULT, myEventDay);
			setResult(Activity.RESULT_OK, returnIntent);
			finish();
		});
	}
}
