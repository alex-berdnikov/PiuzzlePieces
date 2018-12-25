package com.example.alexberdnikov.puzzlepieces.view;

import java.util.Collections;
import java.util.List;
import timber.log.Timber;

class PiecesPicker {
  private final int LAYER_WITH_PX = 50;
  private int screenWidth;
  private int screenHeight;

  private List<Piece> pieces;
  private Piece capturedPiece;
  private float prevX;
  private float prevY;

  PiecesPicker(List<Piece> pieces, int screenWidth, int screenHeight) {
    this.pieces = pieces;
    this.screenWidth = screenWidth;
    this.screenHeight = screenHeight;
  }

  void onTouchStart(float x, float y) {
    capturedPiece = getCapturedPieceFromCoordinates(x, y);
    if (capturedPiece != null) {
      //Collections.swap(pieces, pieces.indexOf(capturedPiece), pieces.size() - 1);
      pieces.remove(capturedPiece);
      pieces.add(capturedPiece);
      prevX = x;
      prevY = y;
    }
  }

  void onMove(float x, float y) {
    if (capturedPiece != null) {
      Timber.d("Captured piece: %s", capturedPiece);
      capturedPiece.x += Math.round(x - prevX);
      capturedPiece.y += Math.round(y - prevY);
      prevX = x;
      prevY = y;
    }
  }

  void onTouchEnd(float x, float y) {
    capturedPiece = null;
    prevX = 0;
    prevY = 0;
  }

  private Piece getCapturedPieceFromCoordinates(float touchX, float touchY) {
    for (Piece piece : pieces) {
      if (isTouchInPieceBounds(piece, touchX, touchY)) {
        return piece;
      }
    }
    return null;
  }

  private boolean isTouchInPieceBounds(Piece piece, float touchX, float touchY) {
    return piece.x <= touchX && touchX <= piece.x + piece.pieceImage.getWidth()
        && piece.y <= touchY && touchY <= piece.y + piece.pieceImage.getHeight();
  }
}
