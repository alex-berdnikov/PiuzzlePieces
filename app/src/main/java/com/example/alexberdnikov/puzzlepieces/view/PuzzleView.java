package com.example.alexberdnikov.puzzlepieces.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Size;
import android.view.MotionEvent;
import android.view.View;
import com.example.alexberdnikov.puzzlepieces.R;
import timber.log.Timber;

public class PuzzleView extends View {

  private Puzzle puzzle;
  private long lastUpdateTimestamp;

  public PuzzleView(Context context) {
    super(context);
  }

  public PuzzleView(Context context,
      @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public PuzzleView(Context context,
      @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public void setupPuzzle(Puzzle puzzle) {
    this.puzzle = puzzle;
    loadBitmap();
  }

  public Size getPuzzleAreaSize() {
    return puzzle.getPuzzleAreaSize();
  }

  private void loadBitmap() {
    //new LoadBitmapTask(getResources(), this).execute(R.drawable.autumn_1);
    new LoadBitmapTask(getResources(), this).execute(R.drawable.emma_stone);
  }

  protected void onImageLoaded(Bitmap bitmap) {
    Timber.d("------ !! --- !! ---- LOADED BITMAP SIZE: %dx%d", bitmap.getWidth(), bitmap.getHeight());
    puzzle.setImageBitmap(bitmap);
    puzzle.generate();
    updateView(false);
  }

  @Override public boolean onTouchEvent(MotionEvent event) {
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        puzzle.onTouchStart(event.getX(), event.getY());
        break;
      case MotionEvent.ACTION_MOVE:
        puzzle.onMove(event.getX(), event.getY());
        updateView(false);
        break;
      case MotionEvent.ACTION_UP:
        puzzle.onTouchEnd(event.getX(), event.getY());
        updateView(true);
        return performClick();
    }
    return true;
  }

  private void updateView(boolean forced) {
    final long FRAME_RATE_MS = 30;
    long currentMillis = System.currentTimeMillis();
    if (FRAME_RATE_MS < currentMillis - lastUpdateTimestamp || forced) {
      invalidate();
      lastUpdateTimestamp = currentMillis;
    }
  }

  @Override public boolean performClick() {
    return super.performClick();
  }

  @Override protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    for (Piece piece : puzzle.getPieces()) {
      canvas.drawBitmap(piece.getPieceImage(), piece.getX(), piece.getY(), null);
    }
  }
}
