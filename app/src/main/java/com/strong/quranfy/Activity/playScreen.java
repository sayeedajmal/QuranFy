package com.strong.quranfy.Activity;

import static com.strong.quranfy.Activity.mediaService.NextPlay;
import static com.strong.quranfy.Activity.mediaService.PreviousPlay;
import static com.strong.quranfy.Activity.mediaService.createDuration;
import static com.strong.quranfy.Activity.mediaService.getDuration;
import static com.strong.quranfy.Activity.mediaService.isPlaying;
import static com.strong.quranfy.Activity.mediaService.mediaPlayer;
import static com.strong.quranfy.Activity.mediaService.setFlagPlay;
import static com.strong.quranfy.R.drawable.pause;
import static com.strong.quranfy.R.drawable.play;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.strong.quranfy.Models.playList;
import com.strong.quranfy.Models.surahData;
import com.strong.quranfy.R;
import com.strong.quranfy.databinding.ActivityPlayScreenBinding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class playScreen extends AppCompatActivity {
    public static String currentSurahNumber;
    static ActivityPlayScreenBinding BindPlayScreen;
    static String currentTime;
    int orientation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BindPlayScreen = ActivityPlayScreenBinding.inflate(getLayoutInflater());

        if (Build.VERSION.SDK_INT > 28)
            BindPlayScreen.progress.setProgressDrawable(AppCompatResources.getDrawable(this, R.drawable.circle));

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        orientation = getResources().getConfiguration().orientation;

        BindPlayScreen.surahName.setText(surahData.getSurahName());
        PlayPause();
        seekBar();
        NextTrack();
        PrevTrack();
        seekBackForward();
        Lyric(surahData.getSurahNumber());

        BindPlayScreen.rotate.setOnClickListener(view -> {
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        });

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            BindPlayScreen.seekBar.setVisibility(View.GONE);
            BindPlayScreen.PreviousTrackButton.setVisibility(View.GONE);
            BindPlayScreen.NextTrackButton.setVisibility(View.GONE);
            BindPlayScreen.TotalTime.setVisibility(View.GONE);
            BindPlayScreen.currentTime.setVisibility(View.GONE);
            BindPlayScreen.surahName.setVisibility(View.GONE);
        }
        setContentView(BindPlayScreen.getRoot());
    }

    private void seekBackForward() {
        BindPlayScreen.seekBack.setOnClickListener(view -> mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 5000, MediaPlayer.SEEK_PREVIOUS_SYNC));
        BindPlayScreen.seekForward.setOnClickListener(view -> mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 5000, MediaPlayer.SEEK_NEXT_SYNC));
    }

    public static void currentDuration() {
        Handler current = new Handler();
        final int delay = 100;
        if (mediaPlayer != null) current.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Setting TotalTime of Surah
                String TotalDuration = createDuration(getDuration());
                BindPlayScreen.TotalTime.setText(TotalDuration);
                try {
                    //Setting the current duration from the media player
                    BindPlayScreen.seekBar.setMax(mediaPlayer.getDuration());
                    BindPlayScreen.progress.setMax(mediaPlayer.getDuration() - 1000);
                    currentTime = createDuration(mediaPlayer.getCurrentPosition());

//                    Updating the lyric Time with Music RealTime
                    BindPlayScreen.lyrics.updateTime(mediaPlayer.getCurrentPosition(), true);

                    BindPlayScreen.currentTime.setText(currentTime);

                    // Notification Action  for Play Pause
                    if (!isPlaying) {
                        BindPlayScreen.PlayPauseButton.setImageResource(play);
                    } else {
                        //Setting progressBar of Slider
                        BindPlayScreen.seekBar.setProgress(mediaPlayer.getCurrentPosition(), true);
                        BindPlayScreen.progress.setProgress(mediaPlayer.getCurrentPosition(), true);
                        BindPlayScreen.PlayPauseButton.setImageResource(pause);
                    }
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
                current.postDelayed(this, delay);
            }
        }, delay);
    }

    private void Lyric(String surahNumber) {
        //  Getting Resource Id
        @SuppressLint("DiscouragedApi") int lyricId = this.getResources().getIdentifier("_" + surahNumber, "raw", getPackageName());
        if (lyricId != 0) {
            BindPlayScreen.progress.setVisibility(View.GONE);
            BindPlayScreen.quranIcon.setVisibility(View.GONE);
            InputStream file = getResources().openRawResource(lyricId);
            String lrcFile = readTextFile(file);
            BindPlayScreen.lyrics.loadLyric(lrcFile, null);
            BindPlayScreen.lyrics.setCurrentTextSize(90);
            BindPlayScreen.lyrics.setNormalTextSize(70);
            BindPlayScreen.lyrics.setTextGravity(Gravity.END);
            BindPlayScreen.lyrics.setTimelineTextColor(Color.rgb(9, 162, 189));
            BindPlayScreen.lyrics.setCurrentColor(Color.rgb(9, 162, 189));

            BindPlayScreen.lyrics.setDraggable(true, l -> {
                BindPlayScreen.lyrics.updateTime(l, true);
                if (mediaPlayer != null) {
                    setFlagPlay(true);
                    mediaPlayer.seekTo(l, MediaPlayer.SEEK_NEXT_SYNC);
                    mediaPlayer.start();
                }
                return false;
            });
            BindPlayScreen.lyrics.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            BindPlayScreen.lyrics.setTimelineColor(Color.parseColor("green"));
        } else {
            BindPlayScreen.lyrics.setVisibility(View.GONE);
            BindPlayScreen.progress.setVisibility(View.VISIBLE);
            BindPlayScreen.quranIcon.setVisibility(View.VISIBLE);
        }

    }

    //Reading Lyric File by InputStream and return as String
    @NonNull
    private String readTextFile(@NonNull InputStream file) {
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
        BindPlayScreen.NextTrackButton.setOnClickListener(view -> {
            playList.ACTION("NEXT");
            NextPlay();
        });
    }

    private void PrevTrack() {
        BindPlayScreen.PreviousTrackButton.setOnClickListener(view -> {
            playList.ACTION("PREVIOUS");
            PreviousPlay();
        });
    }

    private void seekBar() {
        BindPlayScreen.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null) mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
    }

    private void PlayPause() {
        BindPlayScreen.PlayPauseButton.setOnClickListener(view -> {
            setFlagPlay(mediaService.PlayPause(this));
            if (!isPlaying) {
                BindPlayScreen.PlayPauseButton.setImageResource(play);
            } else {
                BindPlayScreen.PlayPauseButton.setImageResource(pause);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isPlaying) {
            BindPlayScreen.PlayPauseButton.setImageResource(play);
        } else {
            BindPlayScreen.PlayPauseButton.setImageResource(pause);
        }
    }
}