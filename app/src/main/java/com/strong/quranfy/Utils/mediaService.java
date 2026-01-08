package com.strong.quranfy.Utils;

import static com.strong.quranfy.Activity.playScreen.currentDuration;
import static com.strong.quranfy.Adaptor.surah_adaptor.PlaySurahNumber;
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
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.Nullable;

import com.strong.quranfy.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.os.PowerManager;

public class mediaService extends Service {
    public static boolean isPlaying = false;
    static int duration;
    public static MediaPlayer mediaPlayer;
    private MediaSessionCompat mediaSession;
    private PlaybackStateCompat.Builder stateBuilder;
    private AudioManager audioManager;
    private AudioFocusRequest audioFocusRequest;
    
    private ArrayList<String> playlist = new ArrayList<>();
    private int currentTrackIndex = 0;
    private String currentSurahName = "";
    private String currentSurahInfo = "";
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS:
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        setFlagPlay(false);
                        updatePlaybackState(PlaybackStateCompat.STATE_PAUSED);
                        showNotification(R.drawable.play, "Play");
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        setFlagPlay(false);
                        updatePlaybackState(PlaybackStateCompat.STATE_PAUSED);
                        showNotification(R.drawable.play, "Play");
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.setVolume(0.3f, 0.3f);
                    }
                    break;
                case AudioManager.AUDIOFOCUS_GAIN:
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
        
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        
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
                case "PLAY_PLAYLIST":
                    playlist = intent.getStringArrayListExtra("playlist");
                    currentSurahName = intent.getStringExtra("surahName");
                    currentSurahInfo = intent.getStringExtra("surahInfo");
                    currentTrackIndex = 0;
                    
                    Log.d("MEDIA SERVICE", "Received playlist with " + (playlist != null ? playlist.size() : 0) + " tracks");
                    if (playlist != null) {
                        for (int i = 0; i < Math.min(playlist.size(), 5); i++) {
                            Log.d("MEDIA SERVICE", "Track " + i + ": " + playlist.get(i));
                        }
                    }
                    
                    if (playlist != null && !playlist.isEmpty()) {
                        MediaPlay(playlist.get(currentTrackIndex));
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

    public static boolean isPrepared = false;

    public void MediaPlay(String url) {
        // Skip empty URLs
        if (url == null || url.isEmpty()) {
            Log.e("MEDIA SERVICE", "Empty URL, skipping to next");
            playNextTrack();
            return;
        }

        int result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            result = audioManager.requestAudioFocus(audioFocusRequest);
        } else {
            result = audioManager.requestAudioFocus(audioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }

        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            return;
        }

        isPrepared = false;
        duration = 0;

        // Run MediaPlayer setup in background to avoid blocking main thread (Slow Binder)
        executorService.execute(() -> {
            try {
                if (mediaPlayer != null) {
                    try {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                    } catch (Exception e) {
                        Log.e("MEDIA SERVICE", "Error releasing player: " + e.getMessage());
                    }
                    mediaPlayer = null;
                }

                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build());
                
                // Keep CPU awake during playback
                mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);

                Log.d("MEDIA SERVICE", "Playing URL: " + url);
                
                // Use setDataSource(String) for network URLs
                mediaPlayer.setDataSource(url);

                mediaPlayer.setOnPreparedListener(mp -> {
                    isPrepared = true;
                    mp.start();
                    setDuration(mp.getDuration());
                    currentDuration();
                    setFlagPlay(true);
                    updatePlaybackState(PlaybackStateCompat.STATE_PLAYING);

                    MediaMetadataCompat.Builder metadataBuilder = new MediaMetadataCompat.Builder()
                            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, currentSurahName)
                            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, currentSurahInfo)
                            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, mp.getDuration())
                            .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, android.graphics.BitmapFactory.decodeResource(getResources(), R.drawable.quran_icon));
                    mediaSession.setMetadata(metadataBuilder.build());

                    showNotification(R.drawable.pause, "Pause");
                });

                mediaPlayer.setOnCompletionListener(mp -> {
                    isPrepared = false;
                    playNextTrack();
                });

                mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                    isPrepared = false;
                    String errorType = (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) ? "Server Died" : "Unknown";
                    Log.e("MEDIA SERVICE ERROR", "Error playing: " + url + " what=" + what + " (" + errorType + ") extra=" + extra);
                    
                    try {
                        mp.reset();
                    } catch (Exception e) {
                        Log.e("MEDIA SERVICE", "Error resetting player: " + e.getMessage());
                    }
                    
                    new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(this::playNextTrack, 500);
                    return true;
                });

                mediaPlayer.prepareAsync();
            } catch (IOException | IllegalStateException e) {
                isPrepared = false;
                Log.e("MEDIA SERVICE ERROR", "Setup failed: " + e.getMessage());
                new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(this::playNextTrack, 500);
            }
        });
    }
    
    private void playNextTrack() {
        if (currentTrackIndex < playlist.size() - 1) {
            currentTrackIndex++;
            Log.d("MEDIA SERVICE", "Moving to next track: " + currentTrackIndex);
            MediaPlay(playlist.get(currentTrackIndex));
        } else {
            Log.d("MEDIA SERVICE", "Playlist finished");
            setFlagPlay(false);
            updatePlaybackState(PlaybackStateCompat.STATE_PAUSED);
            showNotification(R.drawable.play, "Play");
        }
    }

    public void showNotification(int playPauseBtn, String actionText) {
        if (mediaSession == null) return;
        Notification notification = PushNotification(this, 100, playPauseBtn, actionText, mediaSession.getSessionToken());
        startForeground(1, notification);
    }

    public void PlayPause(Context context) {
    if (mediaPlayer == null) return;

    if (!isPrepared) {
        Log.d("MEDIA SERVICE", "Ignoring PlayPause: still preparing");
        return; // or set a pendingPlay=true flag
    }

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


    public void NextPlay() {
        // Skip to next Surah? Or next Ayah?
        // Usually Next button in notification skips to next track.
        // Here, let's skip to next Ayah if available, or just do nothing for now as logic for next Surah is complex (needs fetch).
        if (currentTrackIndex < playlist.size() - 1) {
            currentTrackIndex++;
            MediaPlay(playlist.get(currentTrackIndex));
        }
    }

    public void PreviousPlay() {
        if (currentTrackIndex > 0) {
            currentTrackIndex--;
            MediaPlay(playlist.get(currentTrackIndex));
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (mediaSession != null) {
            mediaSession.release();
        }
    }

    private void updatePlaybackState(int state) {
        if (mediaPlayer != null && mediaSession != null) {
            long currentPosition = mediaPlayer.getCurrentPosition();
            stateBuilder.setState(state, currentPosition, 1.0f);
            mediaSession.setPlaybackState(stateBuilder.build());
        }
    }
}
