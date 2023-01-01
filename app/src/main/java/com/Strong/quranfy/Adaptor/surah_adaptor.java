package com.Strong.quranfy.Adaptor;

import static com.Strong.quranfy.Models.surahData.setSurahInform;
import static com.Strong.quranfy.Models.surahData.setSurahName;
import static com.Strong.quranfy.Models.surahData.setSurahNumber;
import static com.Strong.quranfy.Activity.NotificationService.CHANNEL_ID;
import static com.Strong.quranfy.R.drawable.next;
import static com.Strong.quranfy.R.drawable.play;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.Strong.quranfy.Activity.Dashboard;
import com.Strong.quranfy.Models.SurahArabicGet;
import com.Strong.quranfy.Models.surahInform;
import com.Strong.quranfy.Models.surah_getter;
import com.Strong.quranfy.R;
import com.Strong.quranfy.Activity.mediaService;
import com.Strong.quranfy.Activity.playScreen;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class surah_adaptor extends RecyclerView.Adapter<surah_adaptor.ViewHolder> {
    ArrayList<surah_getter> surah_getters;
    ArrayList<surahInform> SurahInform;
    ArrayList<SurahArabicGet> SurahArabic;
    public static final int REQ_CODE = 100;
    NotificationManagerCompat NotifyComp;
    static Context context;

    private final onClickSendData onClickSendData;

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

            PushNotification(surah_getter.getSurahNumber(), surah_getter.getSurahName(), surahInform.getSurahInformation());

            onClickSendData.onReceiveData(intent);
            mediaService.setFlag(1);
            context.startActivity(intent);
        });
    }

    private void PushNotification(String SurahNumber, String SurahName, String SurahInform) {
        Intent intent = new Intent(context, Dashboard.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, REQ_CODE, intent, PendingIntent.FLAG_IMMUTABLE);

        NotifyComp = NotificationManagerCompat.from(context);
        Bitmap image = BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.quran_img);
        Notification channel = new NotificationCompat.Builder(context, CHANNEL_ID).setLargeIcon(image).setSmallIcon(R.drawable.quran_img).setColor(Color.rgb(255, 255, 255)).setContentTitle(SurahNumber + " - " + SurahName).setContentIntent(pendingIntent).setContentText(SurahInform).setSilent(true).setPriority(NotificationCompat.PRIORITY_HIGH).addAction(play, "Previous", null).addAction(play, "Play", null).addAction(next, "Pause", null).setOnlyAlertOnce(true).setShowWhen(false).build();
        NotifyComp.notify(1, channel);

    }

    @Override
    public int getItemCount() {
        return surah_getters.size();
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

    public interface onClickSendData {
        void onReceiveData(Intent intent);
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

    //THIS IS USED FOR DOWNLOADING THE FILE WHICH YOU HAVE CLICKED ON ITEM_VIEW
    @SuppressLint("DefaultLocale")
    public static void getAudioFile(String SurahNumber) {
        if (Integer.parseInt(SurahNumber) < 10) {
            int surah = Integer.parseInt(SurahNumber);
            String number = String.format("%02d", surah);
            StorageReference mStorageRef = FirebaseStorage.getInstance().getReference().child(number + ".mp3");
            mStorageRef.getDownloadUrl().addOnSuccessListener(surah_adaptor::AudioPlay).addOnFailureListener(e -> Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            StorageReference mStorageRef = FirebaseStorage.getInstance().getReference().child(SurahNumber + ".mp3");
            mStorageRef.getDownloadUrl().addOnSuccessListener(surah_adaptor::AudioPlay).addOnFailureListener(e -> Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private static void AudioPlay(Uri uri) {
        mediaService.MediaPlay(uri.toString());
    }
}
