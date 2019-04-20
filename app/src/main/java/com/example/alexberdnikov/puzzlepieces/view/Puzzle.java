package com.example.alexberdnikov.puzzlepieces.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Size;
import com.example.alexberdnikov.puzzlepieces.utils.ScreenUtils;
import java.util.LinkedList;
import java.util.List;
import timber.log.Timber;

public abstract class Puzzle {
  private Bitmap imageBitmap;
  private Size puzzleAreaSize;
  private Size screenSize;

  private List<Piece> pieces = new LinkedList<>();
  private PiecesPicker piecesPicker;

  public Puzzle(Context context) {
    calculateAndSetupPuzzleArea(context);
  }

  private void calculateAndSetupPuzzleArea(Context context) {
    this.screenSize = ScreenUtils.getScreenSize(context);
    puzzleAreaSize = new Size(Math.round(screenSize.getWidth() * 0.8f),
        Math.round(screenSize.getHeight() * 0.8f));
    Timber.d("------- puzzle area size: %dx%d", puzzleAreaSize.getWidth(), puzzleAreaSize.getHeight());
  }

  protected Bitmap getImageBitmap() {
    return imageBitmap;
  }

  protected Size getPuzzleAreaSize() {
    return puzzleAreaSize;
  }

  void setImageBitmap(Bitmap imageBitmap) {
    this.imageBitmap = imageBitmap;
  }

  protected List<Piece> getPieces() {
    return pieces;
  }

  void onTouchStart(float x, float y) {
    piecesPicker.onTouchStart(x, y);
  }

  void onMove(float x, float y) {
    piecesPicker.onMove(x, y);
  }

  void onTouchEnd(float x, float y) {
    piecesPicker.onTouchEnd(x, y);
  }

  protected void onGenerated() {
    piecesPicker = createPiecesPicker(screenSize.getWidth(), screenSize.getHeight());
  }

  abstract public void generate();
  abstract protected Piece createPiece(int number, int x, int y);
  abstract protected PiecesPicker createPiecesPicker(int screenWidth, int screenHeight);
}
