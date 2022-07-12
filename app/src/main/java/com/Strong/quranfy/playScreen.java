package com.Strong.quranfy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.Strong.quranfy.databinding.ActivityPlayScreenBinding;


public class playScreen extends AppCompatActivity {
    ActivityPlayScreenBinding BindPlayScreen;
    int favouriteFlag = 0;
    MediaHandler mediaHandler = new MediaHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BindPlayScreen = ActivityPlayScreenBinding.inflate(getLayoutInflater());
        setContentView(BindPlayScreen.getRoot());

        String SurahNumber = getIntent().getStringExtra("SurahNumber");
        String SurahName = getIntent().getStringExtra("SurahName");
        String SurahInformation = getIntent().getStringExtra("SurahInformation");

        //Setting Data of SharedPreference
        SharedPreferences preferences = getSharedPreferences("RecentPlay", Context.MODE_PRIVATE);
        String SharedNumber = preferences.getString("SurahNumber", "");
        String SharedName = preferences.getString("SurahName", "");
        String SurahInform = preferences.getString("SurahInform", "");

        BindPlayScreen.SurahNumber.setText(SharedNumber);
        BindPlayScreen.Particularsurahname.setText(SharedName);
        BindPlayScreen.Particularsurahname.setText(SharedName);
        BindPlayScreen.locationwithVerses.setText(SurahInform);

        // Setting Data Of List to PlayScreen by ItemViewClick
        BindPlayScreen.SurahNumber.setText(SurahNumber);
        BindPlayScreen.surahName.setText(SurahName);
        BindPlayScreen.Particularsurahname.setText(SurahName);
        BindPlayScreen.locationwithVerses.setText(SurahInformation);

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

        //Setting TotalTime of Surah
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            String StopTime = mediaHandler.createDuration(MediaHandler.getDuration());
            BindPlayScreen.StopTime.setText(StopTime);
        }, 1000);

        //Setting Current Duration
        Handler current = new Handler();
        final int delay = 1000;
        current.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Getting the current duration from the media player
                String currentTime = mediaHandler.createDuration(MediaHandler.getCurrentDuration());

                //Setting the current duration in textView
                BindPlayScreen.StartTime.setText(currentTime);
                current.postDelayed(this, delay);
            }
        }, delay);

        Handler seekbar = new Handler();
        //Setting SeekBar Progress
        //Thread to update the seekBar while playing song
        seekbar.postDelayed(new Runnable() {
            @Override
            public void run() {
                while (MediaHandler.getCurrentDuration() < MediaHandler.getCurrentDuration()) {
                    seekbar.postDelayed(this, delay);
                    BindPlayScreen.seekBar.setMax(MediaHandler.getDuration());
                    BindPlayScreen.seekBar.setProgress(MediaHandler.getCurrentDuration());
                }
            }
        }, delay);
    }
}