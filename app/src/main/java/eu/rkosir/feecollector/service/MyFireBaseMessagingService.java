package eu.rkosir.feecollector.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
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
        SharedPreferencesSaver.setFcmToken(this, s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        showNotificationEvent(remoteMessage.getData());
    }

    private void showNotificationEvent(Map<String, String> message) {
        Calendar calendar = Calendar.getInstance();
        JSONObject object = null;
        Event eventShow = null;
        Place place = null;
        Calendar calendarStart = Calendar.getInstance();
        try {
            object = new JSONObject(message.get("event"));
            calendar.setTime(AppConfig.parse.parse(object.getString("start")));
            eventShow = new Event(
                    calendar, object.getString("start"),
                    object.getString("end"),
                    object.getString("name"),
                    object.getString("description"),
                    R.drawable.ic_event_available_black_24dp,
                    object.getString("place_id"));
            object = new JSONObject(message.get("place"));
            place = new Place(object.getInt("id"), object.getString("name"), object.getString("address"), object.getString("latlng"), object.getInt("team_id"));
            calendarStart.setTime(AppConfig.parse.parse(eventShow.getStartDateTime()));
        } catch (ParseException | JSONException e) {
            e.printStackTrace();
        }

        Intent i = new Intent(this, ShowEvent.class);
        i.putExtra("event", eventShow);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, Application.CHANNEL_1_ID)
                .setAutoCancel(true)
                .setContentTitle(getResources().getString(R.string.notification_new_event) + " " + eventShow.getName())
                .setContentText("Show details")
                .setStyle(new NotificationCompat.InboxStyle()
                        .addLine(eventShow.getDescription())
                        .addLine(AppConfig.notificationEventFormat.format(calendarStart.getTime()))
                        .addLine(place.getName()))
                .setColor(Color.GREEN)
                .setSmallIcon(R.drawable.ic_attach_money_white_24dp)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(pendingIntent)
                .build();

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(0, notification);
    }
}
