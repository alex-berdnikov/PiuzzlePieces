package com.example.alexberdnikov.puzzlepieces.view.jigsaw;

import com.example.alexberdnikov.puzzlepieces.view.Piece;
import com.example.alexberdnikov.puzzlepieces.view.PiecesGroup;
import com.example.alexberdnikov.puzzlepieces.view.PiecesPicker;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JigsawPiecesPicker extends PiecesPicker {
  private final int PIECE_HORIZONTAL_LOCK_DISTANCE_THRESHOLD_PX = 20;
  private final int PIECE_VERTICAL_LOCK_DISTANCE_THRESHOLD_PX = 15;

  JigsawPiecesPicker(List<Piece> pieces, int screenWidth, int screenHeight) {
    super(pieces, screenWidth, screenHeight);
  }

  protected JigsawPiece getCapturedPieceFromCoordinates(float touchX, float touchY) {
    for (PiecesGroup group : getPiecesGroups()) {
      for (Piece piece : group.getPieces()) {
        if (isTouchInPieceBounds(piece, touchX, touchY)) {
          return (JigsawPiece) piece;
        }
      }
    }
    return null;
  }

  @Override
  public void onMove(float x, float y) {
    super.onMove(x, y);
  }

  private boolean isTouchInPieceBounds(Piece piece, float touchX, float touchY) {
    return piece.getX() <= touchX && touchX <= piece.getX() + piece.getPieceWidth()
        && piece.getY() <= touchY && touchY <= piece.getY() + piece.getPieceHeight();
  }

  @Override protected void handlePiecesConnections(Piece draggedPiece) {
    PiecesGroup draggedPieceGroup = getGroupOfPiece(draggedPiece);
    Set<JigsawPiecesGroup> groupsToBeMerged = new HashSet<>();
    for (Piece piece : draggedPieceGroup.getPieces()) {
      groupsToBeMerged.addAll(handleLocksWithNeighbors((JigsawPiece) piece));
    }

    for (JigsawPiecesGroup group : groupsToBeMerged) {
      if (group == draggedPieceGroup) {
        continue;
      }

      draggedPieceGroup.mergeWith(group);
      getPiecesGroups().remove(group);
    }

    draggedPieceGroup.alignGroupByPiece(draggedPiece);
  }

  @Override protected JigsawPiecesGroup createPieceGroup(Piece piece) {
    return new JigsawPiecesGroup((JigsawPiece) piece);
  }

  private Set<JigsawPiecesGroup> handleLocksWithNeighbors(JigsawPiece piece) {
    Set<JigsawPiecesGroup> groupsToMerge = new HashSet<>();
    if (piece.isTopSideFree()) {
      JigsawPiece topNeighbor = (JigsawPiece) getPieceByNumber(piece.getTopNeighborNumber());
      if (isTopNeighborIsInLockDistance(piece, topNeighbor)) {
        piece.connectTopSide();
        topNeighbor.connectBottomSide();



        JigsawPiecesGroup topNeighborGroup = (JigsawPiecesGroup) getGroupOfPiece(topNeighbor);
        groupsToMerge.add(topNeighborGroup);
      }
    }

    if (piece.isBottomSideFree()) {
      JigsawPiece bottomNeighbor = (JigsawPiece) getPieceByNumber(piece.getBottomNeighborNumber());
      if (isBottomNeighborIsInLockDistance(piece, bottomNeighbor)) {
        piece.connectBottomSide();
        bottomNeighbor.connectTopSide();

        JigsawPiecesGroup bottomNeighborGroup = (JigsawPiecesGroup) getGroupOfPiece(bottomNeighbor);
        groupsToMerge.add(bottomNeighborGroup);
      }
    }

    if (piece.isLeftSideFree()) {
      JigsawPiece leftNeighbor = (JigsawPiece) getPieceByNumber(piece.getLeftNeighborNumber());
      if (isLeftNeighborIsInLockDistance(piece, leftNeighbor)) {
        piece.connectLeftSide();
        leftNeighbor.connectRightSide();

        JigsawPiecesGroup leftNeighborGroup = (JigsawPiecesGroup) getGroupOfPiece(leftNeighbor);
        groupsToMerge.add(leftNeighborGroup);
      }
    }

    if (piece.isRightSideFree()) {
      JigsawPiece rightNeighbor = (JigsawPiece) getPieceByNumber(piece.getRightNeighborNumber());
      if (isRightNeighborIsInLockDistance(piece, rightNeighbor)) {
        piece.connectRightSide();
        rightNeighbor.connectLeftSide();

        JigsawPiecesGroup rightNeighborGroup = (JigsawPiecesGroup) getGroupOfPiece(rightNeighbor);
        groupsToMerge.add(rightNeighborGroup);
      }
    }

    return groupsToMerge;
  }

  private boolean isTopNeighborIsInLockDistance(JigsawPiece piece, JigsawPiece topNeighbor) {
    return (Math.abs(piece.getLeftTopCornerX() - topNeighbor.getLeftTopCornerX())
        <= PIECE_HORIZONTAL_LOCK_DISTANCE_THRESHOLD_PX

        && (Math.abs(piece.getLeftTopCornerY() - topNeighbor.getLeftBottomCornerY())
        <= PIECE_VERTICAL_LOCK_DISTANCE_THRESHOLD_PX));
  }

  private boolean isBottomNeighborIsInLockDistance(JigsawPiece piece, JigsawPiece bottomNeighbor) {
    return (Math.abs(piece.getLeftBottomCornerX() - bottomNeighbor.getLeftTopCornerX())
        <= PIECE_HORIZONTAL_LOCK_DISTANCE_THRESHOLD_PX

        && (Math.abs(piece.getLeftBottomCornerY() - bottomNeighbor.getLeftTopCornerY())
        <= PIECE_VERTICAL_LOCK_DISTANCE_THRESHOLD_PX));
  }

  private boolean isLeftNeighborIsInLockDistance(JigsawPiece piece, JigsawPiece leftNeighbor) {
    return (Math.abs(piece.getLeftTopCornerY() - leftNeighbor.getLeftTopCornerY())
        <= PIECE_VERTICAL_LOCK_DISTANCE_THRESHOLD_PX

        && (Math.abs(piece.getLeftTopCornerX() - leftNeighbor.getRightTopCornerX())
        <= PIECE_HORIZONTAL_LOCK_DISTANCE_THRESHOLD_PX));
  }

  private boolean isRightNeighborIsInLockDistance(JigsawPiece piece, JigsawPiece rightNeighbor) {
    return (Math.abs(piece.getLeftTopCornerY() - rightNeighbor.getLeftTopCornerY())
        <= PIECE_VERTICAL_LOCK_DISTANCE_THRESHOLD_PX

        && (Math.abs(piece.getRightTopCornerX() - rightNeighbor.getLeftTopCornerX())
        <= PIECE_HORIZONTAL_LOCK_DISTANCE_THRESHOLD_PX));
  }
}