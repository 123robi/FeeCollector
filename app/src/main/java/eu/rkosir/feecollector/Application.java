package eu.rkosir.feecollector;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class Application extends android.app.Application {
	public static final String CHANNEL_1_ID = "channel1";
	public static final String CHANNEL_2_ID = "channel2";
	@Override
	public void onCreate() {
		super.onCreate();
		Fabric.with(this, new Crashlytics());
		createNotificationChannels();
	}

	private void createNotificationChannels() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationChannel channel1 = new NotificationChannel(
					CHANNEL_1_ID,
					"New Events",
					NotificationManager.IMPORTANCE_HIGH
			);
			channel1.setDescription("Receiving notifications on new events");

			NotificationChannel channel2 = new NotificationChannel(
					CHANNEL_2_ID,
					"New Events",
					NotificationManager.IMPORTANCE_HIGH
			);
			channel2.setDescription("Receiving notifications on new events");

			NotificationManager manager = getSystemService(NotificationManager.class);
			manager.createNotificationChannel(channel1);
			manager.createNotificationChannel(channel2);
		}
	}
}
