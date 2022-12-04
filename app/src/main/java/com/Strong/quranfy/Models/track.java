package com.Strong.quranfy.Models;

public class track {
    public String getSurahNumber() {
        return SurahNumber;
    }

    public void setSurahNumber(String surahNumber) {
        SurahNumber = surahNumber;
    }

    public String getSurahName() {
        return SurahName;
    }

    public void setSurahName(String surahName) {
        SurahName = surahName;
    }

    public String getSurahInform() {
        return SurahInform;
    }

    public void setSurahInform(String surahInform) {
        SurahInform = surahInform;
    }

    private String SurahNumber, SurahName, SurahInform;
}
