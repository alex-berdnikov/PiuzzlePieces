package com.example.alexberdnikov.puzzlepieces.view;

import java.util.LinkedList;
import java.util.List;
import timber.log.Timber;

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

      // Put the group to the beginning of the list so it'll be captured first, if it overlays any
      // other groups on the screen
      getPiecesGroups().remove(capturedGroup);
      getPiecesGroups().add(0, capturedGroup);

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
      Piece rightMostPiece = capturedGroup.getRightMostPiece();
      int rightmostPositionX = screenWidth - rightMostPiece.getPieceWidth();
//      Timber.d("--------- rightmostPositionX: %d, rightMostPiece: %s", rightmostPositionX, rightMostPiece);
      if (leftMostPiece.getX() < 0) {
        newX =
            capturedPiece.getPieceOffsetInPuzzle()[0] - leftMostPiece.getPieceOffsetInPuzzle()[0];
      } else if (rightmostPositionX < rightMostPiece.getX()) {
        newX = rightmostPositionX - (rightMostPiece.getPieceOffsetInPuzzle()[0]
            - capturedPiece.getPieceOffsetInPuzzle()[0]);
      }

      capturedPiece.setX(newX);

      int newY = capturedPiece.getY() + Math.round(y - prevY);
      if (newY < 0) {
        newY = 0;
      } else if ((screenHeight - capturedPiece.getPieceHeight()) < newY) {
        newY = screenHeight - capturedPiece.getPieceHeight();
      }

      Piece topMostPiece = capturedGroup.getTopMostPiece();
      Piece bottomMostPiece = capturedGroup.getBottomMostPiece();
      int bottommostPositionY = screenHeight - bottomMostPiece.getPieceHeight();

      if (topMostPiece.getY() < 0) {
        newY = capturedPiece.getPieceOffsetInPuzzle()[1] - topMostPiece.getPieceOffsetInPuzzle()[1];
      } else if (bottommostPositionY < bottomMostPiece.getY()) {
        newY = bottommostPositionY - (bottomMostPiece.getPieceOffsetInPuzzle()[1]
            - capturedPiece.getPieceOffsetInPuzzle()[1]);
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
