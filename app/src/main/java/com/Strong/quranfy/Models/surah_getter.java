package com.Strong.quranfy.Models;

public class surah_getter {
   String SurahNumber;
   String SurahName;

    public surah_getter(){

   }

    public surah_getter(String surahNumber, String surahName) {
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
