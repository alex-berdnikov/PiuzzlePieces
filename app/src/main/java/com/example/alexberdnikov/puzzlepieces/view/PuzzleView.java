package com.example.alexberdnikov.puzzlepieces.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Size;
import android.view.MotionEvent;
import android.view.View;
import com.example.alexberdnikov.puzzlepieces.R;
import timber.log.Timber;

public class PuzzleView extends View {

  private Puzzle puzzle;

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

  public PuzzleView(Context context,
      @Nullable AttributeSet attrs, int defStyleAttr,
      int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  public void setupPuzzle(Puzzle puzzle) {
    this.puzzle = puzzle;
    loadBitmap();
  }

  public Size getPuzzleAreaSize() {
    return puzzle.getPuzzleAreaSize();
  }

  private void loadBitmap() {
    new LoadBitmapTask(getResources(), this).execute(R.drawable.autumn_1);
  }

  protected void onImageLoaded(Bitmap bitmap) {
    puzzle.setImageBitmap(bitmap);
    puzzle.generate();
    invalidate();
  }

  @Override public boolean onTouchEvent(MotionEvent event) {
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        Timber.d("x: %f, y: %f", event.getX(), event.getY());
        puzzle.onTouchStart(event.getX(), event.getY());
        break;
      case MotionEvent.ACTION_MOVE:
        puzzle.onMove(event.getX(), event.getY());
        invalidate();
        break;
      case MotionEvent.ACTION_UP:
        puzzle.onTouchEnd(event.getX(), event.getY());
        return performClick();
    }
    return true;
  }

  @Override public boolean performClick() {
    return super.performClick();
  }

  @Override protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    for (Piece piece : puzzle.getPieces()) {
      canvas.drawBitmap(piece.pieceImage, piece.x, piece.y, null);
    }
  }
}
