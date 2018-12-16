package com.example.alexberdnikov.puzzlepieces.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Size;
import com.example.alexberdnikov.puzzlepieces.utils.ScreenUtils;
import java.util.ArrayList;
import java.util.List;

public abstract class Puzzle {
  private Bitmap imageBitmap;
  private Size puzzleAreaSize;
  private List<Piece> pieces = new ArrayList<>();

  static class Piece {
    Bitmap pieceImage;
    int x;
    int y;

    Piece(Bitmap pieceImage, int x, int y) {
      this.pieceImage = pieceImage;
      this.x = x;
      this.y = y;
    }
  }

  public Puzzle(Context context) {
    calculateAndSetPuzzleArea(context);
  }

  void generate() {
    for (int i = 0; i < getPiecesCount(); i++) {

      // Just put the pieces consequently
      pieces.add(new Piece(createPieceFromNumber(i),
          (i % 16 ) * (96 + 18) + 40,
          (i / 16) * (96 + 18) + 40));
    }
  }

  private void calculateAndSetPuzzleArea(Context context) {
    Size screenSize = ScreenUtils.getScreenSize(context);
    puzzleAreaSize = new Size(Math.round(screenSize.getWidth() * 0.8f),
        Math.round(screenSize.getHeight() * 0.8f));
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

  List<Piece> getPieces() {
    return pieces;
  }

  abstract public int getPiecesCount();
  abstract public Bitmap createPieceFromNumber(int number);
}
