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

import eu.rkosir.feecollector.Application;
import eu.rkosir.feecollector.R;
import eu.rkosir.feecollector.activity.DashboardActivity;

public class MyFirebaseMessagaingService extends FirebaseMessagingService {
	@Override
	public void onMessageReceived(RemoteMessage remoteMessage) {
		showNotification(remoteMessage.getData().get("test"));
	}

	private void showNotification(String message) {
		Intent i = new Intent(this, DashboardActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		PendingIntent pendingIntent = PendingIntent.getActivity(
				this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);

		Notification notification = new NotificationCompat.Builder(this, Application.CHANNEL_1_ID)
				.setAutoCancel(true)
				.setContentTitle("FCMTest")
				.setContentText(message)
				.setSmallIcon(R.drawable.ic_attach_money_white_24dp)
				.setPriority(NotificationCompat.PRIORITY_HIGH)
				.setCategory(NotificationCompat.CATEGORY_MESSAGE)
				.setContentIntent(pendingIntent).build();

		NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		manager.notify(0,notification);
	}
}
