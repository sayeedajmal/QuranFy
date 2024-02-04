package com.strong.quranfy.Utils;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.strong.quranfy.R;

public class FirebaseMsg extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        if (message.getNotification() != null)
            showNotification(message.getNotification().getTitle(), message.getNotification().getBody());
    }

    private void showNotification(String Title, String message) {
        //Get Apk url
        Uri url = Uri.parse("https://sayeedthedev.web.app");
        Intent intent = new Intent(Intent.ACTION_VIEW, url);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        String channel_ID = "UPDATE";
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        //Notification Builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channel_ID)
                .setSmallIcon(R.drawable.quran_img)
                .setColorized(true)
                .setColor(Color.rgb(255, 255, 255))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setVibrate(new long[]{1000, 1000, 1000, 1000})
                .setOnlyAlertOnce(false).setSilent(false)
                .setContentTitle(Title)
                .setContentText(message)
                .setContentIntent(pendingIntent);

        // Notification Manager
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //Notification Channel
        NotificationChannel channel = new NotificationChannel(channel_ID, "QuranFy", NotificationManager.IMPORTANCE_HIGH);
        channel.enableVibration(true);
        channel.enableLights(true);
        builder.setAutoCancel(true);
        manager.createNotificationChannel(channel);
        manager.notify(0, builder.build());
    }
}
