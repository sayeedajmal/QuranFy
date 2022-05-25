package com.Strong.quranfy;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Toast;

import com.Strong.quranfy.databinding.ActivityPlayScreenBinding;

public class playScreen extends AppCompatActivity {
    ActivityPlayScreenBinding BindPlayScreen;
    int favouriteFlag=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BindPlayScreen=ActivityPlayScreenBinding.inflate(getLayoutInflater());

        String SurahNumber=getIntent().getStringExtra("SurahNumber");
        String SurahName=getIntent().getStringExtra("SurahName");
        String SurahInformation=getIntent().getStringExtra("SurahInformation");

        // Setting Data Of List to PlayScreen
        BindPlayScreen.SurahNumber.setText(SurahNumber);
        BindPlayScreen.surahName.setText(SurahName);
        BindPlayScreen.Particularsurahname.setText(SurahName);
        BindPlayScreen.locationwithVerses.setText(SurahInformation);


        //setting Favourite
        BindPlayScreen.selectFavourite.setOnClickListener(view ->{
            if (favouriteFlag==0) {
                BindPlayScreen.selectFavourite.setImageResource(R.drawable.favouritefilled);
                Toast.makeText(this, "Favourite", Toast.LENGTH_SHORT).show();
                favouriteFlag=1;
            }else{
                BindPlayScreen.selectFavourite.setImageResource(R.drawable.favouriteunfilled);
                Toast.makeText(this, "UnFavourite", Toast.LENGTH_SHORT).show();
                favouriteFlag=0;
            }
        });

        BindPlayScreen.backbutton.setOnClickListener(view ->{
            onBackPressed();
        });
        setContentView(BindPlayScreen.getRoot());
    }
}