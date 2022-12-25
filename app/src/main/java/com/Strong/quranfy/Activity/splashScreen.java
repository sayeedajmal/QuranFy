package com.Strong.quranfy.Activity;

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
        startActivity(new Intent(this, Dashboard.class));
        finish();
    }
}