package com.strong.quranfy.Activity;

import static com.strong.quranfy.Activity.playScreen.currentDuration;
import static com.strong.quranfy.Adaptor.surah_adaptor.PlaySurahNumber;
import static com.strong.quranfy.Adaptor.surah_adaptor.getAudioFile;
import static com.strong.quranfy.Utils.MediaPanel.PushNotification;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;

import com.strong.quranfy.R;

import java.io.IOException;

public class mediaService {
    public static boolean isPlaying = false;
    static int duration;
    public static MediaPlayer mediaPlayer;

    public static void MediaPlay(String uri) {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).setUsage(AudioAttributes.USAGE_MEDIA).build());

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        } else try {
            mediaPlayer.setDataSource(uri);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mediaPlayer -> {
                mediaPlayer.start();
                setDuration(mediaPlayer.getDuration());
                currentDuration();
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean PlayPause(Context context) {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                PushNotification(context, 100, R.drawable.play, "Play");
                return false;
            }
            mediaPlayer.start();
            PushNotification(context, 100, R.drawable.pause, "Pause");
            return true;
        }
        return false;
    }

    public static void NextPlay() {
        if (mediaPlayer != null) {
            int nextSurah = Integer.parseInt(PlaySurahNumber) + 1;
            PlaySurahNumber = String.valueOf(nextSurah);
            mediaPlayer.stop();
            mediaPlayer.release();
            getAudioFile(String.valueOf(nextSurah));
        }
    }

    public static void PreviousPlay() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            int PrevSurah = Integer.parseInt(PlaySurahNumber) - 1;
            PlaySurahNumber = String.valueOf(PrevSurah);
            mediaPlayer.stop();
            mediaPlayer.release();
            getAudioFile(String.valueOf(PrevSurah));
        }
    }

    public static void CheckMediaPlaying() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
        }
    }

    public static String createDuration(int duration) {
        String time = "";
        int min = duration / 1000 / 60;
        int sec = duration / 1000 % 60;
        time = time + min + ":";
        if (sec < 10) {
            time += "0";
        }
        time += sec;
        return time;
    }

    public static int getDuration() {
        return duration;
    }

    public static void setDuration(int duration) {
        mediaService.duration = duration;
    }

    public static void setFlagPlay(boolean isPlaying) {
        mediaService.isPlaying = isPlaying;
    }
}
