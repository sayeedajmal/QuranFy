package com.strong.quranfy.Adaptor;

import static com.strong.quranfy.Adaptor.surah_adaptor.getAudioFile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.strong.quranfy.Models.SearchGet;
import com.strong.quranfy.R;

import java.util.ArrayList;

public class Search_Adaptor extends RecyclerView.Adapter<Search_Adaptor.ViewHolder> {
    Context context;
    ArrayList<SearchGet> list;

    public Search_Adaptor(ArrayList<SearchGet> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public Search_Adaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.searchsample, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Search_Adaptor.ViewHolder holder, int position) {
        SearchGet get = list.get(position);
        holder.SurahNumber.setText(get.getSurahNumber());
        holder.SurahName.setText(get.getSurahName());

        holder.itemView.setOnClickListener(view -> getAudioFile(get.getSurahNumber()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView SurahNumber, SurahName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            SurahName = itemView.findViewById(R.id.SearchSurahName);
            SurahNumber = itemView.findViewById(R.id.SearchSurahNumber);
        }
    }
}
