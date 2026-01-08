package com.strong.quranfy.Network;

import com.strong.quranfy.Models.EditionResponse;
import com.strong.quranfy.Models.SurahResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface QuranApiService {
    @GET("surah")
    Call<SurahResponse> getSurah();

    @GET("edition")
    Call<EditionResponse> getEditions(@Query("format") String format, @Query("language") String language, @Query("type") String type);

    @GET("surah/{number}/{edition}")
    Call<com.google.gson.JsonObject> getSurahWithEdition(@retrofit2.http.Path("number") int number, @retrofit2.http.Path("edition") String edition);
}
