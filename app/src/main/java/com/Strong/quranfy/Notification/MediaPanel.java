package com.Strong.quranfy.Notification;

import static com.Strong.quranfy.R.drawable.quran_img;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.Strong.quranfy.Activity.Dashboard;
import com.Strong.quranfy.R;
import com.google.firebase.database.FirebaseDatabase;

public class MediaPanel extends Application {
    public static final String CHANNEL_ID = "Channel_1";
    public static final String CHANNEL_NAME = "QURANFY";
    public static final String ACT_PREVIOUS = "ACTION_PREVIOUS";
    public static final String ACT_PLAY = "ACTION_PLAY";
    public static final String ACT_NEXT = "ACTION_NEXT";


    public static void PushNotification(String SurahNumber, String SurahName, String SurahInform, Context context, int REQ_CODE) {
        Intent intent = new Intent(context, Dashboard.class);
        Bitmap image = BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.quran_img);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, REQ_CODE, intent, PendingIntent.FLAG_IMMUTABLE);

        Intent PlayPause = new Intent(context, BroadCastRec.class);
        PlayPause.putExtra("ACTION", ACT_PLAY);
        PendingIntent PlayPending = PendingIntent.getBroadcast(context, REQ_CODE, PlayPause, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setLargeIcon(image)
                .setSmallIcon(R.drawable.quran)
                .setSilent(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setColorized(true)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
                .setVibrate(new long[]{1000, 1000, 1000, 1000})
                .setContentTitle(SurahNumber + " - " + SurahName)
                .setContentText(SurahInform).setAutoCancel(true)
                .setDefaults(NotificationCompat.GROUP_ALERT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.quran, "Previous", null)
                .addAction(R.drawable.quran_img, "Play Pause", PlayPending)
                .addAction(quran_img, "Next", null)
                .setOnlyAlertOnce(true).setShowWhen(false);
        builder.setOngoing(true);

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        channel.enableLights(true);
        channel.setLockscreenVisibility(1);

        NotificationManager manager = context.getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
        manager.notify(1, builder.build());
    }


    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        createNotificationChannel();
    }

    public void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        channel.enableLights(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            channel.setAllowBubbles(false);
        }
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
    }
}
