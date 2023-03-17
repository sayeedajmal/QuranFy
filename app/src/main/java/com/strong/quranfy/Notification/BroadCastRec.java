package com.strong.quranfy.Notification;

import static com.strong.quranfy.Activity.mediaService.PlayPause;
import static com.strong.quranfy.Activity.mediaService.mediaPlayer;
import static com.strong.quranfy.Activity.mediaService.setFlag;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class BroadCastRec extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getStringExtra("ACTION").equals("ACTION_PLAY")) {
            setFlag(PlayPause());
        } else if (intent.getStringExtra("ACTION").equals("ACTION_PAUSE")) {
            setFlag(PlayPause());
        }
    }

    private static void Play() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) mediaPlayer.start();
    }

    private static void Pause() {
        if (mediaPlayer.isPlaying()) mediaPlayer.pause();
    }
}
