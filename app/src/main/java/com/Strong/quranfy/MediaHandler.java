package com.Strong.quranfy;

import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.SeekBar;

public class MediaHandler {
    SeekBar seekMusicBar;
    Thread updateSeekBar;

    public void PlayOrNot( MediaPlayer mediaPlayer){
        if (mediaPlayer != null) {
            //we will start mediaPlayer if currently there is no songs in it
            mediaPlayer.start();
            mediaPlayer.release();
        }
    }

    public void MusicFile(){

    }
}
