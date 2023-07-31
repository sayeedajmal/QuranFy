package com.strong.quranfy.Activity;

import static com.strong.quranfy.Activity.mediaService.NextPlay;
import static com.strong.quranfy.Activity.mediaService.PreviousPlay;
import static com.strong.quranfy.Activity.mediaService.createDuration;
import static com.strong.quranfy.Activity.mediaService.getDuration;
import static com.strong.quranfy.Activity.mediaService.isPlaying;
import static com.strong.quranfy.Activity.mediaService.mediaPlayer;
import static com.strong.quranfy.Activity.mediaService.setFlag;
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
    static ActivityPlayScreenBinding Bind;
    static String currentTime;
    int orientation;


    public static void currentDuration() {
        Handler current = new Handler();
        final int delay = 100;
        if (mediaPlayer != null) current.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Setting TotalTime of Surah
                String TotalDuration = createDuration(getDuration());
                Bind.TotalTime.setText(TotalDuration);
                try {
                    //Setting the current duration from the media player
                    Bind.seekBar.setMax(mediaPlayer.getDuration());
                    Bind.progress.setMax(mediaPlayer.getDuration() - 1000);
                    currentTime = createDuration(mediaPlayer.getCurrentPosition());
//                    Updating the lyric Time with Music RealTime
                    Bind.lyrics.updateTime(mediaPlayer.getCurrentPosition(), true);

                    Bind.currentTime.setText(currentTime);
                    if (currentTime.equals(TotalDuration)) {
                        Bind.PlayPauseButton.setImageResource(play);
                        setFlag(false);
                    }

                    // Notification Action  for Play Pause
                    if (!isPlaying) {
                        Bind.PlayPauseButton.setImageResource(play);
                    } else {
                        //Setting progressBar of Slider
                        Bind.seekBar.setProgress(mediaPlayer.getCurrentPosition(), true);
                        Bind.progress.setProgress(mediaPlayer.getCurrentPosition(), true);
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

        if (Build.VERSION.SDK_INT > 28)
            Bind.progress.setProgressDrawable(AppCompatResources.getDrawable(this, R.drawable.circle));

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
            Bind.seekBar.setVisibility(View.GONE);
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
            Bind.progress.setVisibility(View.GONE);
            Bind.quranIcon.setVisibility(View.GONE);
            InputStream file = getResources().openRawResource(lyricId);
            String lrcFile = readTextFile(file);
            Bind.lyrics.loadLyric(lrcFile, null);
            Bind.lyrics.setCurrentTextSize(90);
            Bind.lyrics.setNormalTextSize(70);
            Bind.lyrics.setTextGravity(Gravity.END);
            Bind.lyrics.setTimelineTextColor(Color.rgb(9, 162, 189));
            Bind.lyrics.setCurrentColor(Color.rgb(9, 162, 189));

            Bind.lyrics.setDraggable(true, l -> {
                Bind.lyrics.updateTime(l, true);
                if (mediaPlayer != null) {
                    setFlag(true);
                    mediaPlayer.seekTo(l, MediaPlayer.SEEK_NEXT_SYNC);
                    mediaPlayer.start();
                }
                return false;
            });
            Bind.lyrics.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            Bind.lyrics.setTimelineColor(Color.parseColor("green"));
        } else {
            Bind.lyrics.setVisibility(View.GONE);
            Bind.progress.setVisibility(View.VISIBLE);
            Bind.quranIcon.setVisibility(View.VISIBLE);
        }

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
        Bind.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
        Bind.PlayPauseButton.setOnClickListener(view -> {
            setFlag(mediaService.PlayPause(this));
            if (!isPlaying) {
                Bind.PlayPauseButton.setImageResource(play);
            } else {
                Bind.PlayPauseButton.setImageResource(pause);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isPlaying) {
            Bind.PlayPauseButton.setImageResource(play);
        } else {
            Bind.PlayPauseButton.setImageResource(pause);
        }
    }
}