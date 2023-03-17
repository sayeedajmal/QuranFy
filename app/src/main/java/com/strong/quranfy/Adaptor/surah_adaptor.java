package com.strong.quranfy.Adaptor;

import static com.strong.quranfy.Models.surahData.setSurahInform;
import static com.strong.quranfy.Models.surahData.setSurahName;
import static com.strong.quranfy.Models.surahData.setSurahNumber;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.strong.quranfy.Activity.mediaService;
import com.strong.quranfy.Activity.playScreen;
import com.strong.quranfy.Models.SurahArabicGet;
import com.strong.quranfy.Models.surahInform;
import com.strong.quranfy.Models.surah_getter;
import com.strong.quranfy.Notification.MediaPanel;
import com.strong.quranfy.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

public class surah_adaptor extends RecyclerView.Adapter<surah_adaptor.ViewHolder> {
    public static final int REQ_CODE = 100;
    static Context context;
    private final onClickSendData onClickSendData;
    ArrayList<surah_getter> surah_getters;
    ArrayList<surahInform> SurahInform;
    ArrayList<SurahArabicGet> SurahArabic;

    public surah_adaptor(ArrayList<surah_getter> surah_getters, Context context, ArrayList<surahInform> surahInform, ArrayList<SurahArabicGet> surahArabic) {
        surah_adaptor.context = context;
        this.surah_getters = surah_getters;
        this.SurahInform = surahInform;
        this.SurahArabic = surahArabic;
        try {
            this.onClickSendData = ((surah_adaptor.onClickSendData) context);
        } catch (ClassCastException e) {
            throw new ClassCastException(e.getMessage());
        }
    }

    //THIS IS USED FOR DOWNLOADING THE FILE WHICH YOU HAVE CLICKED ON ITEM_VIEW
    @SuppressLint("DefaultLocale")
    public static void getAudioFile(String SurahNumber) {
        if (Integer.parseInt(SurahNumber) < 10) {
            int surah = Integer.parseInt(SurahNumber);
            String number = String.format("%02d", surah);
            StorageReference mStorageRef = FirebaseStorage.getInstance().getReference().child(number + ".mp3");
            mStorageRef.getDownloadUrl().addOnSuccessListener(surah_adaptor::AudioPlay).addOnFailureListener(e -> Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            int Number = Integer.parseInt(SurahNumber);
            if (Number == 70) {
                AudioPlay(Uri.parse("android.resource://com.strong.quranfy/" + R.raw.__70));
            } else {
                StorageReference mStorageRef = FirebaseStorage.getInstance().getReference().child(SurahNumber + ".mp3");
                mStorageRef.getDownloadUrl().addOnSuccessListener(surah_adaptor::AudioPlay).addOnFailureListener(e -> Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }
    }

    public static Uri getRawUri(String filename) {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + File.pathSeparator + File.separator + context.getPackageName() + "/raw/" + filename);
    }

    private static void AudioPlay(Uri uri) {
        mediaService.MediaPlay(uri.toString());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.indexsample, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        surah_getter surah_getter = surah_getters.get(position);
        surahInform surahInform = SurahInform.get(position);
        SurahArabicGet ArabicGet = SurahArabic.get(position);

        holder.surahNumber.setText(surah_getter.getSurahNumber());
        holder.surahName.setText(surah_getter.getSurahName());
        holder.surahInformation.setText(surahInform.getSurahInformation());
        holder.surahNameArabic.setText(ArabicGet.getSurahArabic());

        //Clicking The ItemView or Surah List
        holder.itemView.setOnClickListener(view -> {
            //Implementation of song download
            getAudioFile(surah_getter.getSurahNumber());

            //Setting CurrentSurahNumber
            playScreen.currentSurahNumber = surah_getter.getSurahNumber();

            Intent intent = new Intent(context, playScreen.class);
            /*Sending Data From RecyclerView to PlayScreen*/
            intent.putExtra("SurahNumber", surah_getter.getSurahNumber());
            intent.putExtra("SurahName", surah_getter.getSurahName());
            intent.putExtra("SurahInformation", surahInform.getSurahInformation());

            mediaService.CheckMediaPlaying();

            //Sending the Data to SharedPreference
            DataPref(surah_getter.getSurahNumber(), surah_getter.getSurahName(), ArabicGet.getSurahArabic(), surahInform.getSurahInformation());
            setSurahNumber(surah_getter.getSurahNumber());
            setSurahName(surah_getter.getSurahName());
            setSurahInform(surahInform.getSurahInformation());

            MediaPanel.PushNotification(surah_getter.getSurahNumber(), surah_getter.getSurahName(), surahInform.getSurahInformation(), context, REQ_CODE);

            onClickSendData.onReceiveData(intent);
            mediaService.setFlag(1);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return surah_getters.size();
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

    public interface onClickSendData {
        void onReceiveData(Intent intent);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView surahNumber, surahName, surahInformation, surahNameArabic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            surahNumber = itemView.findViewById(R.id.surahNumber);
            surahName = itemView.findViewById(R.id.surahName);
            surahInformation = itemView.findViewById(R.id.surahInformation);
            surahNameArabic = itemView.findViewById(R.id.surahNameArabic);
        }
    }
}
