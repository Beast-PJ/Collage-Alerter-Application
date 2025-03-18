package com.example.collage_alerter;

import android.content.Context;
import android.widget.Toast;

public class Utility {
    public static void showToast(Context context, String s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }
}
