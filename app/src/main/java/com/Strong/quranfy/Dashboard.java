package com.Strong.quranfy;

import static com.Strong.quranfy.R.drawable.pause;
import static com.Strong.quranfy.R.drawable.play;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

import com.Strong.quranfy.Adaptor.surah_adaptor;
import com.Strong.quranfy.Fragment.juz;
import com.Strong.quranfy.Fragment.surah;
import com.Strong.quranfy.databinding.ActivityDashboardBinding;

public class Dashboard extends AppCompatActivity implements surah_adaptor.onClickSendData {
    static MediaPlayer mediaPlayer;
    ActivityDashboardBinding BindDash;
    viewPagerSelection viewPagerAdaptor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BindDash=ActivityDashboardBinding.inflate(getLayoutInflater());

        surah surah=new surah();

        juz juz=new juz();
        viewPagerAdaptor=new viewPagerSelection(getSupportFragmentManager(), 0);
        viewPagerAdaptor.addFragment(surah, "surah");
        viewPagerAdaptor.addFragment(juz, "Juz");

        BindDash.dashboardPager.setAdapter(viewPagerAdaptor);
        BindDash.tabLayout.setupWithViewPager(BindDash.dashboardPager);
        // Playing Control
        mediaPlayer=MediaPlayer.create(this, R.raw.sureh_fatiha);

        BindDash.PlayPauseButton.setOnClickListener(view ->{
            if(mediaPlayer.isPlaying()){
                BindDash.PlayPauseButton.setImageResource(play);
                mediaPlayer.pause();
            }else{
                BindDash.PlayPauseButton.setImageResource(pause);
               mediaPlayer.start();

            }
        });
        BindDash.NextTrackButton.setOnClickListener(view ->{
        });

        BindDash.playStrip.setOnClickListener(view ->{
            Intent intent=new Intent(this, playScreen.class);
            startActivity(intent);
        });
        setContentView(BindDash.getRoot());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
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