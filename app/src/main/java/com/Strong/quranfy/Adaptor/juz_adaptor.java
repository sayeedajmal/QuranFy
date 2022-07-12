package com.Strong.quranfy.Adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Strong.quranfy.Models.surah_getter;
import com.Strong.quranfy.R;

import java.util.ArrayList;

public class juz_adaptor extends RecyclerView.Adapter<juz_adaptor.ViewHolder> {
    ArrayList<surah_getter> surah_list;
    Context context;

    public juz_adaptor(ArrayList<surah_getter> surah_getters, Context context) {
        this.surah_list = surah_getters;
        this.context = context;
    }

    @NonNull
    @Override
    public juz_adaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_main, parent, false);
        return new juz_adaptor.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull juz_adaptor.ViewHolder holder, int position) {
        surah_getter getter = surah_list.get(position);

    }

    @Override
    public int getItemCount() {
        return surah_list.size();
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
