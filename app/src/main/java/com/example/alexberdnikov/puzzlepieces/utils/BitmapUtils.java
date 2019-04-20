package com.example.alexberdnikov.puzzlepieces.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapUtils {
  public static Bitmap scaleBitmap(Bitmap srcBitmap, int outWidth, int outHeight) {
    return Bitmap.createScaledBitmap(srcBitmap, outWidth, outHeight, true);
  }

  public static Bitmap cropCenterBitmap(Bitmap srcBitmap, int outWidth, int outHeight) {
    if (srcBitmap.getWidth() <= outWidth && srcBitmap.getHeight() <= outHeight) {
      return srcBitmap;
    }

    int cropX = (outWidth < srcBitmap.getWidth())
        ? (srcBitmap.getWidth() - outWidth) / 2
        : srcBitmap.getWidth();

    int cropY = (outHeight < srcBitmap.getWidth())
        ? (srcBitmap.getHeight() - outHeight) / 2
        : srcBitmap.getHeight();

    return Bitmap.createBitmap(srcBitmap, cropX, cropY, outWidth, outHeight);
  }
}
