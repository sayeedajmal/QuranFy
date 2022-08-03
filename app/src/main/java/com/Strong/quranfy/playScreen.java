package com.Strong.quranfy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.Toast;

import com.Strong.quranfy.Models.surahData;
import com.Strong.quranfy.databinding.ActivityPlayScreenBinding;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


public class playScreen extends AppCompatActivity {
    ActivityPlayScreenBinding BindPlayScreen;
    int favouriteFlag = 0;
    String currentTime;
    String TotalTime;
    static int duration, currentDuration;
    static MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BindPlayScreen = ActivityPlayScreenBinding.inflate(getLayoutInflater());
        setContentView(BindPlayScreen.getRoot());

        // Setting Data Of List to PlayScreen by ItemViewClick
        BindPlayScreen.SurahNumber.setText(surahData.getSurahNumber());
        BindPlayScreen.surahName.setText(surahData.getSurahName());
        BindPlayScreen.ParticularSurahName.setText(surahData.getSurahName());
        BindPlayScreen.SurahInformation.setText(surahData.getSurahInform());

        //setting Favourite
        BindPlayScreen.selectFavourite.setOnClickListener(view -> {
            if (favouriteFlag == 0) {
                BindPlayScreen.selectFavourite.setImageResource(R.drawable.favouritefilled);
                favouriteFlag = 1;
            } else {
                BindPlayScreen.selectFavourite.setImageResource(R.drawable.favouriteunfilled);
                favouriteFlag = 0;
            }
        });

        BindPlayScreen.backbutton.setOnClickListener(view -> onBackPressed());

        BindPlayScreen.PlayPauseButton.setOnClickListener(view -> {
            PlayPause(getApplicationContext(), BindPlayScreen.PlayPauseButton);
        });

        //Setting Current Duration
        Handler current = new Handler();
        final int delay = 1000;
        current.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Setting the current duration from the media player
                currentTime = createDuration(getCurrentDuration());
                BindPlayScreen.StartTime.setText(currentTime);

                //Setting TotalTime of Surah
                TotalTime = createDuration(getDuration());
                BindPlayScreen.StopTime.setText(TotalTime);
                current.postDelayed(this, delay);

                BindPlayScreen.seekBar.setMax(getDuration());
                BindPlayScreen.seekBar.setProgress(getCurrentDuration());

            }
        }, delay);
    }

    public static void MediaPlay(String uri) {
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

    public static void PlayPause(Context context, ImageButton imageButton) {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                //imageButton.setImageResource(R.drawable.play);
                mediaPlayer.pause();
            } else {
                // imageButton.setImageResource(R.drawable.pause);
                mediaPlayer.start();
            }
        } else {
            Toast.makeText(context, "First Select A Surah", Toast.LENGTH_SHORT).show();
        }
    }

    public static int getDuration() {
        return duration;
    }

    public static void setDuration(int duration) {
        playScreen.duration = duration;
    }

    public static int getCurrentDuration() {
        return currentDuration;
    }

    public static void setCurrentDuration(int currentDuration) {
        playScreen.currentDuration = currentDuration;
    }

    public void Destroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    public static void CheckMediaPlaying() {
        if (mediaPlayer != null) {
            //we will start mediaPlayer if currently there is no songs in it
            mediaPlayer.start();
            mediaPlayer.release();
        }
    }
}