package com.example.alexberdnikov.puzzlepieces.view;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Size;
import com.example.alexberdnikov.puzzlepieces.utils.BitmapUtils;
import com.example.alexberdnikov.puzzlepieces.utils.ScreenUtils;
import java.lang.ref.WeakReference;
import timber.log.Timber;

class LoadBitmapTask extends AsyncTask<Integer, Void, Bitmap> {
  private Resources resources;
  private WeakReference<PuzzleView> puzzleViewWeakReference;
  private Size puzzleAreaSize;

  LoadBitmapTask(Resources resources, PuzzleView puzzleView) {
    this.resources = resources;
    this.puzzleViewWeakReference = new WeakReference<>(puzzleView);
  }

  @Override protected void onPreExecute() {
    if (puzzleViewWeakReference.get() == null) {
      cancel(true);
      return;
    }

    puzzleAreaSize = puzzleViewWeakReference.get().getPuzzleAreaSize();
  }

  @Override protected Bitmap doInBackground(Integer... bitmapResource) {
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeResource(resources, bitmapResource[0], options);

    if (puzzleViewWeakReference.get() == null) {
      return null;
    }

    Size screeSize = ScreenUtils.getScreenSize(puzzleViewWeakReference.get().getContext());
    options.inSampleSize = ScreenUtils.calculateInSampleSize(
        options, screeSize.getWidth(), screeSize.getHeight());

    options.inJustDecodeBounds = false;
    Bitmap scaledBitmap = BitmapFactory.decodeResource(resources, bitmapResource[0], options);


    Timber.d("------ !!!! --- !!!! --- options: outSize: %dx%d", options.outWidth, options.outHeight);

    Timber.d("------ !!!! --- bitmap size: %dx%d, play area size: %dx%d", scaledBitmap.getWidth(),
        scaledBitmap.getHeight(), puzzleAreaSize.getWidth(), puzzleAreaSize.getHeight());

    Size puzzleAreaSize = puzzleViewWeakReference.get().getPuzzleAreaSize();
    boolean isBitmapLessThanPlayArea = scaledBitmap.getWidth() < puzzleAreaSize.getWidth()
        || scaledBitmap.getHeight() < puzzleAreaSize.getHeight();

    if (isBitmapLessThanPlayArea) {
      // Stretch bitmap to the size of play area and crop

      return BitmapUtils.scaleAndCropBitmap(scaledBitmap, puzzleAreaSize.getWidth(),
          puzzleAreaSize.getHeight());
    }

    boolean isBitmapBiggerThanPlayArea = puzzleAreaSize.getWidth() < options.outWidth
        || puzzleAreaSize.getHeight() < options.outHeight;
    if (isBitmapBiggerThanPlayArea) {
      // Resize image to play are size and crop what's left
      // return result;
    }

    return scaledBitmap;
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
