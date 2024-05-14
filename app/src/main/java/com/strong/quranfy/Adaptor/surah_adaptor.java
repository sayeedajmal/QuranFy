package com.strong.quranfy.Adaptor;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.strong.quranfy.Activity.Dashboard.updateList;
import static com.strong.quranfy.Models.surahData.setSurahInform;
import static com.strong.quranfy.Models.surahData.setSurahName;
import static com.strong.quranfy.Models.surahData.setSurahNumber;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.strong.quranfy.Activity.playScreen;
import com.strong.quranfy.Models.SurahArabicGet;
import com.strong.quranfy.Models.surahInform;
import com.strong.quranfy.Models.surah_getter;
import com.strong.quranfy.R;
import com.strong.quranfy.Utils.MediaPanel;
import com.strong.quranfy.Utils.mediaService;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class surah_adaptor extends RecyclerView.Adapter<surah_adaptor.ViewHolder> {
    public static final int REQ_CODE = 100;
    public static Context context;
    public static String PlaySurahNumber;
    private final onClickSendData onClickSendData;
    static ArrayList<surah_getter> surah_getters;
    static ArrayList<surahInform> SurahInform;
    static int lyricId;
    ArrayList<SurahArabicGet> SurahArabic;
    public static int POSITION;

    public surah_adaptor(ArrayList<surah_getter> surah_getters, Context context, ArrayList<surahInform> surahInform, ArrayList<SurahArabicGet> surahArabic) {
        surah_adaptor.context = context;
        surah_adaptor.surah_getters = surah_getters;
        SurahInform = surahInform;
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

        lyricId = context.getResources().getIdentifier("_" + surah_getter.getSurahNumber(), "raw", context.getPackageName());
        for (int i = 1; i <= position; i++)
            if (lyricId != 0) {
                holder.ReadImage.setVisibility(View.VISIBLE);
            } else holder.ReadImage.setVisibility(View.GONE);


        //Clicking The ItemView or Surah List
        holder.itemView.setOnClickListener(view -> {
            //Implementation of song download
            PlaySurahNumber = surah_getter.getSurahNumber();

            setPOSITION(position);

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

            MediaPanel.PushNotification(context, REQ_CODE, R.drawable.pause, "Pause");

            onClickSendData.onReceiveData(intent);

            mediaService.setFlagPlay(true);

            context.startActivity(intent);
        });

        //        Check Already File is Stored or Not
        holder.DownloadButton.setVisibility(!checkFile(surah_getter.getSurahNumber()) ? View.VISIBLE : View.INVISIBLE);

        //Download Button Getting The Surah And Store to the App's Storage
        holder.DownloadButton.setOnClickListener(v -> {
            String surahNumber = surah_getter.getSurahNumber();

            StorageReference mStorageRef;
            if (Integer.parseInt(surahNumber) < 10) {
                int surah = Integer.parseInt(surahNumber);
                @SuppressLint("DefaultLocale") String number = String.format("%02d", surah);
                mStorageRef = FirebaseStorage.getInstance().getReference().child(number + ".mp3");
            } else {
                mStorageRef = FirebaseStorage.getInstance().getReference().child(surahNumber + ".mp3");
            }

            File[] directory = context.getExternalFilesDirs(Environment.DIRECTORY_MUSIC);
            File file = new File(directory[0], mStorageRef.getName());

            mStorageRef.getFile(file).addOnSuccessListener(taskSnapshot -> {
                Toast.makeText(context, "Surah Saved", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> Snackbar.make(v, Objects.requireNonNull(e.getLocalizedMessage()), Snackbar.LENGTH_SHORT).show());

        });

    }

    private boolean checkFile(String surahNumber) {
        File[] directory = context.getExternalFilesDirs(Environment.DIRECTORY_MUSIC);
        File fileUri;
        if (Integer.parseInt(surahNumber) < 10) {
            @SuppressLint("DefaultLocale") String number = String.format("%02d", Integer.parseInt(surahNumber));
            fileUri = new File(directory[0], number + ".mp3");
        } else {
            fileUri = new File(directory[0], surahNumber + ".mp3");
        }
        return fileUri.exists();
    }

    //THIS IS USED FOR DOWNLOADING THE FILE WHICH YOU HAVE CLICKED ON ITEM_VIEW
    @SuppressLint("DefaultLocale")
    public static void getAudioFile(String SurahNumber) {
        StorageReference mStorageRef;
        File[] directory = context.getExternalFilesDirs(Environment.DIRECTORY_MUSIC);
        File file;

        if (Integer.parseInt(SurahNumber) < 10) {
            int surah = Integer.parseInt(SurahNumber);
            String number = String.format("%02d", surah);
            file = new File(directory[0], number + ".mp3");
        } else {
            file = new File(directory[0], SurahNumber + ".mp3");
        }
        if (file.exists()) {
            mediaService.localSurah(Uri.fromFile(file));
        } else {
            if (Integer.parseInt(SurahNumber) < 10) {
                int surah = Integer.parseInt(SurahNumber);
                String number = String.format("%02d", surah);
                mStorageRef = FirebaseStorage.getInstance().getReference().child(number + ".mp3");
            } else {
                mStorageRef = FirebaseStorage.getInstance().getReference().child(SurahNumber + ".mp3");
            }

            mStorageRef.getDownloadUrl().addOnSuccessListener(surah_adaptor::AudioPlay).addOnFailureListener(e -> Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }


    private static void AudioPlay(Uri uri) {
        mediaService.MediaPlay(uri, context);
    }

    //Update Data on Next or Previous Button
    public static void UpdateData() {
        surah_getter surah_getter = surah_getters.get(POSITION);
        surahInform surahInform = SurahInform.get(POSITION);

        setSurahNumber(surah_getter.getSurahNumber());
        setSurahName(surah_getter.getSurahName());
        setSurahInform(surahInform.getSurahInformation());

        updateList();
        MediaPanel.PushNotification(context, REQ_CODE, R.drawable.pause, "Pause");
    }

    public static void closeNotification() {
        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(1);
    }

    @Override
    public int getItemCount() {
        return surah_getters.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView surahNumber;
        public TextView surahName;
        public TextView surahInformation;
        public TextView surahNameArabic;
        public CircleImageView ReadImage;
        public ImageButton DownloadButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            surahNumber = itemView.findViewById(R.id.surahNumber);
            surahName = itemView.findViewById(R.id.surahName);
            surahInformation = itemView.findViewById(R.id.surahInformation);
            surahNameArabic = itemView.findViewById(R.id.surahNameArabic);
            ReadImage = itemView.findViewById(R.id.surahRead);
            DownloadButton = itemView.findViewById(R.id.download);
        }
    }

    public static void setPOSITION(int POSITION) {
        surah_adaptor.POSITION = POSITION;
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
