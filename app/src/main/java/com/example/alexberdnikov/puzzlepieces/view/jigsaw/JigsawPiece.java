package com.example.alexberdnikov.puzzlepieces.view.jigsaw;

import android.graphics.Bitmap;
import com.example.alexberdnikov.puzzlepieces.view.Piece;

public class JigsawPiece extends Piece {
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

  private final int rPivotX;
  private final int rPivotY;

  private final int imageRectX;
  private final int imageRectY;

  public JigsawPiece(
      Bitmap pieceImage,
      PiecesSidesGenerator piecesSidesGenerator,
      int number,
      int pieceImageRectX,
      int pieceImageRectY,
      int rPivotX,
      int rPivotY,
      int x,
      int y) {

    super(pieceImage, number, x, y);
    this.sidesDescription = piecesSidesGenerator.getSidesDescription(number);
    this.puzzleColumnsCount = piecesSidesGenerator.getPuzzleColumnsCount();
    this.puzzleRowsCount = piecesSidesGenerator.getPuzzleRowsCount();

    this.rPivotX = rPivotX;
    this.rPivotY = rPivotY;

    this.imageRectX = pieceImageRectX;
    this.imageRectY = pieceImageRectY;

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

  @Override public int[] getPieceOffsetInPuzzle() {
    return new int[] { imageRectX, imageRectY };
  }

  int getRelativePivotX() {
    return rPivotX;
  }

  int getRelativePivotY() {
    return rPivotY;
  }

  int getPivotX() {
    return getX() + getRelativePivotX();
  }

  int getPivotY() {
    return getY() + getRelativePivotY();
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
