package com.Strong.quranfy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.Toast;
import com.Strong.quranfy.databinding.ActivityPlayScreenBinding;

import java.io.IOException;

public class playScreen extends AppCompatActivity {
    ActivityPlayScreenBinding BindPlayScreen;
    int favouriteFlag=0;
    MediaHandler mediaHandler=new MediaHandler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BindPlayScreen=ActivityPlayScreenBinding.inflate(getLayoutInflater());
        setContentView(BindPlayScreen.getRoot());

        String SurahNumber=getIntent().getStringExtra("SurahNumber");
        String SurahName=getIntent().getStringExtra("SurahName");
        String SurahInformation=getIntent().getStringExtra("SurahInformation");

        //Setting Data of SharedPreference
        SharedPreferences preferences=getSharedPreferences("RecentPlay", Context.MODE_PRIVATE);
        String SharedNumber=preferences.getString("SurahNumber","");
        String SharedName=preferences.getString("SurahName","");
        String SurahInform=preferences.getString("SurahInform","");

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
        BindPlayScreen.selectFavourite.setOnClickListener(view ->{
            if (favouriteFlag==0) {
                BindPlayScreen.selectFavourite.setImageResource(R.drawable.favouritefilled);
                favouriteFlag=1;
            }else{
                BindPlayScreen.selectFavourite.setImageResource(R.drawable.favouriteunfilled);
                favouriteFlag=0;
            }
        });

        //Checking Songs are Playing or not
        try {
            mediaHandler.checkPlayingOrNot(getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Implementation of Update seekBar
        mediaHandler.setUpdateSeekBar(BindPlayScreen.seekBar);

        //Implementing Play Pause Button
        BindPlayScreen.PlayPauseButton.setOnClickListener(view -> mediaHandler.ButtonPlayPause(BindPlayScreen.PlayPauseButton));

        //Performing Button After Song Finish
        mediaHandler.mediaAfterComplete(BindPlayScreen.NextTrackButton);

        //Implementing NextButton
        BindPlayScreen.NextTrackButton.setOnClickListener(view -> mediaHandler.ButtonNext(BindPlayScreen.NextTrackButton, getApplicationContext()));

        //Implementing BackButton
        BindPlayScreen.PreviousTrackButton.setOnClickListener(view -> mediaHandler.ButtonPrevious(BindPlayScreen.PreviousTrackButton, getApplicationContext()));


        BindPlayScreen.backbutton.setOnClickListener(view -> onBackPressed());

        //Setting the Music player from the position of the seekbar
        BindPlayScreen.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //getting the progress of the seek bar and setting it to Media Player
                mediaHandler.SeekBarUpdate(seekBar, BindPlayScreen.StartTime, BindPlayScreen.StopTime);
            }
        });

    }
}