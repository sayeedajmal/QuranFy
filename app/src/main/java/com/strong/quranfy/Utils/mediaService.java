package com.strong.quranfy.Utils;

import static com.strong.quranfy.Activity.playScreen.currentDuration;
import static com.strong.quranfy.Adaptor.surah_adaptor.PlaySurahNumber;
import static com.strong.quranfy.Adaptor.surah_adaptor.getAudioFile;
import static com.strong.quranfy.Utils.MediaPanel.PushNotification;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import androidx.annotation.Nullable;

import com.strong.quranfy.R;

import java.io.IOException;
import java.util.Objects;

public class mediaService extends Service {
    public static boolean isPlaying = false;
    static int duration;
    public static MediaPlayer mediaPlayer;
    private MediaSessionCompat mediaSession;
    private PlaybackStateCompat.Builder stateBuilder;
    private AudioManager audioManager;
    private AudioFocusRequest audioFocusRequest;
    
    private final AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS:
                    // Permanent loss - stop playback
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        setFlagPlay(false);
                        updatePlaybackState(PlaybackStateCompat.STATE_PAUSED);
                        showNotification(R.drawable.play, "Play");
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    // Temporary loss (e.g., phone call) - pause
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        setFlagPlay(false);
                        updatePlaybackState(PlaybackStateCompat.STATE_PAUSED);
                        showNotification(R.drawable.play, "Play");
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    // Temporary loss but can duck (lower volume) - just lower volume
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.setVolume(0.3f, 0.3f);
                    }
                    break;
                case AudioManager.AUDIOFOCUS_GAIN:
                    // Regained focus - restore volume
                    if (mediaPlayer != null) {
                        mediaPlayer.setVolume(1.0f, 1.0f);
                    }
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        
        // Initialize AudioManager
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        
        // Create AudioFocusRequest for Android O and above
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            audioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(
                            new AudioAttributes.Builder()
                                    .setUsage(AudioAttributes.USAGE_MEDIA)
                                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                    .build()
                    )
                    .setOnAudioFocusChangeListener(audioFocusChangeListener)
                    .build();
        }
        
        mediaSession = new MediaSessionCompat(this, "QuranFy");
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        
        stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PAUSE | 
                            PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_SKIP_TO_NEXT | 
                            PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS | PlaybackStateCompat.ACTION_SEEK_TO);
        
        mediaSession.setPlaybackState(stateBuilder.build());
        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                PlayPause(getApplicationContext());
            }

            @Override
            public void onPause() {
                PlayPause(getApplicationContext());
            }

            @Override
            public void onSkipToNext() {
                NextPlay();
            }

            @Override
            public void onSkipToPrevious() {
                PreviousPlay();
            }

            @Override
            public void onSeekTo(long pos) {
                if (mediaPlayer != null) {
                    mediaPlayer.seekTo((int) pos);
                    updatePlaybackState(PlaybackStateCompat.STATE_PLAYING);
                }
            }
        });
        mediaSession.setActive(true);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();
            switch (action) {
                case "PLAY_URI":
                    String uriString = intent.getStringExtra("uri");
                    if (uriString != null) {
                        MediaPlay(Uri.parse(uriString), this);
                    }
                    break;
                case "PLAY":
                    PlayPause(this);
                    break;
                case "PAUSE":
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        setFlagPlay(false);
                        showNotification(R.drawable.play, "Play");
                    }
                    break;
                case "RESUME":
                    if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                        mediaPlayer.start();
                        setFlagPlay(true);
                        showNotification(R.drawable.pause, "Pause");
                    }
                    break;
                case "NEXT":
                    NextPlay();
                    break;
                case "PREVIOUS":
                    PreviousPlay();
                    break;
                case "CLOSE":
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) mediaPlayer.pause();
                    setFlagPlay(false);
                    
                    // Abandon audio focus
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        audioManager.abandonAudioFocusRequest(audioFocusRequest);
                    } else {
                        audioManager.abandonAudioFocus(audioFocusChangeListener);
                    }
                    
                    stopForeground(true);
                    stopSelf();
                    break;
            }
        }
        return START_NOT_STICKY;
    }

    public void MediaPlay(Uri FileUri, Context context) {
        // Request audio focus before playing
        int result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            result = audioManager.requestAudioFocus(audioFocusRequest);
        } else {
            result = audioManager.requestAudioFocus(audioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }
        
        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            // Could not get audio focus, don't play
            return;
        }
        
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).setUsage(AudioAttributes.USAGE_MEDIA).build());
        }

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        
        try {
            mediaPlayer.setDataSource(context, FileUri);
            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                setDuration(mp.getDuration());
                currentDuration();
                setFlagPlay(true);
                updatePlaybackState(PlaybackStateCompat.STATE_PLAYING);
                
                // Set metadata
                android.support.v4.media.MediaMetadataCompat.Builder metadataBuilder = new android.support.v4.media.MediaMetadataCompat.Builder()
                        .putString(android.support.v4.media.MediaMetadataCompat.METADATA_KEY_TITLE, com.strong.quranfy.Models.surahData.getSurahName())
                        .putString(android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ARTIST, com.strong.quranfy.Models.surahData.getSurahInform())
                        .putLong(android.support.v4.media.MediaMetadataCompat.METADATA_KEY_DURATION, mp.getDuration())
                        .putBitmap(android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ALBUM_ART, android.graphics.BitmapFactory.decodeResource(getResources(), R.drawable.quran_icon));
                mediaSession.setMetadata(metadataBuilder.build());
                
                showNotification(R.drawable.pause, "Pause");
            });
            
            mediaPlayer.setOnCompletionListener(mp -> {
                setFlagPlay(false);
                updatePlaybackState(PlaybackStateCompat.STATE_PAUSED);
                showNotification(R.drawable.play, "Play");
            });
            
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            Log.e("MEDIA SERVICE ERROR : ", Objects.requireNonNull(e.getLocalizedMessage()));
        }
    }

    public void showNotification(int playPauseBtn, String actionText) {
        if (mediaSession == null) return;
        Notification notification = PushNotification(this, 100, playPauseBtn, actionText, mediaSession.getSessionToken());
        startForeground(1, notification);
    }

    // Kept static for compatibility, but should be refactored later
    public static void localSurah(Uri path) {
        // This seems unused or for local files? 
        // For now, let's leave it but it won't trigger foreground service unless we change it.
    }

    public void PlayPause(Context context) {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                setFlagPlay(false);
                updatePlaybackState(PlaybackStateCompat.STATE_PAUSED);
                showNotification(R.drawable.play, "Play");
            } else {
                mediaPlayer.start();
                setFlagPlay(true);
                updatePlaybackState(PlaybackStateCompat.STATE_PLAYING);
                showNotification(R.drawable.pause, "Pause");
            }
        }
    }

    public void NextPlay() {
        if (mediaPlayer != null) {
            int nextSurah = Integer.parseInt(PlaySurahNumber) + 1;
            PlaySurahNumber = String.valueOf(nextSurah);
            mediaPlayer.stop();
            mediaPlayer.reset();
            getAudioFile(String.valueOf(nextSurah));
        }
    }

    public void PreviousPlay() {
        if (mediaPlayer != null) {
            int PrevSurah = Integer.parseInt(PlaySurahNumber) - 1;
            PlaySurahNumber = String.valueOf(PrevSurah);
            mediaPlayer.stop();
            mediaPlayer.reset();
            getAudioFile(String.valueOf(PrevSurah));
        }
    }

    public static void CheckMediaPlaying() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public static String createDuration(int duration) {
        String time = "";
        int min = duration / 1000 / 60;
        int sec = duration / 1000 % 60;
        time = time + min + ":";
        if (sec < 10) {
            time += "0";
        }
        time += sec;
        return time;
    }

    public static int getDuration() {
        return duration;
    }

    public static void setDuration(int duration) {
        mediaService.duration = duration;
    }

    public static void setFlagPlay(boolean isPlaying) {
        mediaService.isPlaying = isPlaying;
    }

    private void updatePlaybackState(int state) {
        if (mediaPlayer != null && mediaSession != null) {
            long currentPosition = mediaPlayer.getCurrentPosition();
            stateBuilder.setState(state, currentPosition, 1.0f);
            mediaSession.setPlaybackState(stateBuilder.build());
        }
    }
}
