package eu.rkosir.feecollector.service;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import eu.rkosir.feecollector.helper.SharedPreferencesSaver;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

	@Override
	public void onTokenRefresh() {
		super.onTokenRefresh();
		String token = FirebaseInstanceId.getInstance().getToken();
		SharedPreferencesSaver.setFcmToken(this,token);
	}
}
