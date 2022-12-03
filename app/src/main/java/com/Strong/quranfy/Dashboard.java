package com.Strong.quranfy;

import static com.Strong.quranfy.NotificationService.CHANNEL_ID;
import static com.Strong.quranfy.R.drawable.next;
import static com.Strong.quranfy.R.drawable.pause;
import static com.Strong.quranfy.R.drawable.play;
import static com.Strong.quranfy.mediaService.PlayPause;
import static com.Strong.quranfy.mediaService.flag;
import static com.Strong.quranfy.mediaService.mediaPlayer;
import static com.Strong.quranfy.mediaService.setFlag;
import static com.Strong.quranfy.playScreen.currentTime;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.Strong.quranfy.Adaptor.surah_adaptor;
import com.Strong.quranfy.Fragment.juz;
import com.Strong.quranfy.Fragment.surah;
import com.Strong.quranfy.databinding.ActivityDashboardBinding;

public class Dashboard extends AppCompatActivity implements surah_adaptor.onClickSendData {
    ActivityDashboardBinding BindDash;
    viewPagerSelection viewPagerAdaptor;
    NotificationManagerCompat NotifiComp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BindDash = ActivityDashboardBinding.inflate(getLayoutInflater());
        NotifiComp = NotificationManagerCompat.from(getApplicationContext());

        Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.quran);
        Notification channel = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.quran)
                .setColor(Color.rgb(255, 255, 255))
                .setContentTitle("18").setContentTitle("Al-Kahf")
                .setContentText("MECCAN - 110 VERSES")
                .setLargeIcon(image).setPriority(NotificationCompat.PRIORITY_LOW)
                .addAction(play, "Previous", null)
                .addAction(play, "Play", null)
                .addAction(next, "Pause", null)
                .setOnlyAlertOnce(true).setShowWhen(false).build();
        NotifiComp.notify(1, channel);

        surah surah = new surah();
        juz juz = new juz();
        viewPagerAdaptor = new viewPagerSelection(getSupportFragmentManager(), 0);
        viewPagerAdaptor.addFragment(surah, "surah");
        viewPagerAdaptor.addFragment(juz, "Favourite");

        SetData();

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
        BindDash.NextTrackButton.setOnClickListener(view -> mediaService.NextPlay(Dashboard.this));

        //PlayStrip At bottom
        BindDash.playStrip.setOnClickListener(view -> {
            if (mediaPlayer != null) startActivity(new Intent(this, playScreen.class));
        });

        SharedPreferences preferences = getSharedPreferences("RecentPlay", Context.MODE_PRIVATE);
        BindDash.PlaySurahNumber.setText(preferences.getString("SurahNumber", ""));
        BindDash.PlaySurahName.setText(preferences.getString("SurahName", ""));
        BindDash.PlaySurahInform.setText(preferences.getString("SurahInform", ""));
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