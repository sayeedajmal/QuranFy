package com.strong.quranfy.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.strong.quranfy.databinding.ActivitySplashBinding;

@SuppressLint("CustomSplashScreen")
public class splashScreen extends AppCompatActivity {
    ActivitySplashBinding BindSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BindSplash = ActivitySplashBinding.inflate(getLayoutInflater());
        startActivity(new Intent(this, Dashboard.class));
        finish();
    }
}