package com.strong.quranfy.Utils;

import static com.strong.quranfy.Activity.mediaService.NextPlay;
import static com.strong.quranfy.Activity.mediaService.PlayPause;
import static com.strong.quranfy.Activity.mediaService.PreviousPlay;
import static com.strong.quranfy.Activity.mediaService.mediaPlayer;
import static com.strong.quranfy.Activity.mediaService.setFlagPlay;
import static com.strong.quranfy.Adaptor.surah_adaptor.PlaySurahNumber;
import static com.strong.quranfy.Adaptor.surah_adaptor.closeNotification;
import static com.strong.quranfy.Models.playList.ACTION;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import java.util.Objects;


public class BroadCastRec extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String ACTION = intent.getAction();
        // Handle phone state changes (incoming calls, etc.)
        assert ACTION != null;
        if (ACTION.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

            assert state != null;
            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) mediaPlayer.pause();
            } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) mediaPlayer.pause();
            } else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                if (mediaPlayer != null && !mediaPlayer.isPlaying()) mediaPlayer.start();

            }
        }


        switch (Objects.requireNonNull(ACTION)) {
            case "PLAY":
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    setFlagPlay(PlayPause(context));
                } else if (mediaPlayer != null) {
                    setFlagPlay(PlayPause(context));
                }
                break;
            case "NEXT":
                if (Integer.parseInt(PlaySurahNumber) < 114 && mediaPlayer != null) {
                    NextPlay();
                    ACTION("NEXT");
                }
                break;
            case "PREVIOUS":
                if (Integer.parseInt(PlaySurahNumber) > 1 && mediaPlayer != null) {
                    PreviousPlay();
                    ACTION("PREVIOUS");
                }
                break;
            case "CLOSE":
                if (mediaPlayer != null && mediaPlayer.isPlaying()) mediaPlayer.pause();
                setFlagPlay(false);
                closeNotification();
                break;
        }
    }
}
