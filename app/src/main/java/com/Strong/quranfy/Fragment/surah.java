package com.Strong.quranfy.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.Strong.quranfy.Adaptor.surah_adaptor;
import com.Strong.quranfy.Models.SurahArabicGet;
import com.Strong.quranfy.Models.surahInform;
import com.Strong.quranfy.Models.surah_getter;
import com.Strong.quranfy.databinding.FragmentMainBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class surah extends Fragment {
    FragmentMainBinding BindMain;
    FirebaseDatabase database;
    ArrayList<surah_getter> SurahName = new ArrayList<>();
    ArrayList<surahInform> SurahInform = new ArrayList<>();
    ArrayList<SurahArabicGet> ArabicGet = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        BindMain = FragmentMainBinding.inflate(inflater, container, false);

        surah_adaptor surah_adaptor = new surah_adaptor(SurahName, getContext(), SurahInform, ArabicGet);
        BindMain.RecyclerView.setAdapter(surah_adaptor);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        BindMain.RecyclerView.setLayoutManager(linearLayoutManager);

        database = FirebaseDatabase.getInstance();

        database.getReference().addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                /*For SurahName*/
                for (DataSnapshot dataSnapshot : snapshot.child("SurahIndex").getChildren()) {
                    SurahName.add(new surah_getter(dataSnapshot.getKey(),
                            dataSnapshot.getValue(String.class)));
                }
                /*For SurahInformation*/
                for (DataSnapshot dataSnapshot : snapshot.child("SurahInform").getChildren()) {
                    SurahInform.add(new surahInform(dataSnapshot.getValue(String.class)));
                }
                /*For SurahNameArabic*/
                for (DataSnapshot dataSnapshot : snapshot.child("SurahArabic").getChildren()) {
                    ArabicGet.add(new SurahArabicGet(dataSnapshot.getValue(String.class)));
                }
                surah_adaptor.notifyDataSetChanged();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Can't Fetched List", Toast.LENGTH_SHORT).show();
                surah_adaptor.notifyDataSetChanged();
            }
        });

        return BindMain.getRoot();
    }
}
