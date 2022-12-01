package com.Strong.quranfy;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import com.google.firebase.database.FirebaseDatabase;

public class NotificationService extends Application {
    public static final String CHANNEL_ID = "Channel_1";
    public static final String CHANNEL_NAME = "QURANIFY";

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        createNotificationChannel();
    }

    public void createNotificationChannel() {
        NotificationChannel NotifiChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        NotifiChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotifiChannel.enableLights(true);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(NotifiChannel);
    }
}
