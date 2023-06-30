package com.strong.quranfy.Activity;

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
import com.strong.quranfy.databinding.ActivitySearchBinding;

import java.util.Objects;

public class SearchActivity extends AppCompatActivity {
    ActivitySearchBinding BindSearch;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BindSearch = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(BindSearch.getRoot());
        reference = FirebaseDatabase.getInstance().getReference().child("Surah").child("SurahIndex");
        BindSearch.SearchSurah.requestFocus();

        BindSearch.SearchSurah.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                SearchSurah(editable);
            }
        });
    }

    private void SearchSurah(Editable editable) {
        String TextSearch = editable.toString();

        if (!TextSearch.isEmpty()) {
            Query query = reference;
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (getSurahNo(TextSearch)) {
                            int SurahNo = Integer.parseInt(TextSearch);
                            if (SurahNo == Integer.parseInt(Objects.requireNonNull(dataSnapshot.getKey()))) {
                                System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<< " + dataSnapshot.getKey());
                            }
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private boolean getSurahNo(String Text) {
        try {
            Integer.parseInt(Text);
            return true;
        } catch (NumberFormatException e) {
            System.out.println("<<<<<<<<<<<<<<<<<<<, " + e.getLocalizedMessage());
        }
        return false;
    }
}