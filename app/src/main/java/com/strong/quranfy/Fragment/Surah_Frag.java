package com.strong.quranfy.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.strong.quranfy.Adaptor.surah_adaptor;
import com.strong.quranfy.Models.SurahArabicGet;
import com.strong.quranfy.Models.surahInform;
import com.strong.quranfy.Models.surah_getter;
import com.strong.quranfy.databinding.FragmentMainBinding;

import java.util.ArrayList;

public class Surah_Frag extends Fragment {
    FragmentMainBinding BindMain;
    static FirebaseDatabase database;
    static DatabaseReference reference;
    static Query query;
    static ArrayList<surah_getter> SurahName = new ArrayList<>();
    static ArrayList<surahInform> SurahInform = new ArrayList<>();
    static ArrayList<SurahArabicGet> ArabicGet = new ArrayList<>();
    static surah_adaptor adaptor;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        BindMain = FragmentMainBinding.inflate(inflater, container, false);

        adaptor = new surah_adaptor(SurahName, getContext(), SurahInform, ArabicGet);
        BindMain.RecyclerView.setAdapter(adaptor);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        BindMain.RecyclerView.setLayoutManager(linearLayoutManager);

        database = FirebaseDatabase.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("Surah").child("SurahIndex");

        GetSurah();

        return BindMain.getRoot();
    }

    public static void GetSurah() {
        database.getReference().child("Surah").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                SurahName.clear();
                SurahInform.clear();
                ArabicGet.clear();
                /*For SurahName*/
                for (DataSnapshot dataSnapshot : snapshot.child("SurahIndex").getChildren()) {
                    SurahName.add(new surah_getter(dataSnapshot.getKey(), dataSnapshot.getValue(String.class)));
                }
                /*For SurahInformation*/
                for (DataSnapshot dataSnapshot : snapshot.child("SurahInform").getChildren()) {
                    SurahInform.add(new surahInform(dataSnapshot.getValue(String.class)));
                }
                /*For SurahNameArabic*/
                for (DataSnapshot dataSnapshot : snapshot.child("SurahArabic").getChildren()) {
                    ArabicGet.add(new SurahArabicGet(dataSnapshot.getValue(String.class)));
                }
                adaptor.notifyDataSetChanged();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                adaptor.notifyDataSetChanged();
            }
        });

    }

    @SuppressLint("NotifyDataSetChanged")
    public static void SearchSurah(String inputSearch) {
        if (isSurahNo(inputSearch)) {
            query = reference.orderByKey().startAt(inputSearch).endAt(inputSearch + "\uf8ff");
        } else query = reference.orderByValue().startAt(inputSearch).endAt(inputSearch + "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                SurahName.clear();
                SurahInform.clear();
                ArabicGet.clear();
                /*For SurahName*/
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    SurahName.add(new surah_getter(dataSnapshot.getKey(), dataSnapshot.getValue(String.class)));
                    SurahInform.add(new surahInform(""));
                    ArabicGet.add(new SurahArabicGet(""));
                }

                adaptor.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        adaptor.notifyDataSetChanged();
    }

    private static boolean isSurahNo(String Text) {
        try {
            Integer.parseInt(Text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
