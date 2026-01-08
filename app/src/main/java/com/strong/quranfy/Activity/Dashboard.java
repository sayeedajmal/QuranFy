package com.strong.quranfy.Activity;

import static com.strong.quranfy.Activity.playScreen.currentTime;
import static com.strong.quranfy.Adaptor.surah_adaptor.PlaySurahNumber;
import static com.strong.quranfy.R.drawable.pause;
import static com.strong.quranfy.R.drawable.play;
import static com.strong.quranfy.Utils.mediaService.isPlaying;
import static com.strong.quranfy.Utils.mediaService.mediaPlayer;
import com.strong.quranfy.Utils.mediaService;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.strong.quranfy.Adaptor.surah_adaptor;
import com.strong.quranfy.Models.Edition;
import com.strong.quranfy.Models.EditionResponse;
import com.strong.quranfy.Models.Surah;
import com.strong.quranfy.Models.SurahResponse;
import com.strong.quranfy.Models.surahData;
import com.strong.quranfy.Network.RetrofitClient;
import com.strong.quranfy.Network.QuranApiService;
import com.strong.quranfy.R;
import com.strong.quranfy.databinding.ActivityDashboardBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Dashboard extends AppCompatActivity implements surah_adaptor.onClickSendData {
    static ActivityDashboardBinding BindDash;
    QuranApiService apiService;
    List<Surah> surahList = new ArrayList<>();
    List<Edition> qariList = new ArrayList<>();
    surah_adaptor adapter;
    public static String selectedQariId = "ar.alafasy"; // Default
    public static boolean isAyahMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BindDash = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(BindDash.getRoot());

        if (mediaPlayer == null) {
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.cancel(1);
        }

        apiService = RetrofitClient.getClient().create(QuranApiService.class);

        // Setup RecyclerView
        BindDash.SurahRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new surah_adaptor(surahList, this);
        BindDash.SurahRecyclerView.setAdapter(adapter);

        // Fetch Data
        getSurahs();
        getQaris();

        SetData();
        addDate();

        Handler current = new Handler();
        final int delay = 500;
        current.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mediaPlayer != null && mediaService.isPrepared) {
                        BindDash.ProgressBar.setMax(mediaPlayer.getDuration());
                        BindDash.PlayStripLoading.setVisibility(View.GONE);
                        BindDash.PlayPauseButton.setVisibility(View.VISIBLE);
                    } else if (mediaPlayer != null) {
                        // Preparing
                        BindDash.PlayStripLoading.setVisibility(View.VISIBLE);
                        BindDash.PlayPauseButton.setVisibility(View.INVISIBLE);
                        
                        // Reset UI elements to avoid showing stale data
                        BindDash.ProgressBar.setProgress(0);
                        BindDash.surahCurrentTime.setText("00:00");
                    }

                    // Notification Action for Play Pause
                    if (!isPlaying) {
                        BindDash.PlayPauseButton.setImageResource(play);
                    } else {
                        BindDash.surahCurrentTime.setText(currentTime);
                        if (mediaPlayer != null && mediaService.isPrepared)
                            BindDash.ProgressBar.setProgress(mediaPlayer.getCurrentPosition(), true);
                        BindDash.PlayPauseButton.setImageResource(pause);
                    }
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
                current.postDelayed(this, delay);
            }
        }, delay);

        //PlayButton
        BindDash.PlayPauseButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, com.strong.quranfy.Utils.mediaService.class);
            intent.setAction("PLAY");
            startService(intent);
        });

        //Next Track
        BindDash.NextTrackButton.setOnClickListener(view -> {
            if (PlaySurahNumber != null && Integer.parseInt(PlaySurahNumber) < 114 && mediaPlayer != null) {
                Intent intent = new Intent(this, com.strong.quranfy.Utils.mediaService.class);
                intent.setAction("NEXT");
                startService(intent);
            } else Toast.makeText(this, "select surah to listen", Toast.LENGTH_SHORT).show();

        });

        //PlayStrip At bottom
        BindDash.playStrip.setOnClickListener(view -> {
            if (mediaPlayer != null) {
                startActivity(new Intent(this, playScreen.class));
            } else Toast.makeText(this, "select surah to listen", Toast.LENGTH_SHORT).show();
        });

        //SharedPreferences setting Data
        SharedPreferences preferences = getSharedPreferences("RecentPlay", Context.MODE_PRIVATE);
        BindDash.PlaySurahNumber.setText(preferences.getString("SurahNumber", ""));
        BindDash.PlaySurahName.setText(preferences.getString("SurahName", ""));
        BindDash.PlaySurahInform.setText(preferences.getString("SurahInform", ""));

        BindDash.Setting.setOnClickListener(v -> startActivity(new Intent(this, Setting.class)));

        // Ayah Mode Toggle
        isAyahMode = preferences.getBoolean("isAyahMode", true);
        BindDash.AyahModeToggle.setChecked(isAyahMode);
        BindDash.AyahModeToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isAyahMode = isChecked;
            preferences.edit().putBoolean("isAyahMode", isChecked).apply();
            // Refresh Qari list because some qaris might only be available in one mode
            getQaris();
        });


        BindDash.SearchButton.setOnClickListener(view -> {
            BindDash.AppBarLayout.setExpanded(true, true);
            BindDash.Search.setVisibility(View.VISIBLE);
            BindDash.Search.setIconified(false);
            BindDash.Search.requestFocus();
        });
        BindDash.Search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filter(s);
                return false;
            }
        });
    }

    private void getSurahs() {
        Call<SurahResponse> call = apiService.getSurah();
        call.enqueue(new Callback<SurahResponse>() {
            @Override
            public void onResponse(@NonNull Call<SurahResponse> call, @NonNull Response<SurahResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    surahList.clear();
                    surahList.addAll(response.body().getData());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<SurahResponse> call, @NonNull Throwable t) {
                Toast.makeText(Dashboard.this, "Failed to fetch Surahs", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getQaris() {
        // If Ayah Mode is ON, we need versebyverse editions.
        // If Ayah Mode is OFF (Surah Mode), we need translation editions.
        Call<EditionResponse> call = apiService.getEditions("audio", null, isAyahMode ? "versebyverse" : "translation");
        call.enqueue(new Callback<EditionResponse>() {
            @Override
            public void onResponse(@NonNull Call<EditionResponse> call, @NonNull Response<EditionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    qariList.clear();
                    qariList.addAll(response.body().getData());
                    setupSpinner();
                }
            }

            @Override
            public void onFailure(@NonNull Call<EditionResponse> call, @NonNull Throwable t) {
                Toast.makeText(Dashboard.this, "Failed to fetch Qaris", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSpinner() {
        List<String> qariNames = qariList.stream()
                .map(q -> q.getName())
                .collect(Collectors.toList());

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, qariNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        BindDash.QariSelection.setAdapter(spinnerAdapter);

        BindDash.QariSelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedQariId = qariList.get(position).getIdentifier();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void filter(String text) {
        List<Surah> filteredList = new ArrayList<>();
        for (Surah item : surahList) {
            if (item.getName().toLowerCase().contains(text.toLowerCase()) || 
                item.getEnglishName().toLowerCase().contains(text.toLowerCase()) ||
                String.valueOf(item.getNumber()).contains(text)) {
                filteredList.add(item);
            }
        }
        adapter.filterList(filteredList);
    }

    @SuppressLint("SetTextI18n")
    private void addDate() {
        Calendar calendar = new GregorianCalendar();
        String Day = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.MONTH, Locale.getDefault());
        String Month = calendar.getDisplayName(Calendar.MONTH, Calendar.MONTH, Locale.getDefault());
        int Date = calendar.get(Calendar.DATE);
        BindDash.TodayDate.setText(Date + " " + Month + ", " + Day);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isPlaying) {
            BindDash.PlayPauseButton.setImageResource(play);
        } else {
            BindDash.PlayPauseButton.setImageResource(pause);
        }
    }

    // SETTING DATA FROM SHARED PREFERENCES TO ...LAST READ...
    public void SetData() {
        @SuppressLint("WrongConstant") SharedPreferences preferences = getSharedPreferences("RecentPlay", MODE_APPEND);
        BindDash.LastReadSurahNum.setText(preferences.getString("SurahNumber", ""));
        BindDash.LastReadSurah.setText(preferences.getString("SurahName", ""));
        BindDash.LastReadSurahArabic.setText(preferences.getString("SurahNameArabic", ""));
    }

    //PLAY STRIP DATA SET WHILE CLICK ON SURAH NAME
    @Override
    public void onReceiveData(Intent intent) {
        BindDash.PlayPauseButton.setImageResource(play);
        BindDash.PlaySurahNumber.setText(intent.getStringExtra("SurahNumber"));
        BindDash.PlaySurahName.setText(intent.getStringExtra("SurahName"));
        BindDash.PlaySurahInform.setText(intent.getStringExtra("SurahInformation"));
    }

    public static void updateList() {
        BindDash.PlaySurahNumber.setText(surahData.getSurahNumber());
        BindDash.PlaySurahName.setText(surahData.getSurahName());
        BindDash.PlaySurahInform.setText(surahData.getSurahInform());
    }
}