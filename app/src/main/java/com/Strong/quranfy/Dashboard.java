package com.Strong.quranfy;

import static com.Strong.quranfy.R.drawable.pause;
import static com.Strong.quranfy.R.drawable.play;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.Strong.quranfy.Adaptor.surah_adaptor;
import com.Strong.quranfy.Fragment.juz;
import com.Strong.quranfy.Fragment.surah;
import com.Strong.quranfy.databinding.ActivityDashboardBinding;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Dashboard extends AppCompatActivity implements surah_adaptor.onClickSendData,MediaHandler.PlayPauseButton {
    ActivityDashboardBinding BindDash;
    MediaHandler mediaHandler =new MediaHandler();
    viewPagerSelection viewPagerAdaptor;
    String SurahNumber, SurahName,SurahInform,SurahNameArabic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BindDash=ActivityDashboardBinding.inflate(getLayoutInflater());

        surah surah=new surah();
        juz juz=new juz();
        viewPagerAdaptor=new viewPagerSelection(getSupportFragmentManager(), 0);
        viewPagerAdaptor.addFragment(surah, "surah");
        viewPagerAdaptor.addFragment(juz, "Juz");

        SetData();

        BindDash.dashboardPager.setAdapter(viewPagerAdaptor);
        BindDash.tabLayout.setupWithViewPager(BindDash.dashboardPager);


        //Search Button
        BindDash.Search.setOnClickListener(view ->{
            BindDash.SearchText.setVisibility(View.VISIBLE);
        });



        //Next Track
        BindDash.NextTrackButton.setOnClickListener(view ->{
            mediaHandler.ButtonNext(BindDash.NextTrackButton, getApplicationContext());
        });

        //PlayStrip At bottom
        BindDash.playStrip.setOnClickListener(view ->{
            Intent intent=new Intent(this, playScreen.class);
            intent.putExtra("SurahNumber",SurahNumber);
            intent.putExtra("SurahName",SurahName);
            intent.putExtra("SurahInformation",SurahInform);
            startActivity(intent);
        });

        setContentView(BindDash.getRoot());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
    // SETTING DATA FROM SHARED PREFERENCES TO ITEM_VIEW
    public void SetData(){
        //Adding Data of SharedPreferences
        @SuppressLint("WrongConstant") SharedPreferences preferences=getSharedPreferences("RecentPlay",MODE_APPEND);
        this.SurahNumber=preferences.getString("SurahNumber","");
        this.SurahName=preferences.getString("SurahName","");
        this.SurahInform=preferences.getString("SurahInform","");
        this.SurahNameArabic=preferences.getString("SurahNameArabic","");
        //LAST READ
        BindDash.LastReadSurahNum.setText(SurahNumber);
        BindDash.LastReadSurah.setText(SurahName);
        BindDash.LastReadSurahArabic.setText(SurahNameArabic);
        //PLAY STRIP
        BindDash.PlaySurahNumber.setText(SurahNumber);
        BindDash.PlaySurahName.setText(SurahName);
        BindDash.PlaySurahLocation.setText(SurahInform);
    }
    @Override
    public void onReceiveData(Intent intent) {
        BindDash.PlayPauseButton.setImageResource(play);
        BindDash.PlaySurahNumber.setText(intent.getStringExtra("SurahNumber"));
        BindDash.PlaySurahName.setText(intent.getStringExtra("SurahName"));
        BindDash.PlaySurahLocation.setText(intent.getStringExtra("SurahInformation"));
        BindDash.PlayPauseButton.setImageResource(pause);
    }

    // Playing Control
    @Override
    public void SendPlayPause(ImageButton button) {
        BindDash.PlayPauseButton.setOnClickListener(v -> {
            mediaHandler.ButtonPlayPause(BindDash.PlayPauseButton);
        });
    }
}