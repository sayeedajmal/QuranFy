package com.Strong.quranfy;

import android.media.MediaPlayer;

import java.io.IOException;

public class MediaHandler {
    static MediaPlayer mediaPlayer;

    public void MediaPlay(String uri){
        mediaPlayer=new MediaPlayer();
        try {
            mediaPlayer.setDataSource(uri);
            mediaPlayer.prepare();
            mediaPlayer.start();
            String duration=createDuration(mediaPlayer.getDuration());
            System.out.println("<<<<<<<<"+duration);
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
}