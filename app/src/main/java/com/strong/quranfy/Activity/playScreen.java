package com.strong.quranfy.Activity;

import static com.strong.quranfy.R.drawable.pause;
import static com.strong.quranfy.R.drawable.play;
import static com.strong.quranfy.Utils.mediaService.createDuration;
import static com.strong.quranfy.Utils.mediaService.getDuration;
import static com.strong.quranfy.Utils.mediaService.isPlaying;
import static com.strong.quranfy.Utils.mediaService.mediaPlayer;
import static com.strong.quranfy.Utils.mediaService.setFlagPlay;

import android.annotation.SuppressLint;
import android.content.Intent;
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

import com.strong.quranfy.Models.surahData;
import com.strong.quranfy.R;
import com.strong.quranfy.Utils.mediaService;
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
        BindPlayScreen.seekBack.setOnClickListener(view -> {
            if (mediaPlayer != null && mediaService.isPrepared) {
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 5000, MediaPlayer.SEEK_PREVIOUS_SYNC);
            }
        });
        BindPlayScreen.seekForward.setOnClickListener(view -> {
            if (mediaPlayer != null && mediaService.isPrepared) {
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 5000, MediaPlayer.SEEK_NEXT_SYNC);
            }
        });
    }

    public static void currentDuration() {
        Handler current = new Handler();
        final int delay = 100;
        if (mediaPlayer != null) current.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    // Check if mediaPlayer is still valid
                    if (mediaPlayer == null || BindPlayScreen == null) {
                        return;
                    }
                    
                    //Setting TotalTime of Surah
                    String TotalDuration = createDuration(getDuration());
                    BindPlayScreen.TotalTime.setText(TotalDuration);
                    
                    if (mediaPlayer != null && mediaService.isPrepared) {
                        // Hide loading indicator when prepared
                        BindPlayScreen.loadingProgress.setVisibility(View.GONE);
                        BindPlayScreen.PlayPauseButton.setVisibility(View.VISIBLE);
                        
                        //Setting the current duration from the media player
                        int duration = mediaPlayer.getDuration();
                        int currentPosition = mediaPlayer.getCurrentPosition();
                    
                        BindPlayScreen.seekBar.setMax(duration);
                        BindPlayScreen.progress.setMax(duration - 1000);
                        currentTime = createDuration(currentPosition);

                        // Updating the lyric Time with Music RealTime
                        BindPlayScreen.lyrics.updateTime(currentPosition, true);

                        BindPlayScreen.currentTime.setText(currentTime);

                        // Notification Action  for Play Pause
                        if (!isPlaying) {
                            BindPlayScreen.PlayPauseButton.setImageResource(play);
                        } else {
                            //Setting progressBar of Slider
                            BindPlayScreen.seekBar.setProgress(currentPosition, true);
                            BindPlayScreen.progress.setProgress(currentPosition, true);
                            BindPlayScreen.PlayPauseButton.setImageResource(pause);
                        }
                    } else {
                        // Show loading indicator while preparing
                        BindPlayScreen.loadingProgress.setVisibility(View.VISIBLE);
                        BindPlayScreen.PlayPauseButton.setVisibility(View.INVISIBLE);
                        
                        // Reset UI elements to avoid showing stale data
                        BindPlayScreen.seekBar.setProgress(0);
                        BindPlayScreen.progress.setProgress(0);
                        BindPlayScreen.currentTime.setText("00:00");
                    }
                    current.postDelayed(this, delay);
                } catch (IllegalStateException e) {
                    // MediaPlayer is in an invalid state, stop the handler
                    return;
                }
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
                if (mediaPlayer != null && mediaService.isPrepared) {
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
            
            // Add pulse animation to Quran icon
            android.view.animation.Animation pulseAnim = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.pulse_animation);
            BindPlayScreen.quranIcon.startAnimation(pulseAnim);
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
            Intent intent = new Intent(this, mediaService.class);
            intent.setAction("NEXT");
            startService(intent);
        });
    }

    private void PrevTrack() {
        BindPlayScreen.PreviousTrackButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, mediaService.class);
            intent.setAction("PREVIOUS");
            startService(intent);
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
                if (mediaPlayer != null && mediaService.isPrepared) {
                    mediaPlayer.seekTo(seekBar.getProgress());
                }
            }
        });
    }

    private void PlayPause() {
        BindPlayScreen.PlayPauseButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, mediaService.class);
            intent.setAction("PLAY");
            startService(intent);
            // Optimistic UI update or wait for runnable?
            // Original code: setFlagPlay(mediaService.PlayPause(this));
            // Service toggles flag. Runnable updates UI.
            // Let's just trigger service. Runnable loop will handle UI.
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