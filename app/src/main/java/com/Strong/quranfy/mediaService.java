package com.Strong.quranfy;

import android.content.Context;
import android.media.MediaPlayer;
import android.widget.Toast;

import java.io.IOException;

public class mediaService {
    static int flag;
    static int duration, currentDuration;
    static MediaPlayer mediaPlayer;

    public static void MediaPlay(String uri) {
        mediaPlayer = new MediaPlayer();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        } else try {
            mediaPlayer.setDataSource(uri);
            mediaPlayer.prepare();
            mediaPlayer.start();
            setDuration(mediaPlayer.getDuration());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static int PlayPause() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                return 0;
            } else {
                mediaPlayer.start();
                return 1;
            }
        } else {
            return 0;
        }
    }

    public static void NextPlay(Context context) {
        Toast.makeText(context, "Next", Toast.LENGTH_SHORT).show();
    }

    public static void CheckMediaPlaying() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
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

    public static int getCurrentDuration() {
        return currentDuration;
    }

    public static void setCurrentDuration(int currentDuration) {
        mediaService.currentDuration = currentDuration;
    }

    public static void setFlag(int flag) {
        mediaService.flag = flag;
    }
}
