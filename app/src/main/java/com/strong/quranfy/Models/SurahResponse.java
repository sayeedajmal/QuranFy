package com.strong.quranfy.Models;

import java.util.List;

public class SurahResponse {
    private int code;
    private String status;
    private List<Surah> data;

    public int getCode() {
        return code;
    }

    public String getStatus() {
        return status;
    }

    public List<Surah> getData() {
        return data;
    }
}
