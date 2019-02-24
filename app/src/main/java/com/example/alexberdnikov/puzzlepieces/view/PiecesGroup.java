package com.example.alexberdnikov.puzzlepieces.view;

import java.util.ArrayList;
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

  public synchronized List<Piece> getPieces() {
    return pieces;
  }

  public void mergeWith(PiecesGroup piecesGroup) {
    pieces.addAll(piecesGroup.pieces);
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

  abstract public int getWidth();

  abstract public int getHeight();
}
