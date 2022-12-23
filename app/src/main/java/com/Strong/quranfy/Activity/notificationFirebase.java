package com.Strong.quranfy.Activity;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.Strong.quranfy.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class notificationFirebase extends FirebaseMessagingService {
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
        Uri url = Uri.parse("https://firebasestorage.googleapis.com/v0/b/sayeedquranfy.appspot.com/o/app-release.apk?alt=media&token=367ebabd-4520-44e9-b165-68fd0528e4f5");
        Intent intent = new Intent(Intent.ACTION_VIEW, url);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        String channel_ID = "UPDATE";
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        Bitmap bitmap = BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.quran);

        //Notification Builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channel_ID)
                .setColorized(true)
                .setColor(Color.rgb(255, 255, 255))
                .setLargeIcon(bitmap)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.quran)
                .setColor(Color.rgb(255, 255, 255))
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
