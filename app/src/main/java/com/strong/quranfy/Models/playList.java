package com.strong.quranfy.Models;

import static com.strong.quranfy.Adaptor.surah_adaptor.POSITION;
import static com.strong.quranfy.Adaptor.surah_adaptor.UpdateData;
import static com.strong.quranfy.Adaptor.surah_adaptor.setPOSITION;

public class playList {

    public static void ACTION(String button) {
        if (button.equals("NEXT")) {
            setPOSITION(POSITION + 1);
            UpdateData();

        } else if (button.equals("PREVIOUS")) {
            setPOSITION(POSITION - 1);
            UpdateData();
        }
    }
}
