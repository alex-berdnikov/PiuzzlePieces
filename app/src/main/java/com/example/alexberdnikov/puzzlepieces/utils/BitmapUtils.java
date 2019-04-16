package com.example.alexberdnikov.puzzlepieces.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapUtils {
  public static Bitmap scaleAndCropBitmap(Bitmap srcBitmap, int resultWidth, int resultHeight) {
    return Bitmap.createScaledBitmap(srcBitmap, resultWidth, resultHeight, true);
  }
}
