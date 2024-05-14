package com.strong.quranfy.Activity;

import static com.strong.quranfy.Activity.playScreen.currentTime;
import static com.strong.quranfy.Adaptor.surah_adaptor.PlaySurahNumber;
import static com.strong.quranfy.Fragment.Surah_Frag.SearchSurah;
import static com.strong.quranfy.Models.playList.ACTION;
import static com.strong.quranfy.R.drawable.pause;
import static com.strong.quranfy.R.drawable.play;
import static com.strong.quranfy.Utils.mediaService.NextPlay;
import static com.strong.quranfy.Utils.mediaService.PlayPause;
import static com.strong.quranfy.Utils.mediaService.isPlaying;
import static com.strong.quranfy.Utils.mediaService.mediaPlayer;
import static com.strong.quranfy.Utils.mediaService.setFlagPlay;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.strong.quranfy.Adaptor.surah_adaptor;
import com.strong.quranfy.Fragment.Qirat_Frag;
import com.strong.quranfy.Fragment.Surah_Frag;
import com.strong.quranfy.Models.surahData;
import com.strong.quranfy.R;
import com.strong.quranfy.databinding.ActivityDashboardBinding;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;

public class Dashboard extends AppCompatActivity implements surah_adaptor.onClickSendData {
    static ActivityDashboardBinding BindDash;
    viewPagerSelection viewPagerAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BindDash = ActivityDashboardBinding.inflate(getLayoutInflater());

        if (mediaPlayer == null) {
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.cancel(1);
        }

        Surah_Frag Surah_Frag = new Surah_Frag();
        Qirat_Frag QiratFrag = new Qirat_Frag();
        viewPagerAdaptor = new viewPagerSelection(getSupportFragmentManager(), 0);
        viewPagerAdaptor.addFragment(Surah_Frag, "SURAH");
        viewPagerAdaptor.addFragment(QiratFrag, "QIRAT");
        BindDash.dashboardPager.setAdapter(viewPagerAdaptor);
        BindDash.tabLayout.setupWithViewPager(BindDash.dashboardPager);
        Objects.requireNonNull(BindDash.tabLayout.getTabAt(0)).setIcon(R.drawable.quran_icon);
        Objects.requireNonNull(BindDash.tabLayout.getTabAt(1)).setIcon(R.drawable.quran_icon);

        SetData();
        addDate();

        Handler current = new Handler();
        final int delay = 500;
        current.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mediaPlayer != null) BindDash.ProgressBar.setMax(mediaPlayer.getDuration());

                    // Notification Action for Play Pause
                    if (!isPlaying) {
                        BindDash.PlayPauseButton.setImageResource(play);
                    } else {
                        BindDash.surahCurrentTime.setText(currentTime);
                        if (mediaPlayer != null)
                            BindDash.ProgressBar.setProgress(mediaPlayer.getCurrentPosition(), true);
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
            setFlagPlay(PlayPause(this));
            if (!isPlaying) {
                BindDash.PlayPauseButton.setImageResource(play);
            } else {
                BindDash.PlayPauseButton.setImageResource(pause);
            }
        });

        //Next Track
        BindDash.NextTrackButton.setOnClickListener(view -> {
            if (PlaySurahNumber != null && Integer.parseInt(PlaySurahNumber) < 114 && mediaPlayer != null) {
                NextPlay();
                ACTION("NEXT");
            } else Toast.makeText(this, "select surah to listen", Toast.LENGTH_SHORT).show();

        });

        //PlayStrip At bottom
        BindDash.playStrip.setOnClickListener(view -> {
            if (mediaPlayer != null) {
                startActivity(new Intent(this, playScreen.class));
            } else Toast.makeText(this, "select surah to listen", Toast.LENGTH_SHORT).show();
        });

        //SharedPreferences setting Data
        SharedPreferences preferences = getSharedPreferences("RecentPlay", Context.MODE_PRIVATE);
        BindDash.PlaySurahNumber.setText(preferences.getString("SurahNumber", ""));
        BindDash.PlaySurahName.setText(preferences.getString("SurahName", ""));
        BindDash.PlaySurahInform.setText(preferences.getString("SurahInform", ""));

        BindDash.Setting.setOnClickListener(v -> startActivity(new Intent(this, Setting.class)));


        BindDash.SearchButton.setOnClickListener(view -> {
            BindDash.AppBarLayout.setExpanded(true, true);
            BindDash.Search.setVisibility(View.VISIBLE);
            BindDash.Search.setIconified(false);
            BindDash.Search.requestFocus();
        });
        BindDash.Search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!s.isEmpty()) {
                    SearchSurah(s);
                } else com.strong.quranfy.Fragment.Surah_Frag.GetSurah();

                return false;
            }
        });
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
        if (!isPlaying) {
            BindDash.PlayPauseButton.setImageResource(play);
        } else {
            BindDash.PlayPauseButton.setImageResource(pause);
        }
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
}