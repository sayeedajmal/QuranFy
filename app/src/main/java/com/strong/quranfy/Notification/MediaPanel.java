package com.strong.quranfy.Notification;

import static com.strong.quranfy.Activity.mediaService.getDuration;

import android.annotation.SuppressLint;
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
import android.support.v4.media.session.MediaSessionCompat;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.google.firebase.database.FirebaseDatabase;
import com.strong.quranfy.Activity.Dashboard;
import com.strong.quranfy.R;

public class MediaPanel extends Application {
    public static final String CHANNEL_ID = "Channel_1";
    public static final String CHANNEL_NAME = "QURANFY";

    public static void PushNotification(String SurahNumber, String SurahName, String SurahInform, Context context, int REQ_CODE) {
        Intent intent = new Intent(context, Dashboard.class);
        Bitmap image = BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.quran_img);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, REQ_CODE, intent, PendingIntent.FLAG_IMMUTABLE);

        /*PlayPause Play ACTION*/
        Intent PlayPause = new Intent(context, BroadCastRec.class);
        PlayPause.setAction("PLAY");
        PendingIntent PlayPending = PendingIntent.getBroadcast(context, REQ_CODE, PlayPause, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        /*Next Play ACTION*/
        Intent NextPlay = new Intent(context, BroadCastRec.class);
        NextPlay.setAction("NEXT");
        PendingIntent NextPending = PendingIntent.getBroadcast(context, REQ_CODE, NextPlay, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        /*Previous Play ACTION*/
        Intent PrevPlay = new Intent(context, BroadCastRec.class);
        PrevPlay.setAction("PREVIOUS");
        PendingIntent PrevPending = PendingIntent.getBroadcast(context, REQ_CODE, PrevPlay, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent ClosePlay = new Intent(context, BroadCastRec.class);
        ClosePlay.setAction("CLOSE");
        PendingIntent ClosePending = PendingIntent.getBroadcast(context, REQ_CODE, ClosePlay, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        MediaSessionCompat mediaSession = new MediaSessionCompat(context, "QuranFy");
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setLargeIcon(image)
                .setSmallIcon(R.drawable.quran)
                .setSilent(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setColorized(true)
                .setContentTitle(SurahNumber + " - " + SurahName)
                .setContentText(SurahInform).setAutoCancel(true)
                .setDefaults(NotificationCompat.GROUP_ALERT_ALL)
                .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setProgress(getDuration(), getDuration(), false)
                .addAction(R.drawable.ic_previous, "Previous", PrevPending)
                .addAction(R.drawable.play, "Play Pause", PlayPending)
                .addAction(R.drawable.ic_next, "Next", NextPending)
                .addAction(R.drawable.ic_close, "Close", ClosePending)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(ClosePending)
                        .setShowActionsInCompactView(1/* #1: pause Button */)
                        .setMediaSession(mediaSession.getSessionToken()));
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

    @SuppressLint("RemoteViewLayout")
    private RemoteViews getView() {
         /*new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowCancelButton(true)
                        .setShowActionsInCompactView(1 *//* #1: pause Button *//*)
                .setMediaSession(mediaSession.getSessionToken())*/
        return new RemoteViews(getPackageName(), R.layout.media_panel);
    }
}
