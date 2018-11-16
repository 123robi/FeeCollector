package eu.rkosir.feecollector.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Map;

import eu.rkosir.feecollector.AppConfig;
import eu.rkosir.feecollector.Application;
import eu.rkosir.feecollector.R;
import eu.rkosir.feecollector.activity.DashboardActivity;
import eu.rkosir.feecollector.activity.teamManagement.calendar.ShowEvent;
import eu.rkosir.feecollector.entity.Event;
import eu.rkosir.feecollector.entity.Place;
import eu.rkosir.feecollector.helper.SharedPreferencesSaver;

public class MyFireBaseMessagingService extends FirebaseMessagingService {

	@Override
	public void onNewToken(String s) {
		super.onNewToken(s);
		SharedPreferencesSaver.setFcmToken(this,s);
	}

	@Override
	public void onMessageReceived(RemoteMessage remoteMessage) {
		showNotificationEvent(remoteMessage.getData());
	}

	private void showNotificationEvent(Map<String, String> message) {
		Calendar calendar = Calendar.getInstance();
		JSONObject event = null;
		Event eventShow = null;
		try {
			event = new JSONObject(message.get("event"));
			calendar.setTime(AppConfig.parse.parse(event.getString("start")));
			eventShow = new Event(
					calendar,event.getString("start"),
					event.getString("end"),
					event.getString("name"),
					event.getString("description"),
					R.drawable.ic_event_available_black_24dp,
					event.getString("place_id"));
		} catch (ParseException | JSONException e) {
			e.printStackTrace();
		}

		Intent i = new Intent(this, ShowEvent.class);
		i .putExtra("event", eventShow);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		PendingIntent pendingIntent = PendingIntent.getActivity(
				this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);

		Notification notification = new NotificationCompat.Builder(this, Application.CHANNEL_1_ID)
				.setAutoCancel(true)
				.setContentTitle("ASDASD")
				.setContentText("ASDASDASD")
				.setSmallIcon(R.drawable.ic_attach_money_white_24dp)
				.setPriority(NotificationCompat.PRIORITY_HIGH)
				.setCategory(NotificationCompat.CATEGORY_MESSAGE)
				.setContentIntent(pendingIntent)
				.build();

		NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		manager.notify(0,notification);
	}
}
