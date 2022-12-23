package com.Strong.quranfy.Adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Strong.quranfy.Models.fav_getter;
import com.Strong.quranfy.R;

import java.util.ArrayList;

public class favourite_adaptor extends RecyclerView.Adapter<favourite_adaptor.ViewHolder> {
    ArrayList<fav_getter> fav_list;
    Context context;

    public favourite_adaptor(ArrayList<fav_getter> surah_getters, Context context) {
        this.fav_list = surah_getters;
        this.context = context;
    }

    @NonNull
    @Override
    public favourite_adaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.favsample, parent, false);
        return new favourite_adaptor.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull favourite_adaptor.ViewHolder holder, int position) {
        fav_getter getter = fav_list.get(position);
        holder.surahNumber.setText(getter.getSurahNumber());
        holder.surahName.setText(getter.getSurahName());
    }

    @Override
    public int getItemCount() {
        return fav_list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView surahNumber, surahName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            surahNumber = itemView.findViewById(R.id.surahNumber);
            surahName = itemView.findViewById(R.id.surahName);
        }
    }
}
