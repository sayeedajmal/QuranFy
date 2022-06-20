package com.Strong.quranfy;

import static com.Strong.quranfy.R.drawable.pause;
import static com.Strong.quranfy.R.drawable.play;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.Strong.quranfy.Adaptor.surah_adaptor;
import com.Strong.quranfy.Fragment.juz;
import com.Strong.quranfy.Fragment.surah;
import com.Strong.quranfy.databinding.ActivityDashboardBinding;

public class Dashboard extends AppCompatActivity implements surah_adaptor.onClickSendData {
    ActivityDashboardBinding BindDash;
    MediaHandler MediaHandler=new MediaHandler();
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


        // Playing Control
        BindDash.PlayPauseButton.setOnClickListener(view ->{
          // Controlling Media with The Class Of MediaHandler
        });



        BindDash.NextTrackButton.setOnClickListener(view ->{
        });

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
}