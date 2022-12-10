package com.Strong.quranfy.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.Strong.quranfy.Adaptor.favourite_adaptor;
import com.Strong.quranfy.Models.surah_getter;
import com.Strong.quranfy.databinding.FragmentMainBinding;

import java.util.ArrayList;

public class favourite extends Fragment {
    FragmentMainBinding BindMainFrag;
    ArrayList<surah_getter> surah_getters = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        BindMainFrag = FragmentMainBinding.inflate(inflater, container, false);

        favourite_adaptor surah_adaptor = new favourite_adaptor(surah_getters, getContext());
        BindMainFrag.RecyclerView.setAdapter(surah_adaptor);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        BindMainFrag.RecyclerView.setLayoutManager(layoutManager);
        return BindMainFrag.getRoot();
    }
}