package com.meetup.hacktory.ghostdetector;

import android.widget.TextView;

public class StickerMetaData {
    public final String color;
    public final TextView textView;
    public final String message;

    public StickerMetaData(String color, TextView textView, String message) {
        this.color = color;
        this.textView = textView;
        this.message = message;
    }
}
