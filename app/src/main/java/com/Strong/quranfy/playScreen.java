package com.Strong.quranfy;

import static com.Strong.quranfy.R.drawable.pause;
import static com.Strong.quranfy.R.drawable.play;
import static com.Strong.quranfy.mediaService.createDuration;
import static com.Strong.quranfy.mediaService.flag;
import static com.Strong.quranfy.mediaService.getCurrentDuration;
import static com.Strong.quranfy.mediaService.getDuration;
import static com.Strong.quranfy.mediaService.setFlag;

import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.Strong.quranfy.Models.surahData;
import com.Strong.quranfy.databinding.ActivityPlayScreenBinding;


public class playScreen extends AppCompatActivity {
    ActivityPlayScreenBinding BindPlayScreen;
    int favouriteFlag = 0;
    String currentTime;
    String TotalTime;

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
            setFlag(mediaService.PlayPause());
            if (flag == 0 | flag == 2) {
                BindPlayScreen.PlayPauseButton.setImageResource(play);
            } else {
                BindPlayScreen.PlayPauseButton.setImageResource(pause);
            }
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
            }
        }, delay);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (flag == 0 | flag == 2) {
            BindPlayScreen.PlayPauseButton.setImageResource(play);
        } else {
            BindPlayScreen.PlayPauseButton.setImageResource(pause);
        }
    }
}