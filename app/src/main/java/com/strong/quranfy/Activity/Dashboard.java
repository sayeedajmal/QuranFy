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
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
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
import java.util.Locale;

public class Dashboard extends AppCompatActivity implements surah_adaptor.onClickSendData {
    static ActivityDashboardBinding BindDash;
    viewPagerSelection viewPagerAdaptor;
    private InterstitialAd interstitialAd, BackInterstitial, interPlayScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BindDash = ActivityDashboardBinding.inflate(getLayoutInflater());

        LoadAdsBack();
        LoadAdsPlayScreen();

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(1);

        surah surah = new surah();
        Qiraat qiraat = new Qiraat();
        viewPagerAdaptor = new viewPagerSelection(getSupportFragmentManager(), 0);
        viewPagerAdaptor.addFragment(surah, "surah");
        viewPagerAdaptor.addFragment(qiraat, "Qiraat");
        BindDash.dashboardPager.setAdapter(viewPagerAdaptor);
        BindDash.tabLayout.setupWithViewPager(BindDash.dashboardPager);

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
                    if (flag == 0 | flag == 2) {
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
            setFlag(PlayPause());
            if (flag == 0 | flag == 2) {
                BindDash.PlayPauseButton.setImageResource(play);
            } else {
                BindDash.PlayPauseButton.setImageResource(pause);
            }
        });

        //Next Track
        BindDash.NextTrackButton.setOnClickListener(view -> {
            playList.ACTION("NEXT");
            NextPlay();
        });

        //PlayStrip At bottom
        BindDash.playStrip.setOnClickListener(view -> {
            Intent intent = new Intent(this, playScreen.class);
            if (mediaPlayer != null)
                if (interPlayScreen != null)
                    interPlayScreen.show(Dashboard.this);
            assert interPlayScreen != null;
            interPlayScreen.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                    startActivity(intent);
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);
                    startActivity(intent);
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent();
                }
            });
        });

        //SharedPreferences setting Data
        SharedPreferences preferences = getSharedPreferences("RecentPlay", Context.MODE_PRIVATE);
        BindDash.PlaySurahNumber.setText(preferences.getString("SurahNumber", ""));
        BindDash.PlaySurahName.setText(preferences.getString("SurahName", ""));
        BindDash.PlaySurahInform.setText(preferences.getString("SurahInform", ""));

        BindDash.Setting.setOnClickListener(v -> {
            LoadAdsSetting();
            if (interstitialAd != null) {
                interstitialAd.show(Dashboard.this);
                interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                        startSetting();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        super.onAdFailedToShowFullScreenContent(adError);
                        Toast.makeText(Dashboard.this, adError.getMessage(), Toast.LENGTH_SHORT).show();
                        startSetting();
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent();
                    }
                });

            } else {
                Toast.makeText(this, "Ad not ready Yet", Toast.LENGTH_SHORT).show();
                startSetting();
            }
        });

        BindDash.Search.setOnClickListener(v -> this.startActivity(new Intent(this, SearchActivity.class)));
        setContentView(BindDash.getRoot());
    }

    private void SearchData(Editable editable) {
        String SearchData = editable.toString();
        if (!SearchData.isEmpty()) {

        }

    }

    private void startSetting() {
        startActivity(new Intent(this, Setting.class));
    }

    private void LoadAdsSetting() {
        MobileAds.initialize(this, initializationStatus -> {
        });
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this, "ca-app-pub-8883319358533025/6675443468", adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                Dashboard.this.interstitialAd = interstitialAd;
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                Toast.makeText(Dashboard.this, loadAdError.getMessage(), Toast.LENGTH_SHORT).show();
                interstitialAd = null;
            }
        });
    }

    private void LoadAdsPlayScreen() {
        MobileAds.initialize(this, initializationStatus -> {
        });
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this, "ca-app-pub-8883319358533025/9092641673", adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                Dashboard.this.interPlayScreen = interstitialAd;
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                Toast.makeText(Dashboard.this, loadAdError.getMessage(), Toast.LENGTH_SHORT).show();
                interPlayScreen = null;
            }
        });
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
        LoadAdsSetting();
        if (flag == 0 | flag == 2) {
            BindDash.PlayPauseButton.setImageResource(play);
        } else {
            BindDash.PlayPauseButton.setImageResource(pause);
        }
    }

    @Override
    public void onBackPressed() {
        if (BackInterstitial != null) {
            BackInterstitial.show(Dashboard.this);
            BackInterstitial.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                    Dashboard.super.onBackPressed();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);
                    Dashboard.super.onBackPressed();
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent();
                    Dashboard.super.onBackPressed();
                }

            });
        } else {
            Dashboard.super.onBackPressed();
        }

    }

    private void LoadAdsBack() {
        MobileAds.initialize(this, initializationStatus -> {

            AdRequest adRequest2 = new AdRequest.Builder().build();

            InterstitialAd.load(this, "ca-app-pub-8883319358533025/9301606800", adRequest2, new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd backInterStitial) {
                    Dashboard.this.BackInterstitial = backInterStitial;
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    Toast.makeText(Dashboard.this, loadAdError.getMessage(), Toast.LENGTH_SHORT).show();
                    BackInterstitial = null;
                }
            });
        });
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
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(s -> database.push().setValue(s)).addOnFailureListener(e -> System.out.println("Device FCM:  " + e.getLocalizedMessage()));
    }
}