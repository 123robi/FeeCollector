package eu.rkosir.feecollector.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Map;

import eu.rkosir.feecollector.AppConfig;
import eu.rkosir.feecollector.Application;
import eu.rkosir.feecollector.R;
import eu.rkosir.feecollector.activity.teamManagement.calendar.ShowEvent;
import eu.rkosir.feecollector.entity.Event;
import eu.rkosir.feecollector.entity.Place;

public class MyFirebaseMessagaingService extends FirebaseMessagingService {
	@Override
	public void onMessageReceived(RemoteMessage remoteMessage) {
		showNotificationEvent(remoteMessage.getData());
	}

	private void showNotificationEvent(Map<String, String> message) {
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(AppConfig.parse.parse(message.get("start")));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Event event = new Event(
				calendar,message.get("start"),
				message.get("end"),
				message.get("name"),
				message.get("description"),
				R.drawable.ic_event_available_black_24dp,
				message.get("place_id")
		);
		Place mPlace = new Place(Integer.parseInt(message.get("id")),message.get("name"), message.get("address"), message.get("latlng"), Integer.parseInt(message.get("team_id")));
		Intent i = new Intent(this, ShowEvent.class);
		i .putExtra("event", event);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		PendingIntent pendingIntent = PendingIntent.getActivity(
				this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);

		Notification notification = new NotificationCompat.Builder(this, Application.CHANNEL_1_ID)
				.setAutoCancel(true)
				.setContentTitle(event.getName())
				.setContentText(event.getDescription() + "\n" + mPlace.getAddress())
				.setSmallIcon(R.drawable.ic_attach_money_white_24dp)
				.setPriority(NotificationCompat.PRIORITY_HIGH)
				.setCategory(NotificationCompat.CATEGORY_MESSAGE)
				.setContentIntent(pendingIntent)
				.build();

		NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		manager.notify(0,notification);
	}
}
