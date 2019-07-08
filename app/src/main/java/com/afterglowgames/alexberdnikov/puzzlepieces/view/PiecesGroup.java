package com.afterglowgames.alexberdnikov.puzzlepieces.view;

import androidx.annotation.CallSuper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This class describes the group of connected pieces. Every must have at least
 * one piece in it from the beginning.
 */
abstract public class PiecesGroup {
  private List<Piece> pieces = new ArrayList<>();

  /**
   * Initializes group with one piece.
   */
  public PiecesGroup(Piece piece) {
    pieces.add(piece);
  }

  public List<Piece> getPieces() {
    return pieces;
  }

  @CallSuper
  public void mergeWith(PiecesGroup piecesGroup) {
    pieces.addAll(piecesGroup.pieces);
    Collections.sort(pieces, new Comparator<Piece>() {
      @Override public int compare(Piece piece1, Piece piece2) {
        return Integer.compare(piece1.getNumber(), piece2.getNumber());
      }
    });
  }

  boolean contains(Piece piece) {
    return pieces.contains(piece);
  }

  /**
   * Aligns all pieces in the group taking coordinates of the given Piece argument
   * calculating all other pieces coordinates according to it. Has to be implemented
   * for every type of puzzle.
   */
  abstract public void alignGroupByPiece(Piece piece);

  abstract public Piece getTopMostPiece();

  abstract public Piece getRightMostPiece();

  abstract public Piece getBottomMostPiece();

  abstract public Piece getLeftMostPiece();
}
