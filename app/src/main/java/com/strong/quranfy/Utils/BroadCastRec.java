package com.strong.quranfy.Utils;

import static com.strong.quranfy.Utils.mediaService.mediaPlayer;
import static com.strong.quranfy.Utils.mediaService.setFlagPlay;
import static com.strong.quranfy.Adaptor.surah_adaptor.PlaySurahNumber;
import static com.strong.quranfy.Adaptor.surah_adaptor.PlaySurahNumber;

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
            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING) || state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                Intent pauseIntent = new Intent(context, mediaService.class);
                pauseIntent.setAction("PAUSE");
                context.startService(pauseIntent);
            } else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                Intent resumeIntent = new Intent(context, mediaService.class);
                resumeIntent.setAction("RESUME");
                context.startService(resumeIntent);
            }
        }
    }
}
