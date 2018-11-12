package eu.rkosir.feecollector.activity.teamManagement;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import eu.rkosir.feecollector.R;
import eu.rkosir.feecollector.entity.User;
import eu.rkosir.feecollector.helper.SharedPreferencesSaver;


public class UserDetail extends AppCompatActivity {

	private User myUser;
	private Toolbar mToolbar;
	private TextView mName, mTeam, mAge, mEmail, mNumber, mAddress, mBirthday;
	private RelativeLayout mRelativeLayoutAddress, mRelativeLayoutPhoneNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_detail);
		Intent intent = getIntent();
		mToolbar = findViewById(R.id.back_action_bar);
		mToolbar.setNavigationOnClickListener(view -> onBackPressed());

		if (intent != null) {
			Object user = intent.getParcelableExtra("user");
			if (user instanceof User) {
				myUser = (User) user;
			}
		}
		mToolbar.setTitle(myUser.getName());
		mName = findViewById(R.id.player_name);
		mTeam = findViewById(R.id.player_team);
		mAge = findViewById(R.id.player_age);
		mEmail = findViewById(R.id.email);
		mNumber = findViewById(R.id.phone_number);
		mAddress = findViewById(R.id.address);
		mBirthday = findViewById(R.id.birthday);

		mRelativeLayoutAddress = findViewById(R.id.relative_address);
		mRelativeLayoutPhoneNumber = findViewById(R.id.relative_phone_number);

		if (myUser.getName() != null || !myUser.getName().equals("")) {
			mName.setText(myUser.getName());
		}
		mTeam.setText(SharedPreferencesSaver.getLastTeamName(getApplicationContext()));
		// #todo age
		mEmail.setText(myUser.getEmail());
		if (myUser.getPhoneNumber() != null && !(myUser.getPhoneNumber().equals(""))) {
			mNumber.setText(myUser.getPhoneNumber());
		} else {
			findViewById(R.id.line_phone_number).setVisibility(View.GONE);
			mRelativeLayoutPhoneNumber.setVisibility(View.GONE);
		}

		if (myUser.getAddress() != null && !(myUser.getAddress().equals(""))) {
			mAddress.setText(myUser.getAddress());
		} else {
			findViewById(R.id.line_address).setVisibility(View.GONE);
			mRelativeLayoutAddress.setVisibility(View.GONE);
		}
		// #todo birhtday date
	}
}
