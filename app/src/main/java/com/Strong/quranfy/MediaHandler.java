package com.Strong.quranfy;

import com.Strong.quranfy.Models.AudioUri;

public class MediaHandler {

    //Method of PlayAudio
    public void playAudio() {
        AudioUri audioUri = new AudioUri();
        String audiURL = audioUri.getAudioURL();
        System.out.println("<<<<<<<<<<" + audiURL);
    }
}