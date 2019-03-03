package com.example.alexberdnikov.puzzlepieces.view;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Size;
import com.example.alexberdnikov.puzzlepieces.utils.ScreenUtils;
import java.lang.ref.WeakReference;
import timber.log.Timber;

class LoadBitmapTask extends AsyncTask<Integer, Void, Bitmap> {
  private Resources resources;
  private WeakReference<PuzzleView> puzzleViewWeakReference;

  LoadBitmapTask(Resources resources, PuzzleView puzzleView) {
    this.resources = resources;
    this.puzzleViewWeakReference = new WeakReference<>(puzzleView);
  }

  @Override protected Bitmap doInBackground(Integer... bitmapResource) {
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeResource(resources, bitmapResource[0], options);

    if (puzzleViewWeakReference.get() == null) {
      return null;
    }

    Size puzzleAreaSize = puzzleViewWeakReference.get().getPuzzleAreaSize();
    options.inSampleSize = ScreenUtils.calculateInSampleSize(
        options, puzzleAreaSize.getWidth(), puzzleAreaSize.getHeight());
    options.inJustDecodeBounds = false;

    return BitmapFactory.decodeResource(resources, bitmapResource[0], options);
  }

  @Override protected void onPostExecute(Bitmap bitmap) {
    if (bitmap == null) {
      Timber.e("Bitmap is null.");
      return;
    }

    if (puzzleViewWeakReference.get() == null) {
      Timber.e("PuzzleView instance is null.");
      return;
    }

    PuzzleView puzzleView = puzzleViewWeakReference.get();
    puzzleView.onImageLoaded(bitmap);
  }
}
