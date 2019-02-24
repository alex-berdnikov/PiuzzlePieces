package com.example.alexberdnikov.puzzlepieces.view;

import java.util.LinkedList;
import java.util.List;

/**
 * Class for managing pieces moves and detecting and handling neighboring pieces connections.
 * Has to be implemented for every particular type of puzzle (like jigsaw).
 */
public abstract class PiecesPicker {
  private List<Piece> pieces;
  private List<PiecesGroup> piecesGroups = new LinkedList<>();

  private Piece capturedPiece;
  private PiecesGroup capturedGroup;
  private float prevX;
  private float prevY;

  private int screenWidth;
  private int screenHeight;

  public PiecesPicker(List<Piece> pieces, int screenWidth, int screenHeight) {
    this.pieces = pieces;
    for (Piece piece : pieces) {
      piecesGroups.add(createPieceGroup(piece));
    }

    this.screenWidth = screenWidth;
    this.screenHeight = screenHeight;
  }

  void onTouchStart(float x, float y) {
    capturedPiece = getCapturedPieceFromCoordinates(x, y);
    if (capturedPiece != null) {
      capturedGroup = getPieceGroup(capturedPiece);

      // Put the piece to the end of the list so it'll be drawn on top of others
      for (Piece piece : capturedGroup.getPieces()) {
        pieces.remove(piece);
        pieces.add(piece);
      }

      prevX = x;
      prevY = y;
    }
  }

  public void onMove(float x, float y) {
    if (capturedPiece != null) {
      int newX = capturedPiece.getX() + Math.round(x - prevX);
      if (newX < 0) {
        newX = 0;
      } else if ((screenWidth - capturedPiece.getPieceWidth()) < newX) {
        newX = screenWidth - capturedPiece.getPieceWidth();
      }

      Piece leftMostPiece = capturedGroup.getLeftMostPiece();
     /* if (leftMostPiece.getX() < 0) {
        newX += Math.abs(leftMostPiece.getX());
      }

      Piece rightMostPiece = capturedGroup.getRightMostPiece();
      int rightmostPositionX = screenWidth - capturedPiece.getPieceWidth();
      if ((rightmostPositionX) < rightMostPiece.getX()) {
        newX -= rightmostPositionX;
      }
*/
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

      capturedGroup.alignGroupByPiece(capturedPiece);
    }
  }

  void onTouchEnd(float x, float y) {
    if (capturedPiece != null) {
      handlePiecesConnections(capturedPiece);
    }

    capturedPiece = null;
    capturedGroup = null;
    prevX = 0;
    prevY = 0;
  }

  protected List<PiecesGroup> getPiecesGroups() {
    return piecesGroups;
  }

  protected PiecesGroup getGroupOfPiece(Piece piece) {
    for (PiecesGroup group : getPiecesGroups()) {
      if (group.contains(piece)) {
        return group;
      }
    }
    throw new IllegalStateException("Piece must belong to some group.");
  }

  private PiecesGroup getPieceGroup(Piece piece) {
    for (PiecesGroup group : piecesGroups) {
      if (group.contains(piece)) {
        return group;
      }
    }
    throw new IllegalStateException("Given piece wasn't found in any of the groups.");
  }

  protected Piece getPieceByNumber(int number) {
    for (PiecesGroup group : getPiecesGroups()) {
      for (Piece piece : group.getPieces()) {
        if (piece.getNumber() == number) {
          return piece;
        }
      }
    }
    throw new IllegalArgumentException(String.format("There's no piece with number %d", number));
  }

  abstract protected Piece getCapturedPieceFromCoordinates(float touchX, float touchY);
  abstract protected void handlePiecesConnections(Piece draggedPiece);
  abstract protected PiecesGroup createPieceGroup(Piece piece);
}
