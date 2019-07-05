package com.afterglowgames.alexberdnikov.puzzlepieces.utils;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.Size;
import android.view.WindowManager;

public class ScreenUtils {
  public static Size getScreenSize(Context context) {
    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
    int screenWidth = displayMetrics.widthPixels;
    int screenHeight = displayMetrics.heightPixels;

    DisplayMetrics realMetrics = getRealMetrics(context);
    if (screenHeight < screenWidth) {
      screenWidth = realMetrics.widthPixels;
    } else {
      screenHeight = realMetrics.heightPixels;
    }

    return new Size(screenWidth, screenHeight);
  }

  private static DisplayMetrics getRealMetrics(Context context) {
    WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    DisplayMetrics metrics = new DisplayMetrics();
    windowManager.getDefaultDisplay().getRealMetrics(metrics);
    return metrics;
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
