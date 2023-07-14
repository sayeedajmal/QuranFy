package com.strong.quranfy.Models;

public class SearchGet {
    String SurahNumber;
    String SurahName;

    public SearchGet(String surahNumber, String surahName) {
        SurahNumber = surahNumber;
        SurahName = surahName;
    }

    public String getSurahNumber() {
        return SurahNumber;
    }

    public String getSurahName() {
        return SurahName;
    }
}
