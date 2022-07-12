package com.Strong.quranfy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.Strong.quranfy.databinding.ActivitySplashBinding;


public class Splash extends AppCompatActivity {
    ActivitySplashBinding BindSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BindSplash = ActivitySplashBinding.inflate(getLayoutInflater());

        new Handler().postDelayed(() -> {
            startActivity(new Intent(this, Dashboard.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }, 200);
        setContentView(BindSplash.getRoot());
    }
}