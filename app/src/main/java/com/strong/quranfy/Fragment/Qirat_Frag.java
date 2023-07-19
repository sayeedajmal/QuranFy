package com.strong.quranfy.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.strong.quranfy.Adaptor.qurat_adaptor;
import com.strong.quranfy.Models.qirat_getter;
import com.strong.quranfy.databinding.FragmentQiratBinding;

import java.util.ArrayList;

public class Qirat_Frag extends Fragment {
    FragmentQiratBinding Bind;
    ArrayList<qirat_getter> qirat_getters = new ArrayList<>();

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
        Bind = FragmentQiratBinding.inflate(inflater, container, false);

        //qirat_getters.add(new qirat_getter("01", "Al-Fateha"));

        qurat_adaptor qurat_adaptor = new qurat_adaptor(qirat_getters, getContext());
    /*    Bind.RecyclerView.setAdapter(qurat_adaptor);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        Bind.RecyclerView.setLayoutManager(layoutManager);*/
        return Bind.getRoot();
    }
}