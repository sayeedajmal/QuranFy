package com.strong.quranfy.Utils;

import static com.strong.quranfy.Models.surahData.SurahInform;
import static com.strong.quranfy.Models.surahData.SurahName;
import static com.strong.quranfy.Models.surahData.SurahNumber;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.core.app.NotificationCompat;

import com.google.firebase.database.FirebaseDatabase;
import com.strong.quranfy.Activity.Dashboard;
import com.strong.quranfy.R;

public class MediaPanel extends Application {
    public static final String CHANNEL_ID = "Channel_1";
    public static final String CHANNEL_NAME = "QURAN_FY";


    public static void PushNotification(Context context, int REQ_CODE, int PlayPauseCode, String textPlay) {
        Intent intent = new Intent(context, Dashboard.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Bitmap image = BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.quran_icon);
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
        mediaSession.setActive(true);

        // Set media metadata (e.g., title, artist, album art)
        MediaMetadataCompat.Builder metadataBuilder = new MediaMetadataCompat.Builder().putString(MediaMetadataCompat.METADATA_KEY_TITLE, SurahNumber + " - " + SurahName).putString(MediaMetadataCompat.METADATA_KEY_ARTIST, SurahInform).putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, BitmapFactory.decodeResource(context.getResources(), R.drawable.quran_icon));
        mediaSession.setMetadata(metadataBuilder.build());

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID).setLargeIcon(image).setSmallIcon(R.drawable.quran_icon).setSilent(true).setVisibility(NotificationCompat.VISIBILITY_PUBLIC).setAutoCancel(true).setDefaults(NotificationCompat.PRIORITY_HIGH).setCategory(NotificationCompat.EXTRA_MEDIA_SESSION).setContentIntent(pendingIntent).addAction(R.drawable.ic_previous, "Previous", PrevPending).addAction(PlayPauseCode, textPlay, PlayPending)
                // .addAction(!isPlaying ? R.drawable.play : R.drawable.ic_pause, isPlaying ? "Pause" : "Play", PlayPending)
                .addAction(R.drawable.ic_next, "Next", NextPending).addAction(R.drawable.ic_close, "Close", ClosePending).setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setShowCancelButton(true).setCancelButtonIntent(ClosePending).setShowActionsInCompactView(0, 1, 2).setMediaSession(mediaSession.getSessionToken())).setOngoing(true);

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
    }
}
