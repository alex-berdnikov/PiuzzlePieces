package com.example.alexberdnikov.puzzlepieces.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Size;
import com.example.alexberdnikov.puzzlepieces.utils.ScreenUtils;
import java.util.LinkedList;
import java.util.List;

public abstract class Puzzle {
  private Bitmap imageBitmap;
  private Size puzzleAreaSize;
  private List<Piece> pieces = new LinkedList<>();
  private PiecesPicker piecesPicker;


  public Puzzle(Context context) {
    calculateAndSetPuzzleArea(context);
  }

  void generate() {
    for (int i = 0; i < getPiecesCount(); i++) {
      // Just put the pieces consequently
      pieces.add(createPiece(i, (i % 16 ) * (96 + 18) + 40, (i / 16) * (96 + 18) + 40));
    }
  }

  private void calculateAndSetPuzzleArea(Context context) {
    Size screenSize = ScreenUtils.getScreenSize(context);
    puzzleAreaSize = new Size(Math.round(screenSize.getWidth() * 0.8f),
        Math.round(screenSize.getHeight() * 0.8f));
    piecesPicker = new PiecesPicker(pieces, puzzleAreaSize.getWidth(), puzzleAreaSize.getHeight());
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

  private Piece createPiece(int number, int x, int y) {
    return new Piece(createPieceImage(number), number, x, y);
  }

  List<Piece> getPieces() {
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

  abstract public int getPiecesCount();
  abstract public Bitmap createPieceImage(int pieceNumber);
}
