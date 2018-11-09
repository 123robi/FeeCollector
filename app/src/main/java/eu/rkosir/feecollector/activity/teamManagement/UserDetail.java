package eu.rkosir.feecollector.activity.teamManagement;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import eu.rkosir.feecollector.R;
import eu.rkosir.feecollector.entity.User;


public class UserDetail extends AppCompatActivity {

	private User myUser;
	private Toolbar mToolbar;

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
	}
}
