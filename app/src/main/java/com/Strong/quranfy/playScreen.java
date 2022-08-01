package com.Strong.quranfy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.Strong.quranfy.Models.surahData;
import com.Strong.quranfy.databinding.ActivityPlayScreenBinding;

import java.util.Timer;
import java.util.TimerTask;


public class playScreen extends AppCompatActivity {
    ActivityPlayScreenBinding BindPlayScreen;
    int favouriteFlag = 0;
    MediaHandler mediaHandler = new MediaHandler();

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
            mediaHandler.PlayPause(BindPlayScreen.PlayPauseButton, getApplicationContext());
        });

        //Setting Current Duration
        Handler current = new Handler();
        final int delay = 100;
        current.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Setting the current duration from the media player
                String currentTime = mediaHandler.createDuration(MediaHandler.getCurrentDuration());
                BindPlayScreen.StartTime.setText(currentTime);

                //Setting TotalTime of Surah
                String TotalTime = mediaHandler.createDuration(MediaHandler.getDuration());
                BindPlayScreen.StopTime.setText(TotalTime);
                current.postDelayed(this, delay);
            }
        }, delay);

        //Max Seekbar
        Toast.makeText(this, "Total " + MediaHandler.getDuration(), Toast.LENGTH_SHORT).show();
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Toast.makeText(playScreen.this, " Current " +
                        MediaHandler.getCurrentDuration(), Toast.LENGTH_SHORT).show();
            }
        }, 0, 900);
    }
}