package com.strong.quranfy.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.strong.quranfy.BuildConfig;
import com.strong.quranfy.databinding.ActivitySettingBinding;

public class Setting extends AppCompatActivity {

    ActivitySettingBinding Bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bind = ActivitySettingBinding.inflate(getLayoutInflater());

        Bind.link.setOnClickListener(view -> {
            Uri uri = Uri.parse("https://sayeedthedev.web.app");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });

        MobileAds.initialize(this, initializationStatus -> {
        });

        AdRequest request = new AdRequest.Builder().build();
        Bind.firstAd.loadAd(request);
        Bind.secondAd.loadAd(request);
        Bind.thirdAd.loadAd(request);
        Bind.fourthAd.loadAd(request);
        Bind.fifthAd.loadAd(request);

        Bind.Version.setText(BuildConfig.VERSION_NAME);
        setContentView(Bind.getRoot());
    }
}