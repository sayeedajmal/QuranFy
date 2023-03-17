package com.strong.quranfy.Models;

public class qirat_getter {
    String SurahNumber;
    String SurahName;

    public qirat_getter() {

    }

    public String getSurahNumber() {
        return SurahNumber;
    }

    public String getSurahName() {
        return SurahName;
    }

    public qirat_getter(String surahNumber, String surahName) {
        SurahNumber = surahNumber;
        SurahName = surahName;
    }
}
