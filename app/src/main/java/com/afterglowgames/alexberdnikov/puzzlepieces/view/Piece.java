package com.afterglowgames.alexberdnikov.puzzlepieces.view;

import android.graphics.Bitmap;
import java.util.Locale;

abstract public class Piece {
  private final Bitmap pieceImage;
  private final int number;
  private int x;
  private int y;

  public Piece(Bitmap pieceImage, int number, int x, int y) {
    this.pieceImage = pieceImage;
    this.number = number;
    this.x = x;
    this.y = y;
  }

  public int getPieceWidth() {
    return pieceImage.getWidth();
  }

  public int getPieceHeight() {
    return pieceImage.getHeight();
  }

  public int getX() {
    return x;
  }

  public void setX(int x) {
    this.x = x;
  }

  public int getY() {
    return y;
  }

  public void setY(int y) {
    this.y = y;
  }

  Bitmap getPieceImage() {
    return pieceImage;
  }

  @Override public String toString() {
    return String.format(Locale.getDefault(), "[#%d, x: %d, y: %d]", getNumber(), x, y);
  }

  public int getNumber() {
    return number;
  }

  public abstract int[] getPieceOffsetInPuzzle();
}
