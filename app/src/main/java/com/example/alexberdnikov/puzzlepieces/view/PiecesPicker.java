package com.example.alexberdnikov.puzzlepieces.view;

import java.util.List;

/**
 * Class for managing pieces moves and detecting and handling neighboring pieces connections.
 * Has to be implemented for particular type of puzzle (like jigsaw).
 */
public abstract class PiecesPicker {
  private List<Piece> pieces;

  private Piece capturedPiece;
  private float prevX;
  private float prevY;

  private int screenWidth;
  private int screenHeight;

  public PiecesPicker(List<Piece> pieces, int screenWidth, int screenHeight) {
    this.pieces = pieces;
    this.screenWidth = screenWidth;
    this.screenHeight = screenHeight;
  }

  void onTouchStart(float x, float y) {
    capturedPiece = getCapturedPieceFromCoordinates(x, y);
    if (capturedPiece != null) {
      // Put the piece to the end of the list so it'll be drawn on top of others
      pieces.remove(capturedPiece);
      pieces.add(capturedPiece);

      prevX = x;
      prevY = y;
    }
  }

  void onMove(float x, float y) {
    if (capturedPiece != null) {

      int newX = capturedPiece.getX() + Math.round(x - prevX);
      if (newX < 0) {
        newX = 0;
      } else if ((screenWidth - capturedPiece.getPieceWidth()) < newX) {
        newX = screenWidth - capturedPiece.getPieceWidth();
      }
      capturedPiece.setX(newX);

      int newY = capturedPiece.getY() + Math.round(y - prevY);
      if (newY < 0) {
        newY = 0;
      } else if ((screenHeight - capturedPiece.getPieceHeight()) < newY) {
        newY = screenHeight - capturedPiece.getPieceHeight();
      }
      capturedPiece.setY(newY);

      prevX = x;
      prevY = y;
    }
  }

  void onTouchEnd(float x, float y) {
    if (capturedPiece != null) {
      handlePiecesConnections(capturedPiece);
    }

    capturedPiece = null;
    prevX = 0;
    prevY = 0;
  }

  protected List<Piece> getPieces() {
    return pieces;
  }

  abstract protected Piece getCapturedPieceFromCoordinates(float touchX, float touchY);
  abstract protected void handlePiecesConnections(Piece draggedPiece);
}
