package com.Strong.quranfy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.Strong.quranfy.databinding.ActivityPlayScreenBinding;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class playScreen extends AppCompatActivity {
    ActivityPlayScreenBinding BindPlayScreen;
    int favouriteFlag=0;
    String AudioFileUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BindPlayScreen=ActivityPlayScreenBinding.inflate(getLayoutInflater());

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
                Toast.makeText(this, "Favourite", Toast.LENGTH_SHORT).show();
                favouriteFlag=1;
            }else{
                BindPlayScreen.selectFavourite.setImageResource(R.drawable.favouriteunfilled);
                Toast.makeText(this, "UnFavourite", Toast.LENGTH_SHORT).show();
                favouriteFlag=0;
            }
        });

        BindPlayScreen.backbutton.setOnClickListener(view -> onBackPressed());
        setContentView(BindPlayScreen.getRoot());

        // Getting AudioFile From FireStorage
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference().child(SurahNumber+".mp3");
        mStorageRef.getDownloadUrl().addOnSuccessListener(uri -> {
             AudioFileUrl=uri.toString();
            Toast.makeText(this, AudioFileUrl, Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(Throwable::printStackTrace);
    }
}