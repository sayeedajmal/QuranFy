package com.strong.quranfy.Notification;

import static com.strong.quranfy.Activity.mediaService.NextPlay;
import static com.strong.quranfy.Activity.mediaService.PlayPause;
import static com.strong.quranfy.Activity.mediaService.PreviousPlay;
import static com.strong.quranfy.Activity.mediaService.setFlag;
import static com.strong.quranfy.Adaptor.surah_adaptor.PlaySurahNumber;
import static com.strong.quranfy.Adaptor.surah_adaptor.closeNotification;
import static com.strong.quranfy.Models.playList.ACTION;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class BroadCastRec extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String ACTION = intent.getAction();
        switch (ACTION) {
            case "PLAY":
                setFlag(PlayPause());
                break;
            case "NEXT":
                if (Integer.parseInt(PlaySurahNumber) < 114) NextPlay();
                ACTION("NEXT");
                break;
            case "PREVIOUS":
                if (Integer.parseInt(PlaySurahNumber) > 1) PreviousPlay();
                ACTION("PREVIOUS");
                break;
            case "CLOSE":
                closeNotification();
                break;
        }
    }
}
