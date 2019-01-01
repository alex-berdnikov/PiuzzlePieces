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

  JigsawPiece(
      Bitmap pieceImage,
      SidesDescription sidesDescription,
      int puzzleColumnsCount,
      int puzzleRowsCount,
      int number,
      int x,
      int y) {

    super(pieceImage, number, x, y);
    this.sidesDescription = sidesDescription;
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
      boolean isInFirstRow = number % puzzleColumnsCount < puzzleColumnsCount;
      sideNeighbors[SidesDescription.SIDE_TOP] = isInFirstRow
          ? NEIGHBOR_NOT_AVAILABLE
          : number - puzzleColumnsCount;

      boolean isInLastRow =
          (((puzzleColumnsCount * puzzleRowsCount) - 1) - puzzleColumnsCount) < number;
      sideNeighbors[SidesDescription.SIDE_BOTTOM] = isInLastRow
          ? NEIGHBOR_NOT_AVAILABLE
          : number + puzzleColumnsCount;

      boolean isInFirstColumn = (number % puzzleColumnsCount == 0);
      sideNeighbors[SidesDescription.SIDE_RIGHT] = isInFirstColumn
          ? NEIGHBOR_NOT_AVAILABLE : number - 1;

      boolean isInLastColumn = number / puzzleColumnsCount == 1;
      sideNeighbors[SidesDescription.SIDE_RIGHT] = isInLastColumn
          ? NEIGHBOR_NOT_AVAILABLE : number + 1;
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

  public boolean isTopSideFree() {
    return sidesStatuses[SidesDescription.SIDE_TOP] == SideConnection.FREE;
  }

  public void connectTopSide() {
    if (sidesStatuses[SidesDescription.SIDE_TOP] == SideConnection.FREE) {
      sidesStatuses[SidesDescription.SIDE_TOP] = SideConnection.CONNECTED;
    }
  }

  public boolean isRightSideFree() {
    return sidesStatuses[SidesDescription.SIDE_RIGHT] == SideConnection.FREE;
  }

  public void connectRightSide() {
    if (sidesStatuses[SidesDescription.SIDE_RIGHT] == SideConnection.FREE) {
      sidesStatuses[SidesDescription.SIDE_RIGHT] = SideConnection.CONNECTED;
    }
  }

  public boolean isBottomSideFree() {
    return sidesStatuses[SidesDescription.SIDE_BOTTOM] == SideConnection.FREE;
  }

  public void connectBottomSide() {
    if (sidesStatuses[SidesDescription.SIDE_BOTTOM] == SideConnection.FREE) {
      sidesStatuses[SidesDescription.SIDE_BOTTOM] = SideConnection.CONNECTED;
    }
  }

  public boolean isLeftSideFree() {
    return sidesStatuses[SidesDescription.SIDE_LEFT] == SideConnection.FREE;
  }

  public void connectLeftSide() {
    if (sidesStatuses[SidesDescription.SIDE_LEFT] == SideConnection.FREE) {
      sidesStatuses[SidesDescription.SIDE_LEFT] = SideConnection.CONNECTED;
    }
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
