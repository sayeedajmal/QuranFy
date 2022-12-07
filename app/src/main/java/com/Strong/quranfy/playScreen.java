package com.Strong.quranfy;

import static com.Strong.quranfy.R.drawable.pause;
import static com.Strong.quranfy.R.drawable.play;
import static com.Strong.quranfy.mediaService.createDuration;
import static com.Strong.quranfy.mediaService.flag;
import static com.Strong.quranfy.mediaService.getDuration;
import static com.Strong.quranfy.mediaService.mediaPlayer;
import static com.Strong.quranfy.mediaService.setFlag;

import android.os.Bundle;
import android.os.Handler;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.Strong.quranfy.Models.surahData;
import com.Strong.quranfy.databinding.ActivityPlayScreenBinding;


public class playScreen extends AppCompatActivity {
    static ActivityPlayScreenBinding BindPlayScreen;
    int favouriteFlag = 0;
    public static String currentSurahNumber;
    static String currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BindPlayScreen = ActivityPlayScreenBinding.inflate(getLayoutInflater());
        setContentView(BindPlayScreen.getRoot());

        // Setting Data Of List to PlayScreen by ItemViewClick
        BindPlayScreen.SurahNumber.setText(surahData.getSurahNumber());
        BindPlayScreen.surahName.setText(surahData.getSurahName());
        BindPlayScreen.ParticularSurahName.setText(surahData.getSurahName());
        BindPlayScreen.SurahInformation.setText(surahData.getSurahInform());

        //setting Favourite
        BindPlayScreen.backPlayScreen.setOnClickListener(view -> onBackPressed());

        favourite();
        PlayPause();
        seekBar();
        NextTrack();
        PrevTrack();
    }

    public static void currentDuration() {
        Handler current = new Handler();
        final int delay = 1000;
        current.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Setting TotalTime of Surah
                String TotalDuration = createDuration(getDuration());
                BindPlayScreen.TotalTime.setText(TotalDuration);
                try {
                    //Setting the current duration from the media player
                    BindPlayScreen.progressBar.setMax(mediaPlayer.getDuration());
                    currentTime = createDuration(mediaPlayer.getCurrentPosition());
                    BindPlayScreen.currentTime.setText(currentTime);

                    if (currentTime.equals(TotalDuration)) {
                        BindPlayScreen.PlayPauseButton.setImageResource(play);
                        setFlag(0);
                    }
                    //Setting progressBar of Slider
                    BindPlayScreen.progressBar.setProgress(mediaPlayer.getCurrentPosition(), true);
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
                current.postDelayed(this, delay);
            }
        }, delay);
    }

    private void NextTrack() {
        BindPlayScreen.NextTrackButton.setOnClickListener(view -> {
           /* int surahNumber = Integer.parseInt(currentSurahNumber) + 1;
            surah_adaptor.getAudioFile(String.valueOf(surahNumber));*/
            Toast.makeText(this, "Feature is on Way. So go Back and Select Surah", Toast.LENGTH_SHORT).show();
        });
    }

    private void PrevTrack() {
        BindPlayScreen.NextTrackButton.setOnClickListener(view -> {
           /* int surahNumber = Integer.parseInt(currentSurahNumber) - 1;
            surah_adaptor.getAudioFile(String.valueOf(surahNumber));*/
            Toast.makeText(this, "Feature is on Way. So go Back and Select Surah", Toast.LENGTH_SHORT).show();
        });
    }

    private void seekBar() {
        BindPlayScreen.progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
    }

    private void PlayPause() {
        BindPlayScreen.PlayPauseButton.setOnClickListener(view -> {
            setFlag(mediaService.PlayPause());
            if (flag == 0 | flag == 2) {
                BindPlayScreen.PlayPauseButton.setImageResource(play);
            } else {
                BindPlayScreen.PlayPauseButton.setImageResource(pause);
            }
        });
    }

    private void favourite() {
        BindPlayScreen.selectFavourite.setOnClickListener(view -> {
            if (favouriteFlag == 0) {
                BindPlayScreen.selectFavourite.setImageResource(R.drawable.favouritefilled);
                favouriteFlag = 1;
            } else {
                BindPlayScreen.selectFavourite.setImageResource(R.drawable.favouriteunfilled);
                favouriteFlag = 0;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (flag == 0 | flag == 2) {
            BindPlayScreen.PlayPauseButton.setImageResource(play);
        } else {
            BindPlayScreen.PlayPauseButton.setImageResource(pause);
        }
    }
}