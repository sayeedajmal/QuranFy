package com.strong.quranfy.Activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.strong.quranfy.Adaptor.Search_Adaptor;
import com.strong.quranfy.Models.SearchGet;
import com.strong.quranfy.databinding.ActivitySearchBinding;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    ActivitySearchBinding BindSearch;
    DatabaseReference reference;

    ArrayList<SearchGet> list = new ArrayList<>();
    Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BindSearch = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(BindSearch.getRoot());
        reference = FirebaseDatabase.getInstance().getReference().child("Surah").child("SurahIndex");
        BindSearch.SearchSurah.requestFocus();

        BindSearch.backButton.setOnClickListener(v -> onBackPressed());

        Search_Adaptor adaptor = new Search_Adaptor(list, this);
        BindSearch.SearchSurah.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() != 0) SearchSurah(editable, adaptor);
            }
        });

        BindSearch.RecyclerView.setAdapter(adaptor);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void SearchSurah(Editable editable, Search_Adaptor adaptor) {
        if (isSurahNo(editable.toString())) {
            query = reference.orderByKey().startAt(editable.toString()).endAt(editable + "\uf8ff");
        } else
            query = reference.orderByValue().startAt(editable.toString()).endAt(editable + "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    SearchGet get = new SearchGet(dataSnapshot.getKey(), dataSnapshot.getValue(String.class));
                    list.add(get);
                }
                adaptor.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        adaptor.notifyDataSetChanged();
    }

    private boolean isSurahNo(String Text) {
        try {
            Integer.parseInt(Text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}