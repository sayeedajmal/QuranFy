package com.strong.quranfy.Activity;

import static com.strong.quranfy.Activity.mediaService.NextPlay;
import static com.strong.quranfy.Activity.mediaService.PlayPause;
import static com.strong.quranfy.Activity.mediaService.flag;
import static com.strong.quranfy.Activity.mediaService.mediaPlayer;
import static com.strong.quranfy.Activity.mediaService.setFlag;
import static com.strong.quranfy.Activity.playScreen.currentTime;
import static com.strong.quranfy.R.drawable.pause;
import static com.strong.quranfy.R.drawable.play;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.strong.quranfy.Adaptor.surah_adaptor;
import com.strong.quranfy.Fragment.Qiraat;
import com.strong.quranfy.Fragment.surah;
import com.strong.quranfy.Models.playList;
import com.strong.quranfy.Models.surahData;
import com.strong.quranfy.databinding.ActivityDashboardBinding;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;

public class Dashboard extends AppCompatActivity implements surah_adaptor.onClickSendData {
    static ActivityDashboardBinding BindDash;
    viewPagerSelection viewPagerAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BindDash = ActivityDashboardBinding.inflate(getLayoutInflater());

        BindDash.Search.setEnabled(false);


        surah surah = new surah();
        Qiraat qiraat = new Qiraat();
        viewPagerAdaptor = new viewPagerSelection(getSupportFragmentManager(), 0);
        viewPagerAdaptor.addFragment(surah, "surah");
        viewPagerAdaptor.addFragment(qiraat, "Qiraat");
        BindDash.dashboardPager.setAdapter(viewPagerAdaptor);
        BindDash.tabLayout.setupWithViewPager(BindDash.dashboardPager);

        // Register Device FCM TO Push Notifications and Know Devices Numbers
        getDeviceFCM();
        SetData();
        addDate();

        Handler current = new Handler();
        final int delay = 500;
        current.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    BindDash.surahCurrentTime.setText(currentTime);

                    // Notification Action for Play Pause
                    if (flag == 0 | flag == 2) {
                        BindDash.PlayPauseButton.setImageResource(play);
                    } else {
                        BindDash.PlayPauseButton.setImageResource(pause);
                    }
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
        // BindDash.Search.setOnClickListener(view -> BindDash.SearchText.setVisibility(View.VISIBLE));


        //Next Track
        BindDash.NextTrackButton.setOnClickListener(view -> {
            playList.ACTION("NEXT");
            NextPlay();
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

        BindDash.Setting.setOnClickListener(v -> startActivity(new Intent(this, Setting.class)));
        setContentView(BindDash.getRoot());
    }

    @SuppressLint("SetTextI18n")
    private void addDate() {
        Calendar calendar = new GregorianCalendar();
        String Day = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.MONTH, Locale.getDefault());
        String Month = calendar.getDisplayName(Calendar.MONTH, Calendar.MONTH, Locale.getDefault());
        int Date = calendar.get(Calendar.DATE);
        BindDash.TodayDate.setText(Date + " " + Month + ", " + Day);

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

    public static void updateList() {
        BindDash.PlaySurahNumber.setText(surahData.getSurahNumber());
        BindDash.PlaySurahName.setText(surahData.getSurahName());
        BindDash.PlaySurahInform.setText(surahData.getSurahInform());
    }

    public static void getDeviceFCM() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Devices");
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(s -> {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("Device ID", s);
            database.push().setValue(hashMap);
        }).addOnFailureListener(e -> System.out.println("Device FCM:  " + e.getLocalizedMessage()));
    }
}