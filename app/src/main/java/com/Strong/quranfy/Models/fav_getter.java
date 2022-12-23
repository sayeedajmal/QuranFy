package com.Strong.quranfy.Models;

public class fav_getter {
    String SurahNumber;
    String SurahName;

    public fav_getter() {

    }

    public String getSurahNumber() {
        return SurahNumber;
    }

    public String getSurahName() {
        return SurahName;
    }

    public fav_getter(String surahNumber, String surahName) {
        SurahNumber = surahNumber;
        SurahName = surahName;
    }
}
