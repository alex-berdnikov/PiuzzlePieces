package com.example.alexberdnikov.puzzlepieces.view.jigsaw;

import com.example.alexberdnikov.puzzlepieces.view.Piece;

public class JigsawPiece {
  private boolean isTopSideConnected;
  private boolean isRightSideConnected;
  private boolean isBottomSideConnected;
  private boolean isLeftSideConnected;
  protected Piece piece;

  JigsawPiece(Piece piece) {
    this.piece = piece;
  }

  public boolean isTopSideConnected() {
    return isTopSideConnected;
  }

  public void connectTopSide() {
    isTopSideConnected = true;
  }

  public boolean isRightSideConnected() {
    return isRightSideConnected;
  }

  public void connectRightSide() {
    isRightSideConnected = true;
  }

  public boolean isBottomSideConnected() {
    return isBottomSideConnected;
  }

  public void connectBottomSide() {
    isBottomSideConnected = true;
  }

  public boolean isLeftSideConnected() {
    return isLeftSideConnected;
  }

  public void connectLeftSide() {
    isLeftSideConnected = true;
  }
}
