package com.strong.quranfy.Activity;

import static com.strong.quranfy.Activity.mediaService.NextPlay;
import static com.strong.quranfy.Activity.mediaService.PreviousPlay;
import static com.strong.quranfy.Activity.mediaService.createDuration;
import static com.strong.quranfy.Activity.mediaService.flag;
import static com.strong.quranfy.Activity.mediaService.getDuration;
import static com.strong.quranfy.Activity.mediaService.mediaPlayer;
import static com.strong.quranfy.Activity.mediaService.setFlag;
import static com.strong.quranfy.R.drawable.pause;
import static com.strong.quranfy.R.drawable.play;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.strong.quranfy.Models.playList;
import com.strong.quranfy.Models.surahData;
import com.strong.quranfy.databinding.ActivityPlayScreenBinding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class playScreen extends AppCompatActivity {
    public static String currentSurahNumber;
    static ActivityPlayScreenBinding Bind;
    static String currentTime;
    int orientation;


    public static void currentDuration() {
        Handler current = new Handler();
        final int delay = 1000;
        if (mediaPlayer != null) current.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Setting TotalTime of Surah
                String TotalDuration = createDuration(getDuration());
                Bind.TotalTime.setText(TotalDuration);
                try {
                    //Setting the current duration from the media player
                    Bind.progressBar.setMax(mediaPlayer.getDuration());
                    currentTime = createDuration(mediaPlayer.getCurrentPosition());
//                    Updating the lyric Time with Music RealTime
                    Bind.lyrics.updateTime(mediaPlayer.getCurrentPosition(), true);

                    Bind.currentTime.setText(currentTime);
                    if (currentTime.equals(TotalDuration)) {
                        Bind.PlayPauseButton.setImageResource(play);
                        setFlag(0);
                    }

                    // Notification Action  for Play Pause
                    if (flag == 0 | flag == 2) {
                        Bind.PlayPauseButton.setImageResource(play);
                    } else {
                        //Setting progressBar of Slider
                        Bind.progressBar.setProgress(mediaPlayer.getCurrentPosition(), true);
                        Bind.PlayPauseButton.setImageResource(pause);
                    }
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
                current.postDelayed(this, delay);
            }
        }, delay);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bind = ActivityPlayScreenBinding.inflate(getLayoutInflater());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        orientation = getResources().getConfiguration().orientation;

        Bind.surahName.setText(surahData.getSurahName());
        PlayPause();
        seekBar();
        NextTrack();
        PrevTrack();
        Lyric(surahData.getSurahNumber());

        Bind.rotate.setOnClickListener(view -> {
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        });

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Bind.progressBar.setVisibility(View.GONE);
            Bind.PreviousTrackButton.setVisibility(View.GONE);
            Bind.NextTrackButton.setVisibility(View.GONE);
            Bind.TotalTime.setVisibility(View.GONE);
            Bind.currentTime.setVisibility(View.GONE);
            Bind.surahName.setVisibility(View.GONE);
        }
        setContentView(Bind.getRoot());
    }

    private void Lyric(String surahNumber) {
        //  Getting Resource Id
        @SuppressLint("DiscouragedApi") int lyricId = this.getResources().getIdentifier("_" + surahNumber, "raw", getPackageName());
        if (lyricId != 0) {
            InputStream file = getResources().openRawResource(lyricId);
            String lrcFile = readTextFile(file);
            Bind.lyrics.loadLyric(lrcFile, null);
            Bind.lyrics.setCurrentTextSize(100);
            Bind.lyrics.setNormalTextSize(80);
            Bind.lyrics.setTextGravity(Gravity.END);
            Bind.lyrics.setTimelineTextColor(Color.rgb(9, 162, 189));
            Bind.lyrics.setCurrentColor(Color.rgb(9, 162, 189));

            Bind.lyrics.setDraggable(true, l -> {
                Bind.lyrics.updateTime(l, true);
                mediaPlayer.seekTo(l, MediaPlayer.SEEK_NEXT_SYNC);
                mediaPlayer.start();
                Bind.PlayPauseButton.setImageResource(pause);
                return false;
            });
            Bind.lyrics.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            Bind.lyrics.setTimelineColor(Color.parseColor("green"));
        } else Toast.makeText(this, "Lyric Can't Added.", Toast.LENGTH_SHORT).show();

    }

    //Reading Lyric File by InputStream and return as String
    private String readTextFile(InputStream file) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int len;
        try {
            while ((len = file.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            file.close();
        } catch (IOException e) {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
        return outputStream.toString();
    }

    private void NextTrack() {
        Bind.NextTrackButton.setOnClickListener(view -> {
            playList.ACTION("NEXT");
            NextPlay();
        });
    }

    private void PrevTrack() {
        Bind.PreviousTrackButton.setOnClickListener(view -> {
            playList.ACTION("PREVIOUS");
            PreviousPlay();
        });
    }

    private void seekBar() {
        Bind.progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
        Bind.PlayPauseButton.setOnClickListener(view -> {
            setFlag(mediaService.PlayPause());
            if (flag == 0 | flag == 2) {
                Bind.PlayPauseButton.setImageResource(play);
            } else {
                Bind.PlayPauseButton.setImageResource(pause);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (flag == 0 | flag == 2) {
            Bind.PlayPauseButton.setImageResource(play);
        } else {
            Bind.PlayPauseButton.setImageResource(pause);
        }
    }
}