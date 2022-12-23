package com.Strong.quranfy.Models;

import android.content.Context;
import android.content.SharedPreferences;

public class FavSurahStore {
    private void SetFav(Context context, String SurahNumber) {
        SharedPreferences preferences = context.getSharedPreferences("FavSurah", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("SNumber", SurahNumber);
        editor.apply();
    }
}
