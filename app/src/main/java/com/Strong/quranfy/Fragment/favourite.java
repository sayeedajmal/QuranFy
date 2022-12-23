package com.Strong.quranfy.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.Strong.quranfy.Adaptor.favourite_adaptor;
import com.Strong.quranfy.Models.fav_getter;
import com.Strong.quranfy.databinding.FragmentFavBinding;

import java.util.ArrayList;

public class favourite extends Fragment {
    FragmentFavBinding BindFav;
    ArrayList<fav_getter> fav_getters = new ArrayList<>();

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
        BindFav = FragmentFavBinding.inflate(inflater, container, false);

       /* fav_getters.add(new fav_getter("01", "Al-Fateha"));
        fav_getters.add(new fav_getter("01", "Al-Fateha"));
        fav_getters.add(new fav_getter("01", "Al-Fateha"));
        fav_getters.add(new fav_getter("01", "Al-Fateha"));
        fav_getters.add(new fav_getter("01", "Al-Fateha"));
        fav_getters.add(new fav_getter("01", "Al-Fateha"));*/
        favourite_adaptor favourite_adaptor = new favourite_adaptor(fav_getters, getContext());
        BindFav.RecyclerView.setAdapter(favourite_adaptor);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        BindFav.RecyclerView.setLayoutManager(layoutManager);
        return BindFav.getRoot();
    }
}