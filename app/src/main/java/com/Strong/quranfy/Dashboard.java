package com.Strong.quranfy;

import static com.Strong.quranfy.R.drawable.pause;
import static com.Strong.quranfy.R.drawable.play;
import static com.Strong.quranfy.mediaService.PlayPause;
import static com.Strong.quranfy.mediaService.flag;
import static com.Strong.quranfy.mediaService.setFlag;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.Strong.quranfy.Adaptor.surah_adaptor;
import com.Strong.quranfy.Fragment.juz;
import com.Strong.quranfy.Fragment.surah;
import com.Strong.quranfy.databinding.ActivityDashboardBinding;

public class Dashboard extends AppCompatActivity implements surah_adaptor.onClickSendData {
    ActivityDashboardBinding BindDash;
    viewPagerSelection viewPagerAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BindDash = ActivityDashboardBinding.inflate(getLayoutInflater());

        surah surah = new surah();
        juz juz = new juz();
        viewPagerAdaptor = new viewPagerSelection(getSupportFragmentManager(), 0);
        viewPagerAdaptor.addFragment(surah, "surah");
        viewPagerAdaptor.addFragment(juz, "Favourite");

        SetData();

        BindDash.dashboardPager.setAdapter(viewPagerAdaptor);
        BindDash.tabLayout.setupWithViewPager(BindDash.dashboardPager);


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
        });

        //PlayStrip At bottom
        BindDash.playStrip.setOnClickListener(view -> {
            Intent intent = new Intent(this, playScreen.class);
            startActivity(intent);
        });

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

    // SETTING DATA FROM SHARED PREFERENCES TO LAST READ
    public void SetData() {
        //Adding Data of SharedPreferences
        @SuppressLint("WrongConstant") SharedPreferences preferences = getSharedPreferences("RecentPlay", MODE_APPEND);
        String SurahNumber = preferences.getString("SurahNumber", "");
        String SurahName = preferences.getString("SurahName", "");
        String SurahNameArabic = preferences.getString("SurahNameArabic", "");
        //LAST READ
        BindDash.LastReadSurahNum.setText(SurahNumber);
        BindDash.LastReadSurah.setText(SurahName);
        BindDash.LastReadSurahArabic.setText(SurahNameArabic);
    }

    @Override
    public void onReceiveData(Intent intent) {
        BindDash.PlayPauseButton.setImageResource(play);
        BindDash.PlaySurahNumber.setText(intent.getStringExtra("SurahNumber"));
        BindDash.PlaySurahName.setText(intent.getStringExtra("SurahName"));
        BindDash.PlaySurahLocation.setText(intent.getStringExtra("SurahInformation"));
        BindDash.PlayPauseButton.setImageResource(pause);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}