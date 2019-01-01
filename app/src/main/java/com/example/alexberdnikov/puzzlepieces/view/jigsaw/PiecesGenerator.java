package com.example.alexberdnikov.puzzlepieces.view.jigsaw;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.example.alexberdnikov.puzzlepieces.view.jigsaw.PiecesGenerator.PieceDescription.SIDE_BOTTOM;
import static com.example.alexberdnikov.puzzlepieces.view.jigsaw.PiecesGenerator.PieceDescription.SIDE_FORM_CONCAVE;
import static com.example.alexberdnikov.puzzlepieces.view.jigsaw.PiecesGenerator.PieceDescription.SIDE_FORM_CONVEX;
import static com.example.alexberdnikov.puzzlepieces.view.jigsaw.PiecesGenerator.PieceDescription.SIDE_FORM_FLAT;
import static com.example.alexberdnikov.puzzlepieces.view.jigsaw.PiecesGenerator.PieceDescription.SIDE_LEFT;
import static com.example.alexberdnikov.puzzlepieces.view.jigsaw.PiecesGenerator.PieceDescription.SIDE_RIGHT;
import static com.example.alexberdnikov.puzzlepieces.view.jigsaw.PiecesGenerator.PieceDescription.SIDE_TOP;


public class PiecesGenerator {
  private List<PieceDescription> pieceDescriptions;
  private final int puzzlePiecesCount;
  private final int puzzleColumnsCount;
  private final int puzzleRowsCount;

  public PiecesGenerator(int columnsCount, int rowsCount) {
    puzzleColumnsCount = columnsCount;
    puzzleRowsCount = rowsCount;
    puzzlePiecesCount = columnsCount * rowsCount;
    generatePuzzle();
  }

  private void generatePuzzle() {
    pieceDescriptions = new ArrayList<PieceDescription>(puzzlePiecesCount) {{
      for (int i = 0; i < puzzlePiecesCount; i++) {
        add(new PieceDescription());
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

      PieceDescription pieceDescription = pieceDescriptions.get(pieceNumber);
      if (pieceDescription == null) {
        pieceDescription = new PieceDescription();
      }

      if (pieceNumber % puzzleColumnsCount == 0) {
        isFirstPieceInRow = true;
      } else if (pieceNumber % puzzleColumnsCount == puzzleColumnsCount - 1) {
        isLastPieceInRow = true;
      }

      if (isFirstPieceInRow) {
        pieceDescription.pieceSidesForms[SIDE_LEFT] = SIDE_FORM_FLAT;
      } else {
        pieceDescription.pieceSidesForms[SIDE_LEFT] =
            (pieceDescriptions.get(pieceNumber - 1).getSideForm(SIDE_RIGHT) == SIDE_FORM_CONCAVE)
                ? SIDE_FORM_CONVEX : SIDE_FORM_CONCAVE;
      }

      pieceDescription.pieceSidesForms[SIDE_RIGHT] = isLastPieceInRow ? SIDE_FORM_FLAT : getRandomSideCurve();
      pieceDescriptions.set(pieceNumber, pieceDescription);
    }
  }

  private void generateTopAndBottomSides() {
    for (int pieceNumber = 0; pieceNumber < puzzlePiecesCount; pieceNumber++) {
      boolean isFirstPieceInColumn = false;
      boolean isLastPieceInColumn = false;

      PieceDescription pieceDescription = pieceDescriptions.get(pieceNumber);
      if (pieceDescription == null) {
        pieceDescription = new PieceDescription();
      }

      if (pieceNumber < puzzleColumnsCount) {
        isFirstPieceInColumn = true;
      } else if (pieceDescriptions.size() - puzzleColumnsCount <= pieceNumber) {
        isLastPieceInColumn = true;
      }

      if (isFirstPieceInColumn) {
        pieceDescription.pieceSidesForms[SIDE_TOP] = SIDE_FORM_FLAT;
      } else {
        int upperPieceBottomSide =
            pieceDescriptions.get(pieceNumber - puzzleColumnsCount).getSideForm(SIDE_BOTTOM);
        pieceDescription.pieceSidesForms[SIDE_TOP] = (upperPieceBottomSide == SIDE_FORM_CONCAVE)
            ? SIDE_FORM_CONVEX : SIDE_FORM_CONCAVE;
      }

      if (isLastPieceInColumn) {
        pieceDescription.pieceSidesForms[SIDE_BOTTOM] = SIDE_FORM_FLAT;
      } else {
        pieceDescription.pieceSidesForms[SIDE_BOTTOM] = getRandomSideCurve();
      }

      pieceDescriptions.set(pieceNumber, pieceDescription);
    }
  }

  int getPuzzleColumnsCount() {
    return puzzleColumnsCount;
  }

  int getPuzzleRowsCount() {
    return puzzleRowsCount;
  }

  public PieceDescription getPiece(int pieceNumber) {
    return pieceDescriptions.get(pieceNumber);
  }

  public int getPiecesCount() {
    return puzzlePiecesCount;
  }

  /**
   * Holds information about every particular piece of puzzle.
   */
  public static class PieceDescription {
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
