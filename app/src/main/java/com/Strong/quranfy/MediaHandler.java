package com.Strong.quranfy;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.Strong.quranfy.Models.surah_getter;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

public class MediaHandler {
    static MediaPlayer mediaPlayer;
    surah_getter URL=new surah_getter();
    public PlayPauseButton playPauseButton;
    //THIS IS USED FOR DOWNLOADING THE FILE WHICH YOU HAVE CLICKED ON ITEM_VIEW
    public void getAudioFile(String SurahNumber, Context context) {
        // Getting AudioFile From FireStorage
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference().child(SurahNumber + ".mp3");
        mStorageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            URL.setURL(uri.toString());
        }
        ).addOnFailureListener(Throwable::printStackTrace);
    }

    public void checkPlayingOrNot(Context context) throws IOException {
        //Checking if any song playing or not
        mediaPlayer = new MediaPlayer();
        //we will start mediaPlayer if currently there is no songs in it
        mediaPlayer.start();
        mediaPlayer.release();
    }

    //Thread to update the seekBar while playing song
    public void setUpdateSeekBar(SeekBar seekMusicBar) {
      /*  Thread updateSeekBar = new Thread(() -> {

            int TotalDuration = mediaPlayer.getDuration();
            int CurrentPosition = 0;

            while (CurrentPosition < TotalDuration) {
                try {

                    Thread.sleep(500);
                    CurrentPosition = mediaPlayer.getCurrentPosition();
                    seekMusicBar.setProgress(CurrentPosition);

                } catch (InterruptedException | IllegalStateException e) {

                    e.printStackTrace();
                }
            }

        });
        seekMusicBar.setMax(mediaPlayer.getDuration());
        updateSeekBar.start(); */
        //Setting the seekbar's max progress to the maximum duration of the media file
    }

    //Setting the Music player from the position of the seekbar
    public void SeekBarUpdate(SeekBar seekMusicBar, TextView txtSongStart, TextView txtSongEnd) {
       /* seekMusicBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                //getting the progress of the seek bar and setting it to Media Player
                mediaPlayer.seekTo(seekBar.getProgress());

            }
        });


        //Creating the Handler to update the current duration
        final Handler handler = new Handler();
        final int delay = 1000;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                //Getting the current duration from the media player
                String currentTime = createDuration(mediaPlayer.getCurrentPosition());
                String songEndTime=createDuration(mediaPlayer.getDuration()-mediaPlayer.getCurrentPosition());
                txtSongEnd.setText(songEndTime);
                //Setting the current duration in textView
                txtSongStart.setText(currentTime);
                handler.postDelayed(this, delay);

            }
        }, delay);
        */
    }

    //Implementing OnClickListener for Play and Pause Button
    public void ButtonPlayPause(ImageButton ButtonPlayPause) {
            //Checking playing any songs or not
        /*   if (mediaPlayer.isPlaying()) {

                //setting the play icon
                ButtonPlayPause.setBackgroundResource(R.drawable.play);

                //Pausing the current media
                mediaPlayer.pause();

            } else {

                //Setting the pause icon
                ButtonPlayPause.setBackgroundResource(R.drawable.pause);

                //Starting the media player
                mediaPlayer.start();

            } */
    }

    //Performing the Button Click Operation after the completion of song
    public void mediaAfterComplete(ImageButton btnNext) {
        //mediaPlayer.setOnCompletionListener(mediaPlayer -> btnNext.performClick());
    }

    //Implementing OnclickListener
    public void ButtonNext(ImageButton btnNext, Context context) {
           System.out.print("<<<<<<<<<<<<<<"+URL.getURL());

          /*  //Stopping the currently playing media
            mediaPlayer.stop();
            mediaPlayer.release();


            //Getting the Current media position and incrementing it by 1
            position = ((position + 1) % mySongs.size());

            //Extracting the media path form the array List
            Uri uri1 = Uri.parse(mySongs.get(position).toString());

            //Setting the path to the media player
            mediaPlayer = MediaPlayer.create(context.getApplicationContext(), uri1);


            //Getting the current song Name and setting it to TextView
            songName = mySongs.get(position).getName();
            txtSongName.setText(songName);

            //Starting the Media Player
            mediaPlayer.start();

            //Extracting the duration of the song
            songEndTime();
            */
    }

    //Implementing the OnClick Listener
    public void ButtonPrevious(ImageButton btnPrevious, Context context) {
          /*  //Stoping the media Player
            mediaPlayer.stop();
            mediaPlayer.release();


            //getting the  current media position and decrementing it by one
            position = ((position - 1) % mySongs.size());
            if (position < 0)
                position = mySongs.size() - 1;

            //Extracting the media path
            Uri uri1 = Uri.parse(mySongs.get(position).toString());

            //Setting the media path to the media player
            mediaPlayer = MediaPlayer.create(context.getApplicationContext(), uri1);
            songName = mySongs.get(position).getName();
            txtSongName.setText(songName);
            mediaPlayer.start();
            songEndTime(); */
    }

    //Preparing the Time format for setting to textView
    public String createDuration(int duration) {

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


    public interface PlayPauseButton{
        void SendPlayPause(ImageButton button);
    }
}