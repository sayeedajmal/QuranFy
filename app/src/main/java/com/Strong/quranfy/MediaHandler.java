package com.Strong.quranfy;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.Toast;


import java.io.IOException;

public class MediaHandler {
    static MediaPlayer mediaPlayer;
    Context context;
    static int duration, currentDuration;

    public static int getDuration() {
        return duration;
    }

    public static void setDuration(int duration) {
        MediaHandler.duration = duration;
    }

    public static int getCurrentDuration() {
        return currentDuration;
    }

    public static void setCurrentDuration(int currentDuration) {
        MediaHandler.currentDuration = currentDuration;
    }

    public MediaHandler() {
        try {
            this.SendData = ((MediaHandler.SendData) context);
        } catch (ClassCastException e) {
            throw new ClassCastException(e.getMessage());
        }
    }

    public void MediaPlay(String uri) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(uri);
            mediaPlayer.prepare();
            mediaPlayer.start();

            //Setting Total Duration of MediaPlayer
            setDuration(mediaPlayer.getDuration());

            //Setting Current Duration of MediaPlayer
            Handler handler = new Handler();
            final int delay = 1000;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Getting the current duration from the media player
                    setCurrentDuration(mediaPlayer.getCurrentPosition());

                    handler.postDelayed(this, delay);
                }
            }, delay);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String createDuration(int duration) {

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

    public void PlayPause(ImageButton imageButton, Context context) {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                imageButton.setImageResource(R.drawable.play);
                mediaPlayer.pause();
                // SendData.onPlayPauseImage(0);
            } else {
                imageButton.setImageResource(R.drawable.pause);
                mediaPlayer.start();
                //  SendData.onPlayPauseImage(1);
            }
        } else {
            Toast.makeText(context, "First Select A Surah", Toast.LENGTH_SHORT).show();
        }
    }

    public void CheckMediaPlaying() {
        if (mediaPlayer != null) {
            //we will start mediaPlayer if currently there is no songs in it
            mediaPlayer.start();
            mediaPlayer.release();
        }
    }

    public void Destroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    private final SendData SendData;

    public interface SendData {
        void onPlayPauseImage(int number);
    }
}