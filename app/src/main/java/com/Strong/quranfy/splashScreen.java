package com.Strong.quranfy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.Strong.quranfy.databinding.ActivitySplashBinding;


@SuppressLint("CustomSplashScreen")
public class splashScreen extends AppCompatActivity {
    ActivitySplashBinding BindSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BindSplash = ActivitySplashBinding.inflate(getLayoutInflater());

        new Handler().postDelayed(() -> {
            startActivity(new Intent(this, Dashboard.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 100);
        setContentView(BindSplash.getRoot());
    }
}