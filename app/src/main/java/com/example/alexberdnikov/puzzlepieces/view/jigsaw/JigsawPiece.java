package com.example.alexberdnikov.puzzlepieces.view.jigsaw;

import android.graphics.Bitmap;
import com.example.alexberdnikov.puzzlepieces.view.Piece;

public class JigsawPiece extends Piece {
  final static int PIECE_CONVEX_CONCAVE_CUBIC_WIDTH = 16;
  final static int PIECE_CONVEX_CUBIC_HEIGHT = 24;

  private enum SideConnection {NOT_AVAILABLE, FREE, CONNECTED}

  private SideConnection[] sidesStatuses = new SideConnection[4];
  private SidesDescription sidesDescription;

  public final static int NEIGHBOR_NOT_AVAILABLE = -1;
  private int[] sideNeighbors = new int[] {
      NEIGHBOR_NOT_AVAILABLE,
      NEIGHBOR_NOT_AVAILABLE,
      NEIGHBOR_NOT_AVAILABLE,
      NEIGHBOR_NOT_AVAILABLE
  };

  final int puzzleColumnsCount;
  final int puzzleRowsCount;

  public JigsawPiece(
      Bitmap pieceImage,
      SidesDescription sidesDescription,
      int puzzleColumnsCount,
      int puzzleRowsCount,
      int number,
      int x,
      int y) {

    super(pieceImage, number, x, y);
    this.sidesDescription = sidesDescription;
    this.puzzleColumnsCount = puzzleColumnsCount;
    this.puzzleRowsCount = puzzleRowsCount;

    initSidesStatuses();
    detectNeighbors(puzzleColumnsCount, puzzleRowsCount);
  }

  private void initSidesStatuses() {
    sidesStatuses[SidesDescription.SIDE_TOP] =
        sidesDescription.getSideForm(SidesDescription.SIDE_TOP)
            == SidesDescription.SIDE_FORM_FLAT ? SideConnection.NOT_AVAILABLE : SideConnection.FREE;

    sidesStatuses[SidesDescription.SIDE_RIGHT] =
        sidesDescription.getSideForm(SidesDescription.SIDE_RIGHT)
            == SidesDescription.SIDE_FORM_FLAT ? SideConnection.NOT_AVAILABLE : SideConnection.FREE;

    sidesStatuses[SidesDescription.SIDE_BOTTOM] =
        sidesDescription.getSideForm(SidesDescription.SIDE_BOTTOM)
            == SidesDescription.SIDE_FORM_FLAT ? SideConnection.NOT_AVAILABLE : SideConnection.FREE;

    sidesStatuses[SidesDescription.SIDE_LEFT] =
        sidesDescription.getSideForm(SidesDescription.SIDE_LEFT)
            == SidesDescription.SIDE_FORM_FLAT ? SideConnection.NOT_AVAILABLE : SideConnection.FREE;
  }

  private void detectNeighbors(int puzzleColumnsCount, int puzzleRowsCount) {
    if (sidesStatuses[SidesDescription.SIDE_TOP] == SideConnection.FREE) {
      boolean isInFirstRow = getNumber() < puzzleColumnsCount;
      sideNeighbors[SidesDescription.SIDE_TOP] = isInFirstRow
          ? NEIGHBOR_NOT_AVAILABLE
          : getNumber() - puzzleColumnsCount;
    }

    if (sidesStatuses[SidesDescription.SIDE_BOTTOM] == SideConnection.FREE) {
      boolean isInLastRow =
          (((puzzleColumnsCount * puzzleRowsCount) - 1) - puzzleColumnsCount) < getNumber();
      sideNeighbors[SidesDescription.SIDE_BOTTOM] = isInLastRow
          ? NEIGHBOR_NOT_AVAILABLE
          : getNumber() + puzzleColumnsCount;
    }

    if (sidesStatuses[SidesDescription.SIDE_RIGHT] == SideConnection.FREE) {
      boolean isInLastColumn = getNumber() % puzzleColumnsCount == puzzleColumnsCount - 1;
      sideNeighbors[SidesDescription.SIDE_RIGHT] = isInLastColumn
          ? NEIGHBOR_NOT_AVAILABLE : getNumber() + 1;
    }

    if (sidesStatuses[SidesDescription.SIDE_LEFT] == SideConnection.FREE) {
      boolean isInFirstColumn = getNumber() % puzzleColumnsCount == 0;
      sideNeighbors[SidesDescription.SIDE_LEFT] = isInFirstColumn
          ? NEIGHBOR_NOT_AVAILABLE : getNumber() - 1;
    }
  }

  public int getTopNeighborNumber() {
    return sideNeighbors[SidesDescription.SIDE_TOP];
  }

  public int getRightNeighborNumber() {
    return sideNeighbors[SidesDescription.SIDE_RIGHT];
  }

  public int getBottomNeighborNumber() {
    return sideNeighbors[SidesDescription.SIDE_BOTTOM];
  }

  public int getLeftNeighborNumber() {
    return sideNeighbors[SidesDescription.SIDE_LEFT];
  }

  boolean isTopSideFree() {
    return sidesStatuses[SidesDescription.SIDE_TOP] == SideConnection.FREE;
  }

  void connectTopSide() {
    if (sidesStatuses[SidesDescription.SIDE_TOP] == SideConnection.FREE) {
      sidesStatuses[SidesDescription.SIDE_TOP] = SideConnection.CONNECTED;
    }
  }

