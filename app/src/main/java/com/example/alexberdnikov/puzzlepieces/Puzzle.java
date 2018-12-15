package com.example.alexberdnikov.puzzlepieces;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.example.alexberdnikov.puzzlepieces.Puzzle.Piece.SIDE_BOTTOM;
import static com.example.alexberdnikov.puzzlepieces.Puzzle.Piece.SIDE_FORM_CONCAVE;
import static com.example.alexberdnikov.puzzlepieces.Puzzle.Piece.SIDE_FORM_CONVEX;
import static com.example.alexberdnikov.puzzlepieces.Puzzle.Piece.SIDE_FORM_FLAT;
import static com.example.alexberdnikov.puzzlepieces.Puzzle.Piece.SIDE_LEFT;
import static com.example.alexberdnikov.puzzlepieces.Puzzle.Piece.SIDE_RIGHT;
import static com.example.alexberdnikov.puzzlepieces.Puzzle.Piece.SIDE_TOP;


public class Puzzle {
  private List<Piece> pieces;
  private final int puzzlePiecesCount;
  private final int puzzleColumnsCount;
  private final int puzzleRowsCount;

  public Puzzle(int columnsCount, int rowsCount) {
    puzzleColumnsCount = columnsCount;
    puzzleRowsCount = rowsCount;
    puzzlePiecesCount = columnsCount * rowsCount;
    generatePuzzle();
  }

  private void generatePuzzle() {
    pieces = new ArrayList<Piece>(puzzlePiecesCount) {{
      for (int i = 0; i < puzzlePiecesCount; i++) {
        add(new Piece());
      }
    }};

    generateLeftAndRightSides();
    generateTopAndBottomSides();
  }

  private int getRandomSideCurve() {
    return new Random().nextInt(2) + 1;
  }

  private void generateLeftAndRightSides() {
    for (int pieceNumber = 0; pieceNumber < puzzlePiecesCount; pieceNumber++) {
      boolean isFirstPieceInRow = false;
      boolean isLastPieceInRow = false;

      Piece piece = pieces.get(pieceNumber);
      if (piece == null) {
        piece = new Piece();
      }

      if (pieceNumber % puzzleColumnsCount == 0) {
        isFirstPieceInRow = true;
      } else if (pieceNumber % puzzleColumnsCount == puzzleColumnsCount - 1) {
        isLastPieceInRow = true;
      }

      if (isFirstPieceInRow) {
        piece.pieceSidesForms[SIDE_LEFT] = SIDE_FORM_FLAT;
      } else {
        piece.pieceSidesForms[SIDE_LEFT] =
            (pieces.get(pieceNumber - 1).getSideForm(SIDE_RIGHT) == SIDE_FORM_CONCAVE)
                ? SIDE_FORM_CONVEX : SIDE_FORM_CONCAVE;
      }

      piece.pieceSidesForms[SIDE_RIGHT] = isLastPieceInRow ? SIDE_FORM_FLAT : getRandomSideCurve();
      pieces.set(pieceNumber, piece);
    }
  }

  private void generateTopAndBottomSides() {
    for (int pieceNumber = 0; pieceNumber < puzzlePiecesCount; pieceNumber++) {
      boolean isFirstPieceInColumn = false;
      boolean isLastPieceInColumn = false;

      Piece piece = pieces.get(pieceNumber);
      if (piece == null) {
        piece = new Piece();
      }

      if (pieceNumber < puzzleColumnsCount) {
        isFirstPieceInColumn = true;
      } else if (pieces.size() - puzzleColumnsCount <= pieceNumber) {
        isLastPieceInColumn = true;
      }

      if (isFirstPieceInColumn) {
        piece.pieceSidesForms[SIDE_TOP] = SIDE_FORM_FLAT;
      } else {
        int upperPieceBottomSide =
            pieces.get(pieceNumber - puzzleColumnsCount).getSideForm(SIDE_BOTTOM);
        piece.pieceSidesForms[SIDE_TOP] = (upperPieceBottomSide == SIDE_FORM_CONCAVE)
            ? SIDE_FORM_CONVEX : SIDE_FORM_CONCAVE;
      }

      if (isLastPieceInColumn) {
        piece.pieceSidesForms[SIDE_BOTTOM] = SIDE_FORM_FLAT;
      } else {
        piece.pieceSidesForms[SIDE_BOTTOM] = getRandomSideCurve();
      }

      pieces.set(pieceNumber, piece);
    }
  }

  /**
   * Assigns every piece its random position on the "board".
   */
  private void explodePuzzle() {

  }

  public int getPuzzleColumnsCount() {
    return puzzleColumnsCount;
  }

  public int getPuzzleRowsCount() {
    return puzzleRowsCount;
  }

  public Piece getPiece(int pieceNumber) {
    return pieces.get(pieceNumber);
  }

  public int getPiecesCount() {
    return puzzlePiecesCount;
  }

  /**
   * Holds information about every particular piece of puzzle.
   */
  public static class Piece {
    public static final int SIDE_FORM_FLAT = 0;
    public static final int SIDE_FORM_CONCAVE = 1;
    public static final int SIDE_FORM_CONVEX = 2;

    public static final int SIDE_TOP = 0;
    public static final int SIDE_RIGHT = 1;
    public static final int SIDE_BOTTOM = 2;
    public static final int SIDE_LEFT = 3;

    private Integer[] pieceSidesForms = new Integer[4];

    public int getSideForm(int side) {
      return pieceSidesForms[side];
    }
  }
}
