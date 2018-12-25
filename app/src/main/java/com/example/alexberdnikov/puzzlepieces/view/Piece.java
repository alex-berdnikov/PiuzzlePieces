package com.example.alexberdnikov.puzzlepieces.view;

import android.graphics.Bitmap;
import java.util.Locale;

class Piece {
  final Bitmap pieceImage;
  final int number;
  int x;
  int y;

  Piece(Bitmap pieceImage, int number, int x, int y) {
    this.pieceImage = pieceImage;
    this.number = number;
    this.x = x;
    this.y = y;
  }

  @Override public String toString() {
    return String.format(Locale.getDefault(), "[#%d, x: %d, y: %d]", number, x, y);
  }
}
