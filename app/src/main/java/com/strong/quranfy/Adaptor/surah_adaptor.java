package com.strong.quranfy.Adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.strong.quranfy.Activity.Dashboard;
import com.strong.quranfy.Activity.playScreen;
import com.strong.quranfy.Models.Surah;
import com.strong.quranfy.Models.surahData;
import com.strong.quranfy.R;
import com.strong.quranfy.Utils.mediaService;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class surah_adaptor extends RecyclerView.Adapter<surah_adaptor.ViewHolder> {
    public static Context context;
    public static String PlaySurahNumber;
    private final onClickSendData onClickSendData;
    List<Surah> surahList;
    List<Surah> surahListFull; // For filtering

    public surah_adaptor(List<Surah> surahList, Context context) {
        surah_adaptor.context = context;
        this.surahList = surahList;
        this.surahListFull = new ArrayList<>(surahList);
        try {
            this.onClickSendData = ((surah_adaptor.onClickSendData) context);
        } catch (ClassCastException e) {
            throw new ClassCastException(e.getMessage());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.indexsample, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Surah surah = surahList.get(position);

        holder.surahNumber.setText(String.valueOf(surah.getNumber()));
        holder.surahName.setText(surah.getEnglishName());
        holder.surahInformation.setText(surah.getEnglishNameTranslation() + " - " + surah.getNumberOfAyahs() + " Ayahs");
        holder.surahNameArabic.setText(surah.getName().replace("سُورَةُ", "").trim());

        holder.readIconContainer.setVisibility(View.GONE); // Hide read icon for now
        holder.downloadIconContainer.setVisibility(View.GONE); // Hide download icon for now

        //Clicking The ItemView or Surah List
        holder.itemView.setOnClickListener(view -> {
            PlaySurahNumber = String.valueOf(surah.getNumber());

            // Generate Playlist
            ArrayList<String> playlist = new ArrayList<>();
            String reciterId = Dashboard.selectedQariId;
            for (int i = 1; i <= surah.getNumberOfAyahs(); i++) {
                // Format: https://cdn.islamic.network/quran/audio/128/{reciterId}/{ayahNumber}.mp3
                // Note: Ayah number here needs to be absolute or relative?
                // The API documentation says {ayahNumber} is absolute (1-6236).
                // Wait, if I use /quran/{edition}, I get all ayahs.
                // If I construct URL manually, I need absolute ayah number.
                // Calculating absolute ayah number is hard without metadata.
                // ALTERNATIVE: Use the API to get the Surah audio URLs.
                // But that requires a network call.
                // The user requirements said: "when i selct a qari then in the backend it have to select that qari"
                // And "Audio files are not always returned directly... but you can extract audio file URLs... or construct them"
                // Constructing absolute ayah numbers requires knowing the start index of each Surah.
                
                // Let's use the /surah/{number}/{edition} endpoint in mediaService or here?
                // If I do it here, I need to fetch it.
                // Better approach: Pass the Surah Number and Reciter ID to MediaService, and let it fetch the audio list?
                // OR: Just play the full surah audio if available?
                // The user said "gapless playback of Ayah-by-Ayah".
                
                // Let's check if there is an endpoint for Surah Audio.
                // GET /surah/{surah}/{edition} returns all ayahs with audio.
                // This is the best way.
            }
            
            // So, instead of passing a list of URLs, let's pass the Surah Number and Reciter ID to the service,
            // and let the service fetch the audio list.
            // OR, fetch it here and pass it. Fetching here is better for UI feedback.
            
            // For now, I'll update the adapter to just trigger the intent with Surah details.
            // I will implement a method in Dashboard or a Helper to fetch audio and start service.
            
            // Actually, `mediaService` should probably handle the fetching to keep UI responsive?
            // No, Service is for playing.
            
            // Let's Fetch Audio URLs here (asynchronously) then start service.
            fetchAudioAndPlay(surah.getNumber(), reciterId, surah);
        });
    }
    
    private void fetchAudioAndPlay(int surahNumber, String reciterId, Surah surah) {
        android.widget.Toast.makeText(context, "Loading Audio...", android.widget.Toast.LENGTH_SHORT).show();

        if (!Dashboard.isAyahMode) {
            // Full Surah Mode
            ArrayList<String> audioUrls = new ArrayList<>();
            // Construct URL: https://cdn.islamic.network/quran/audio-surah/128/{reciterId}/{surahNumber}.mp3
            String surahUrl = "https://cdn.islamic.network/quran/audio-surah/128/" + reciterId + "/" + surahNumber + ".mp3";
            audioUrls.add(surahUrl);

            android.util.Log.d("SURAH_ADAPTER", "Full Surah Mode - URL: " + surahUrl);
            startMediaService(audioUrls, surah, surahNumber);
        } else {
            // Ayah by Ayah Mode
            com.strong.quranfy.Network.QuranApiService apiService = com.strong.quranfy.Network.RetrofitClient.getClient().create(com.strong.quranfy.Network.QuranApiService.class);
            retrofit2.Call<com.google.gson.JsonObject> call = apiService.getSurahWithEdition(surahNumber, reciterId);

            call.enqueue(new retrofit2.Callback<com.google.gson.JsonObject>() {
                @Override
                public void onResponse(retrofit2.Call<com.google.gson.JsonObject> call, retrofit2.Response<com.google.gson.JsonObject> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        com.google.gson.JsonObject data = response.body().getAsJsonObject("data");
                        com.google.gson.JsonArray ayahs = data.getAsJsonArray("ayahs");
                        ArrayList<String> audioUrls = new ArrayList<>();

                        android.util.Log.d("SURAH_ADAPTER", "Total ayahs in response: " + ayahs.size());

                        for (com.google.gson.JsonElement ayah : ayahs) {
                            String audioUrl = ayah.getAsJsonObject().get("audio").getAsString();
                            android.util.Log.d("SURAH_ADAPTER", "Audio URL: " + audioUrl);
                            if (audioUrl != null && !audioUrl.isEmpty()) {
                                audioUrls.add(audioUrl);
                            }
                        }

                        if (audioUrls.isEmpty()) {
                            android.widget.Toast.makeText(context, "No audio available for this Surah", android.widget.Toast.LENGTH_SHORT).show();
                            return;
                        }

                        startMediaService(audioUrls, surah, surahNumber);
                    } else {
                        android.widget.Toast.makeText(context, "Failed to load audio", android.widget.Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<com.google.gson.JsonObject> call, Throwable t) {
                    android.widget.Toast.makeText(context, "Error: " + t.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void startMediaService(ArrayList<String> audioUrls, Surah surah, int surahNumber) {
        android.util.Log.d("SURAH_ADAPTER", "Starting service with playlist size: " + audioUrls.size());

        // Start Service
        Intent intent = new Intent(context, mediaService.class);
        intent.setAction("PLAY_PLAYLIST");
        intent.putStringArrayListExtra("playlist", audioUrls);
        intent.putExtra("surahName", surah.getEnglishName());
        intent.putExtra("surahInfo", surah.getEnglishNameTranslation());
        context.startService(intent);

        // Update UI
        playScreen.currentSurahNumber = String.valueOf(surahNumber);
        Intent playIntent = new Intent(context, playScreen.class);
        playIntent.putExtra("SurahNumber", String.valueOf(surahNumber));
        playIntent.putExtra("SurahName", surah.getEnglishName());
        playIntent.putExtra("SurahInformation", surah.getEnglishNameTranslation());

        // Shared Prefs
        DataPref(String.valueOf(surahNumber), surah.getEnglishName(), surah.getName(), surah.getEnglishNameTranslation());

        surahData.setSurahNumber(String.valueOf(surahNumber));
        surahData.setSurahName(surah.getEnglishName());
        surahData.setSurahInform(surah.getEnglishNameTranslation());

        onClickSendData.onReceiveData(playIntent);
        mediaService.setFlagPlay(true);
        context.startActivity(playIntent);
    }

    @Override
    public int getItemCount() {
        return surahList.size();
    }

    public void filterList(List<Surah> filteredList) {
        surahList = filteredList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView surahNumber;
        public TextView surahName;
        public TextView surahInformation;
        public TextView surahNameArabic;
        public CircleImageView ReadImage;
        public androidx.cardview.widget.CardView readIconContainer;
        public androidx.cardview.widget.CardView downloadIconContainer;
        public ImageButton DownloadButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            surahNumber = itemView.findViewById(R.id.surahNumber);
            surahName = itemView.findViewById(R.id.surahName);
            surahInformation = itemView.findViewById(R.id.surahInformation);
            surahNameArabic = itemView.findViewById(R.id.surahNameArabic);
            ReadImage = itemView.findViewById(R.id.surahRead);
            readIconContainer = itemView.findViewById(R.id.readIconContainer);
            downloadIconContainer = itemView.findViewById(R.id.downloadIconContainer);
            DownloadButton = itemView.findViewById(R.id.download);
        }
    }

    //DataPreference Setup
    public void DataPref(String SurahNumber, String SurahName, String SurahNameArabic, String SurahInform) {
        SharedPreferences preferences = context.getSharedPreferences("RecentPlay", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = preferences.edit();
        prefEditor.putString("SurahNumber", SurahNumber);
        prefEditor.putString("SurahName", SurahName);
        prefEditor.putString("SurahNameArabic", SurahNameArabic);
        prefEditor.putString("SurahInform", SurahInform);
        prefEditor.apply();
    }

    /*This Interface is used for Dashboard strip*/
    public interface onClickSendData {
        void onReceiveData(Intent intent);
    }
}
