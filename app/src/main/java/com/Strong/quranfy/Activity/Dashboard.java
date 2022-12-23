package com.Strong.quranfy.Activity;

import static com.Strong.quranfy.R.drawable.pause;
import static com.Strong.quranfy.R.drawable.play;
import static com.Strong.quranfy.Activity.mediaService.PlayPause;
import static com.Strong.quranfy.Activity.mediaService.flag;
import static com.Strong.quranfy.Activity.mediaService.mediaPlayer;
import static com.Strong.quranfy.Activity.mediaService.setFlag;
import static com.Strong.quranfy.Activity.playScreen.currentTime;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.Strong.quranfy.Adaptor.surah_adaptor;
import com.Strong.quranfy.Fragment.favourite;
import com.Strong.quranfy.Fragment.surah;
import com.Strong.quranfy.databinding.ActivityDashboardBinding;
import com.google.android.material.snackbar.Snackbar;

public class Dashboard extends AppCompatActivity implements surah_adaptor.onClickSendData {
    ActivityDashboardBinding BindDash;
    viewPagerSelection viewPagerAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BindDash = ActivityDashboardBinding.inflate(getLayoutInflater());

        surah surah = new surah();
        favourite favourite = new favourite();
        viewPagerAdaptor = new viewPagerSelection(getSupportFragmentManager(), 0);
        viewPagerAdaptor.addFragment(surah, "surah");
        viewPagerAdaptor.addFragment(favourite, "Favourite");

        SetData();

       /* FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                System.out.println("<<<<<<<<<<" + s);
            }
        });*/

        BindDash.dashboardPager.setAdapter(viewPagerAdaptor);
        BindDash.tabLayout.setupWithViewPager(BindDash.dashboardPager);


        Handler current = new Handler();
        final int delay = 500;
        current.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    BindDash.surahCurrentTime.setText(currentTime);
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
                current.postDelayed(this, delay);
            }
        }, delay);

        //PlayButton
        BindDash.PlayPauseButton.setOnClickListener(view -> {
            setFlag(PlayPause());
            if (flag == 0 | flag == 2) {
                BindDash.PlayPauseButton.setImageResource(play);
            } else {
                BindDash.PlayPauseButton.setImageResource(pause);
            }
        });

        //Search Button
        BindDash.Search.setOnClickListener(view -> BindDash.SearchText.setVisibility(View.VISIBLE));


        //Next Track
        BindDash.NextTrackButton.setOnClickListener(view -> {
            // NextPlay(BindDash.PlaySurahNumber.getText().toString(), this);
            Snackbar.make(BindDash.NextTrackButton, "Feature are on way..", Snackbar.LENGTH_SHORT).show();
        });

        //PlayStrip At bottom
        BindDash.playStrip.setOnClickListener(view -> {
            if (mediaPlayer != null) startActivity(new Intent(this, playScreen.class));
        });

        //SharedPreferences setting Data
        SharedPreferences preferences = getSharedPreferences("RecentPlay", Context.MODE_PRIVATE);
        BindDash.PlaySurahNumber.setText(preferences.getString("SurahNumber", ""));
        BindDash.PlaySurahName.setText(preferences.getString("SurahName", ""));
        BindDash.PlaySurahInform.setText(preferences.getString("SurahInform", ""));

        /*GETTING CURRENT DATA OF MEDIA*/

        setContentView(BindDash.getRoot());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (flag == 0 | flag == 2) {
            BindDash.PlayPauseButton.setImageResource(play);
        } else {
            BindDash.PlayPauseButton.setImageResource(pause);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    // SETTING DATA FROM SHARED PREFERENCES TO ...LAST READ...
    public void SetData() {
        //Adding Data of SharedPreferences
        @SuppressLint("WrongConstant") SharedPreferences preferences = getSharedPreferences("RecentPlay", MODE_APPEND);
        BindDash.LastReadSurahNum.setText(preferences.getString("SurahNumber", ""));
        BindDash.LastReadSurah.setText(preferences.getString("SurahName", ""));
        BindDash.LastReadSurahArabic.setText(preferences.getString("SurahNameArabic", ""));
    }

    //PLAY STRIP DATA SET WHILE CLICK ON SURAH NAME
    @Override
    public void onReceiveData(Intent intent) {
        BindDash.PlayPauseButton.setImageResource(play);
        BindDash.PlaySurahNumber.setText(intent.getStringExtra("SurahNumber"));
        BindDash.PlaySurahName.setText(intent.getStringExtra("SurahName"));
        BindDash.PlaySurahInform.setText(intent.getStringExtra("SurahInformation"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}