  boolean isRightSideFree() {
    return sidesStatuses[SidesDescription.SIDE_RIGHT] == SideConnection.FREE;
  }

  void connectRightSide() {
    if (sidesStatuses[SidesDescription.SIDE_RIGHT] == SideConnection.FREE) {
      sidesStatuses[SidesDescription.SIDE_RIGHT] = SideConnection.CONNECTED;
    }
  }

  boolean isBottomSideFree() {
    return sidesStatuses[SidesDescription.SIDE_BOTTOM] == SideConnection.FREE;
  }

  void connectBottomSide() {
    if (sidesStatuses[SidesDescription.SIDE_BOTTOM] == SideConnection.FREE) {
      sidesStatuses[SidesDescription.SIDE_BOTTOM] = SideConnection.CONNECTED;
    }
  }

  boolean isLeftSideFree() {
    return sidesStatuses[SidesDescription.SIDE_LEFT] == SideConnection.FREE;
  }

  void connectLeftSide() {
    if (sidesStatuses[SidesDescription.SIDE_LEFT] == SideConnection.FREE) {
      sidesStatuses[SidesDescription.SIDE_LEFT] = SideConnection.CONNECTED;
    }
  }

  int getLeftTopCornerX() {
    return (sidesDescription.getSideForm(SidesDescription.SIDE_LEFT)
        == SidesDescription.SIDE_FORM_CONVEX)
        ? getX() + PIECE_CONVEX_CUBIC_HEIGHT : getX();
  }

  int getLeftTopCornerY() {
    return (sidesDescription.getSideForm(SidesDescription.SIDE_TOP)
        == SidesDescription.SIDE_FORM_CONVEX)
        ? getY() + PIECE_CONVEX_CUBIC_HEIGHT : getY();
  }

  int getLeftBottomCornerX() {
    return getLeftTopCornerX();
  }

  int getLeftBottomCornerY() {
    int leftBottomCornerY = getY() + getPieceHeight();
    return (sidesDescription.getSideForm(SidesDescription.SIDE_BOTTOM)
        == SidesDescription.SIDE_FORM_CONVEX)
        ? leftBottomCornerY - PIECE_CONVEX_CUBIC_HEIGHT : leftBottomCornerY;
  }

  int getRightTopCornerX() {
    int rightTopCornerX = getX() + getPieceWidth();
    return (sidesDescription.getSideForm(SidesDescription.SIDE_RIGHT)
        == SidesDescription.SIDE_FORM_CONVEX)
        ? rightTopCornerX - PIECE_CONVEX_CUBIC_HEIGHT : rightTopCornerX;
  }

  private int getSquareWidth() {
    int width = getPieceWidth();
    if (sidesDescription.getSideForm(SidesDescription.SIDE_LEFT)
        == SidesDescription.SIDE_FORM_CONVEX) {
      width -= PIECE_CONVEX_CUBIC_HEIGHT;
    }

    if (sidesDescription.getSideForm(SidesDescription.SIDE_RIGHT)
        == SidesDescription.SIDE_FORM_CONVEX) {
      width -= PIECE_CONVEX_CUBIC_HEIGHT;
    }

    return width;
  }

  private int getSquareHeight() {
    int height = getPieceHeight();
    if (sidesDescription.getSideForm(SidesDescription.SIDE_TOP)
        == SidesDescription.SIDE_FORM_CONVEX) {
      height -= PIECE_CONVEX_CUBIC_HEIGHT;
    }

    if (sidesDescription.getSideForm(SidesDescription.SIDE_BOTTOM)
        == SidesDescription.SIDE_FORM_CONVEX) {
      height -= PIECE_CONVEX_CUBIC_HEIGHT;
    }

    return height;
  }

  int getTopSideDescription() {
    return sidesDescription.getSideForm(SidesDescription.SIDE_TOP);
  }

  int getLeftSideDescription() {
    return sidesDescription.getSideForm(SidesDescription.SIDE_LEFT);
  }

  @Override public int[] getPieceOffsetInPuzzle() {
    int pieceNumberInRow = getNumber() % puzzleColumnsCount;
    int pieceInPuzzleOffsetX = getSquareWidth() * pieceNumberInRow;
    if (getLeftSideDescription() == JigsawPiece.SidesDescription.SIDE_FORM_CONVEX) {
      pieceInPuzzleOffsetX -= JigsawPiece.PIECE_CONVEX_CUBIC_HEIGHT;
    }

    int pieceNumberInColumn = getNumber() / puzzleColumnsCount;
    int pieceInPuzzleOffsetY = getSquareHeight() * pieceNumberInColumn;

    if (getTopSideDescription() == JigsawPiece.SidesDescription.SIDE_FORM_CONVEX) {
      pieceInPuzzleOffsetY -= JigsawPiece.PIECE_CONVEX_CUBIC_HEIGHT;
    }

    return new int[] { pieceInPuzzleOffsetX, pieceInPuzzleOffsetY };
  }

  /**
   * Holds information about sides of a puzzle piece.
   */
  public static class SidesDescription {
    public static final int SIDE_FORM_FLAT = 0;
    public static final int SIDE_FORM_CONCAVE = 1;
    public static final int SIDE_FORM_CONVEX = 2;

    public static final int SIDE_TOP = 0;
    public static final int SIDE_RIGHT = 1;
    public static final int SIDE_BOTTOM = 2;
    public static final int SIDE_LEFT = 3;

    public Integer[] pieceSidesForms = new Integer[4];

    public int getSideForm(int side) {
      return pieceSidesForms[side];
    }
  }
}
