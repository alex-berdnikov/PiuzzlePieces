package com.example.alexberdnikov.puzzlepieces.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Size;
import android.view.View;
import com.example.alexberdnikov.puzzlepieces.R;

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

  @Override protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    for (Puzzle.Piece piece : puzzle.getPieces()) {
      canvas.drawBitmap(piece.pieceImage, piece.x, piece.y, null);
    }
  }
}
