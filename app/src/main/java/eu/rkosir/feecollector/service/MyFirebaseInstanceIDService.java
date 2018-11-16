package eu.rkosir.feecollector.service;

import com.google.firebase.messaging.FirebaseMessagingService;

import eu.rkosir.feecollector.helper.SharedPreferencesSaver;

public class MyFirebaseInstanceIDService extends FirebaseMessagingService {

	@Override
	public void onNewToken(String s) {
		SharedPreferencesSaver.setFcmToken(this,s);
	}
}
