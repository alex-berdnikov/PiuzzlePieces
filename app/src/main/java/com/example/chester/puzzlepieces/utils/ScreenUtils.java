package com.example.chester.puzzlepieces.utils;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.Size;

public class ScreenUtils {
  public static Size getScreenSize(Context context) {
    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
    return new Size(displayMetrics.widthPixels, displayMetrics.heightPixels);
  }

  public static int calculateInSampleSize(
      BitmapFactory.Options options, int reqWidth, int reqHeight) {

    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {

      final int halfHeight = height / 2;
      final int halfWidth = width / 2;

      while ((halfHeight / inSampleSize) >= reqHeight
          && (halfWidth / inSampleSize) >= reqWidth) {
        inSampleSize *= 2;
      }
    }

    return inSampleSize;
  }
}
