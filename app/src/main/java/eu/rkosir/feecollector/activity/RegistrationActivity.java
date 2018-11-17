package eu.rkosir.feecollector.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.iid.FirebaseInstanceId;

import eu.rkosir.feecollector.R;
import eu.rkosir.feecollector.fragment.registrationFragment.Registration_1st_step;
import eu.rkosir.feecollector.helper.SharedPreferencesSaver;

public class RegistrationActivity extends AppCompatActivity {

	private String firebaseToken;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new Registration_1st_step(), "1st_step").commit();
		}
		FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( this, instanceIdResult -> {
			firebaseToken = instanceIdResult.getToken();
			SharedPreferencesSaver.setFcmToken(this,firebaseToken);
		});
	}
}
