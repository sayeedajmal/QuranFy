package com.Strong.quranfy.Adaptor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Strong.quranfy.GetSet.surahInform;
import com.Strong.quranfy.GetSet.surah_getter;
import com.Strong.quranfy.R;
import com.Strong.quranfy.playScreen;

import java.util.ArrayList;

public class surah_adaptor extends RecyclerView.Adapter<surah_adaptor.ViewHolder>{
     ArrayList<surah_getter> surah_getters;
     ArrayList<surahInform> SurahInform;
    Context context;
    private final onClickSendData onClickSendData;
    public surah_adaptor(ArrayList<surah_getter> surah_getters,Context context, ArrayList<surahInform> surahInform) {
        this.context=context;
        this.surah_getters=surah_getters;
        this.SurahInform=surahInform;
        try{
            this.onClickSendData=((surah_adaptor.onClickSendData) context);
        }catch (ClassCastException e){
            throw new ClassCastException(e.getMessage());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(context).inflate(R.layout.indexsample, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        surah_getter surah_getter =surah_getters.get(position);
        surahInform surahInform=SurahInform.get(position);

        holder.surahNumber.setText(surah_getter.getSurahNumber());
        holder.surahName.setText(surah_getter.getSurahName());
        holder.surahInformation.setText(surahInform.getSurahInformation());
       /* holder.surahNameArabic.setText(surah_getter.getSurahNameArabic());*/

        holder.itemView.setOnClickListener(view ->{
            Intent intent=new Intent(context, playScreen.class);
            /*Sending Data From RecyclerView to PlayScreen*/
            intent.putExtra("SurahNumber",surah_getter.getSurahNumber());
            intent.putExtra("SurahName",surah_getter.getSurahName());
            intent.putExtra("SurahInformation",surahInform.getSurahInformation());
            onClickSendData.onReceiveData(intent);
            //Sending the Data to SharedPreference
            DataPref(surah_getter.getSurahNumber(), surah_getter.getSurahName(), surahInform.getSurahInformation());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return surah_getters.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView surahNumber, surahName, surahInformation, surahNameArabic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            surahNumber=itemView.findViewById(R.id.surahNumber);
            surahName=itemView.findViewById(R.id.surahName);
            surahInformation=itemView.findViewById(R.id.surahInformation);
            surahNameArabic=itemView.findViewById(R.id.surahNameArabic);
        }
    }

 public interface onClickSendData{
        public void onReceiveData(Intent intent);
    }
        //DataPreference Setup
    public void DataPref(String SurahNumber, String SurahName, String SurahNameArabic){
        SharedPreferences preferences= context.getSharedPreferences("RecentPlay",Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor=preferences.edit();
        prefEditor.putString("SurahNumber",SurahNumber);
        prefEditor.putString("SurahName",SurahName);
        prefEditor.putString("SurahNameArabic",SurahNameArabic);
        prefEditor.apply();
    }
}
