package com.strong.quranfy.Models;

import java.util.List;

public class EditionResponse {
    private int code;
    private String status;
    private List<Edition> data;

    public int getCode() {
        return code;
    }

    public String getStatus() {
        return status;
    }

    public List<Edition> getData() {
        return data;
    }
}
